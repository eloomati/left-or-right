package io.mhetko.lor.controller;

import io.mhetko.lor.dto.CreateTopicRequestDTO;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topic-requests")
@RequiredArgsConstructor
public class CreateTopicRequestController {

    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody CreateTopicRequestDTO dto) {
        Topic created = topicService.createTopic(dto);
        return ResponseEntity.ok(created);
    }
}
