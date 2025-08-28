package io.mhetko.lor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.repository.CategoryRepository;
import io.mhetko.lor.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;


import java.nio.file.Files;
import java.util.List;
import java.util.Map;

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
            Resource resource = resourceLoader.getResource("classpath:prompts/topic.txt");
            String promptTemplate = Files.readString(resource.getFile().toPath());

            // Pobierz kategorie i tagi z bazy
            List<String> categories = categoryRepository.findAllNames();
            List<String> tags = tagRepository.findAllNames();

            // Wstaw do promptu (np. przez placeholdery)
            promptTemplate = promptTemplate.replace("{{CATEGORIES}}", categories.toString());
            promptTemplate = promptTemplate.replace("{{TAGS}}", tags.toString());

            log.info("Generated prompt: \n{}", promptTemplate);

            return promptTemplate;
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
        String prompt = loadPrompt();
        Map<String, Object> requestBody = Map.of(
                "model", "qwen2:0.5b",
                "prompt", prompt
        );

        try {
            String response = ollamaWebClient.post()
                    .uri("/api/generate")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Odpowiedź Ollama to JSON z polem "response"
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            String content = node.get("response").asText();

            return mapper.readValue(content, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by Ollama", e);
            throw new RuntimeException("Failed to generate topic via Ollama", e);
        }
    }
}

