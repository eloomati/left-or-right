package io.mhetko.lor.service;

import io.mhetko.lor.dto.ProposedTopicDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ChatGptTopicService {

    private final AiModelService aiModelService;
    private final ProposedTopicService proposedTopicService;

    // ID systemowego użytkownika (np. ChatGPT)
    private static final Long SYSTEM_USER_ID = 10L;

    public ProposedTopicDTO generateAndSaveTopic() {
        // 1. Wygeneruj temat z ChatGPT
        ProposedTopicDTO dto = aiModelService.generateTopicGPT();
        dto.setProposedById(SYSTEM_USER_ID);

        // 2. Zapisz temat przez serwis, który waliduje i zwraca DTO
        return proposedTopicService.create(dto);
    }
}
