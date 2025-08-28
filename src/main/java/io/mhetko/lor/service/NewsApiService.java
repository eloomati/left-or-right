package io.mhetko.lor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mhetko.lor.dto.NewsHeadlineDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NewsApiService {

    @Value("${newsapi.key}")
    private String newsApiKey;
    @Value("${newsapi.country}")
    private String newsApiCountry;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NewsApiService(@Qualifier("newsApiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<NewsHeadlineDTO> fetchNewsHeadlinesWithDescription() {
        String url = "?country=" + newsApiCountry + "&apiKey=" + newsApiKey;
        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            JsonNode root = objectMapper.readTree(response);
            List<NewsHeadlineDTO> result = new ArrayList<>();
            int idx = 0;
            for (JsonNode article : root.get("articles")) {
                String title = article.get("title").asText("");
                String description = article.get("description").asText("");
                result.add(new NewsHeadlineDTO(idx++, title, description));
            }
            return result;
        } catch (Exception e) {
            log.error("Błąd pobierania newsów", e);
            return List.of();
        }
    }
}