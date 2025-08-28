package io.mhetko.lor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.dto.NewsHeadlineDTO;
import io.mhetko.lor.repository.CategoryRepository;
import io.mhetko.lor.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiModelService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.model.name}")
    private String huggingFaceModelName;

    @Value("${ai.prompt.qwen2.path:classpath:prompts/prompt_qwen2.txt}")
    private String promptQwen2Path;

    @Value("${ai.prompt.eng.path:classpath:prompts/prompt_eng.txt}")
    private String promptEngPath;

    @Value("${ai.prompt.news.path:classpath:prompts/prompt_news.txt}")
    private String promptNewsPath;

    private final WebClient openAiWebClient;
    private final WebClient huggingFaceWebClient;
    private final WebClient ollamaWebClient;
    private final ResourceLoader resourceLoader;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String readPromptTemplate(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private List<Map<String, Object>> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(cat -> Map.<String, Object>of("id", cat.getId(), "name", cat.getName()))
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getTags() {
        return tagRepository.findAll()
                .stream()
                .map(tag -> Map.<String, Object>of("id", tag.getId(), "name", tag.getName()))
                .collect(Collectors.toList());
    }

    private String preparePromptWithCategoriesAndTags() {
        try {
            String template = readPromptTemplate(promptEngPath);
            String categoriesJson = objectMapper.writeValueAsString(getCategories());
            String tagsJson = objectMapper.writeValueAsString(getTags());
            template = template.replace("{{CATEGORIES}}", categoriesJson)
                    .replace("{{TAGS}}", tagsJson);
            log.info("Generated prompt: \n{}", template);
            return template;
        } catch (Exception e) {
            log.error("Error loading prompt from file", e);
            return "Generate an interesting, neutral topic for online discussion.";
        }
    }

    private String preparePromptWithRandomCategory() {
        try {
            String template = readPromptTemplate(promptEngPath);
            List<Map<String, Object>> categories = getCategories();
            if (categories.isEmpty()) throw new IllegalStateException("Brak kategorii w bazie");
            Map<String, Object> randomCategory = categories.get(new Random().nextInt(categories.size()));
            String categoriesJson = objectMapper.writeValueAsString(List.of(randomCategory));
            template = template.replace("{{CATEGORIES}}", categoriesJson);
            log.info("Generated prompt (random category): \n{}", template);
            return template;
        } catch (Exception e) {
            log.error("Error loading prompt from file", e);
            return "Generate an interesting, neutral topic for online discussion.";
        }
    }

    public ProposedTopicDTO generateTopicGPT() {
        String prompt = preparePromptWithCategoriesAndTags();
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 300
        );
        try {
            Map response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + System.getenv("GPT_KEY"))
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            String json = extractGptContent(response);
            return objectMapper.readValue(json, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by GPT", e);
            throw new RuntimeException("Unable to generate topic via GPT", e);
        }
    }

    private String extractGptContent(Map response) {
        List choices = (List) response.get("choices");
        if (choices == null || choices.isEmpty()) throw new IllegalStateException("Brak odpowiedzi z GPT");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");
        return (String) message.get("content");
    }

    public ProposedTopicDTO generateTopicHF() {
        String prompt = preparePromptWithCategoriesAndTags();
        Map<String, Object> requestBody = Map.of("inputs", prompt);
        try {
            String response = huggingFaceWebClient.post()
                    .uri("/models/" + huggingFaceModelName)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            String json = extractHfContent(response);
            return objectMapper.readValue(json, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by HuggingFace", e);
            throw new RuntimeException("Failed to generate topic via HuggingFace", e);
        }
    }

    private String extractHfContent(String response) throws JsonProcessingException {
        JsonNode arr = objectMapper.readTree(response);
        return arr.get(0).get("generated_text").asText();
    }

    public ProposedTopicDTO generateTopicOllama() {
        String prompt = preparePromptWithRandomCategory();
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
            String fullJson = extractOllamaContent(response);
            log.info("JSON po przekształceniu: {}", fullJson);
            return objectMapper.readValue(fullJson, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by Ollama", e);
            throw new RuntimeException("Failed to generate topic via Ollama", e);
        }
    }

    public ProposedTopicDTO generateTopicOllamaWithNews(String prompt) {
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
            String fullJson = extractOllamaContent(response);
            log.info("JSON po przekształceniu: {}", fullJson);
            return objectMapper.readValue(fullJson, ProposedTopicDTO.class);
        } catch (Exception e) {
            log.error("Error while generating a topic by Ollama (with prompt)", e);
            throw new RuntimeException("Failed to generate topic via Ollama (with prompt)", e);
        }
    }

    private String extractOllamaContent(String response) {
        return Arrays.stream(response.split("\n"))
                .map(line -> {
                    try {
                        return objectMapper.readTree(line).get("response").asText();
                    } catch (Exception e) {
                        return "";
                    }
                })
                .collect(Collectors.joining());
    }

    public String buildPromptWithNews(List<NewsHeadlineDTO> headlines) {
        try {
            String template = readPromptTemplate(promptNewsPath);
            StringBuilder newsSection = new StringBuilder();
            for (NewsHeadlineDTO news : headlines) {
                newsSection.append("- Tytuł: ").append(news.getTitle());
                if (news.getDescription() != null && !news.getDescription().isBlank()) {
                    newsSection.append(" | Opis: ").append(news.getDescription());
                }
                newsSection.append("\n");
            }
            String prompt = template.replace("{{NEWS_LIST}}", newsSection.toString().trim());
            log.info("Prompt do generowania tematu z newsów:\n{}", prompt);
            return prompt;
        } catch (Exception e) {
            log.error("Błąd wczytywania promptu z newsami", e);
            throw new RuntimeException("Błąd wczytywania promptu z newsami", e);
        }
    }
}