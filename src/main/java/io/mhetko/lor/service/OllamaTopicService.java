package io.mhetko.lor.service;

import io.mhetko.lor.dto.NewsHeadlineDTO;
import io.mhetko.lor.dto.ProposedTopicDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OllamaTopicService {

    private final AiModelService aiModelService;
    private final ProposedTopicService proposedTopicService;
    private final NewsApiService newsApiService;

    private static final Long SYSTEM_USER_ID = 10L;

    public ProposedTopicDTO generateAndSaveTopic() {
        ProposedTopicDTO dto = aiModelService.generateTopicOllama();
        dto.setProposedById(SYSTEM_USER_ID);
        return proposedTopicService.create(dto);
    }

    public ProposedTopicDTO generateAndSaveTopicFromNews() {
        List<NewsHeadlineDTO> headlines = newsApiService.fetchNewsHeadlinesWithDescription();
        String prompt = aiModelService.buildPromptWithNews(headlines);
        ProposedTopicDTO dto = aiModelService.generateTopicOllamaWithNews(prompt);
        dto.setProposedById(SYSTEM_USER_ID);
        return proposedTopicService.create(dto);
    }

    public ProposedTopicDTO generateAndSaveTopicFromSingleNews(int newsIndex) {
        List<NewsHeadlineDTO> headlines = newsApiService.fetchNewsHeadlinesWithDescription();
        if (headlines.isEmpty() || newsIndex < 0 || newsIndex >= headlines.size()) {
            throw new IllegalArgumentException("Brak newsa o podanym indeksie");
        }
        NewsHeadlineDTO singleNews = headlines.get(newsIndex);
        String prompt = aiModelService.buildPromptWithNews(List.of(singleNews));
        ProposedTopicDTO dto = aiModelService.generateTopicOllamaWithNews(prompt);
        dto.setProposedById(SYSTEM_USER_ID);
        return proposedTopicService.create(dto);
    }
}