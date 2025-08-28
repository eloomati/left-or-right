package io.mhetko.lor.controller;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.service.ChatGptTopicService;
import io.mhetko.lor.service.HuggingFaceTopicService;
import io.mhetko.lor.service.OllamaTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelsController {

    private final ChatGptTopicService chatGptTopicService;
    private final HuggingFaceTopicService huggingFaceTopicService;
    private final OllamaTopicService ollamaTopicService;

    @PostMapping("/generate/gpt")
    public ProposedTopicDTO generateTopicGPT() {
        return chatGptTopicService.generateAndSaveTopic();
    }

    @PostMapping("/generate/hf")
    public ProposedTopicDTO generateTopicHF() {
        return huggingFaceTopicService.generateAndSaveTopic();
    }

    @PostMapping("/generate/ollama")
    public ProposedTopicDTO generateTopicOllama() {
        return ollamaTopicService.generateAndSaveTopic();
    }
}

