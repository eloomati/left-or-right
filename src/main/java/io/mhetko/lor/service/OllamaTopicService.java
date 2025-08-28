package io.mhetko.lor.service;

import io.mhetko.lor.dto.ProposedTopicDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OllamaTopicService {

    private final AiModelService aiModelService;
    private final ProposedTopicService proposedTopicService;

    private static final Long SYSTEM_USER_ID = 10L;

    public ProposedTopicDTO generateAndSaveTopic() {
        // 1. Wygeneruj temat przez Ollama
        ProposedTopicDTO dto = aiModelService.generateTopicOllama();
        dto.setProposedById(SYSTEM_USER_ID);

        // 2. Zapisz temat
        return proposedTopicService.create(dto);
    }
}