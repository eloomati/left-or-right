package io.mhetko.lor.controller;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.service.ChatGptTopicService;
import io.mhetko.lor.service.HuggingFaceTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ChatGptController {

    private final ChatGptTopicService chatGptTopicService;
    private final HuggingFaceTopicService huggingFaceTopicService;

    @PostMapping("/generate/gpt")
    public ProposedTopicDTO generateTopicGPT() {
        return chatGptTopicService.generateAndSaveTopic();
    }

    @PostMapping("/generate/hf")
    public ProposedTopicDTO generateTopicHF() {
        return huggingFaceTopicService.generateAndSaveTopic();
    }
}

