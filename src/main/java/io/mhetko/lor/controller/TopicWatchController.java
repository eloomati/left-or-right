package io.mhetko.lor.controller;

import io.mhetko.lor.service.TopicWatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topics")
public class TopicWatchController {

    private final TopicWatchService topicWatchService;

    @PostMapping("/{topicId}/watch")
    public ResponseEntity<Void> watchTopic(@PathVariable Long topicId) {
        topicWatchService.watchTopic(topicId);
        return ResponseEntity.ok().build();
    }
}
