package io.mhetko.lor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.repository.CategoryRepository;
import io.mhetko.lor.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiModelService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.model.name}")
    private String huggingFaceModelName;

    private final WebClient openAiWebClient;
    private final WebClient huggingFaceWebClient;
    private final WebClient ollamaWebClient;
    private final ResourceLoader resourceLoader;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    private String loadPrompt() {
        try {
            Resource resource = resourceLoader.getResource("classpath:prompts/prompt_qwen2.txt");
            try (InputStream is = resource.getInputStream()) {
                String promptTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                // Pobierz kategorie i tagi jako obiekty {id, name}
                var categories = categoryRepository.findAll()
                        .stream()
                        .map(cat -> Map.of("id", cat.getId(), "name", cat.getName()))
                        .collect(Collectors.toList());
                var tags = tagRepository.findAll()
                        .stream()
                        .map(tag -> Map.of("id", tag.getId(), "name", tag.getName()))
                        .collect(Collectors.toList());

                ObjectMapper mapper = new ObjectMapper();
                String categoriesJson = mapper.writeValueAsString(categories);
                String tagsJson = mapper.writeValueAsString(tags);

                promptTemplate = promptTemplate.replace("{{CATEGORIES}}", categoriesJson);
                promptTemplate = promptTemplate.replace("{{TAGS}}", tagsJson);

                log.info("Generated prompt: \n{}", promptTemplate);

                return promptTemplate;
            }
        } catch (Exception e) {
            log.error("Error loading prompt from file", e);
            return "Generate an interesting, neutral topic for online discussion.";
        }
    }

    private String loadPromptWithRandomCategory() {
        try {
            Resource resource = resourceLoader.getResource("classpath:prompts/prompt_eng.txt");
            try (InputStream is = resource.getInputStream()) {
                String promptTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                var categories = categoryRepository.findAll()
                        .stream()
                        .map(cat -> Map.of("id", cat.getId(), "name", cat.getName()))
                        .collect(Collectors.toList());

                if (categories.isEmpty()) {
                    throw new IllegalStateException("Brak kategorii w bazie");
                }

                // Wybierz jedną losową kategorię
                var randomCategory = categories.get((int) (Math.random() * categories.size()));

                ObjectMapper mapper = new ObjectMapper();
                String categoriesJson = mapper.writeValueAsString(List.of(randomCategory));

                promptTemplate = promptTemplate.replace("{{CATEGORIES}}", categoriesJson);

                log.info("Generated prompt (random category): \n{}", promptTemplate);

                return promptTemplate;
            }
        } catch (Exception e) {
            log.error("Error loading prompt from file", e);
            return "Generate an interesting, neutral topic for online discussion.";
        }
    }

    public ProposedTopicDTO generateTopicGPT() {
        String prompt = loadPrompt();

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 300
        );

        Map response = openAiWebClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + System.getenv("GPT_KEY"))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List choices = (List) response.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");
        String json = (String) message.get("content");

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, ProposedTopicDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse ChatGPT's response", e);
        }
    }

    public ProposedTopicDTO generateTopicHF() {
        String prompt = loadPrompt();
        Map<String, Object> requestBody = Map.of("inputs", prompt);

        try {
            String response = huggingFaceWebClient.post()
                    .uri("/models/" + huggingFaceModelName)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode arr = mapper.readTree(response);
            String json = arr.get(0).get("generated_text").asText();

            return mapper.readValue(json, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by HuggingFace", e);
            throw new RuntimeException("Failed to generate topic via HuggingFace", e);
        }
    }

    public ProposedTopicDTO generateTopicOllama() {
        String prompt = loadPromptWithRandomCategory();
        Map<String, Object> requestBody = Map.of(
                "model", "qwen2.5",
                "prompt", prompt
        );

        try {
            String response = ollamaWebClient.post()
                    .uri("/api/generate")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Odpowiedź z Ollama: {}", response);

            ObjectMapper mapper = new ObjectMapper();
            String fullJson = Arrays.stream(response.split("\n"))
                    .map(line -> {
                        try {
                            return mapper.readTree(line).get("response").asText();
                        } catch (Exception e) {
                            return "";
                        }
                    })
                    .collect(Collectors.joining());

            log.info("JSON po przekształceniu: {}", fullJson);

            return mapper.readValue(fullJson, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by Ollama", e);
            throw new RuntimeException("Failed to generate topic via Ollama", e);
        }
    }
}