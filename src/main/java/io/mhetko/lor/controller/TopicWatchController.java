package io.mhetko.lor.controller;

import io.mhetko.lor.dto.WatchedTopicDTO;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.service.TopicWatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/proposed/{proposedTopicId}/watch")
    public ResponseEntity<Void> watchProposedTopic(@PathVariable Long proposedTopicId) {
        topicWatchService.watchProposedTopic(proposedTopicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/watched")
    public ResponseEntity<List<WatchedTopicDTO>> getWatchedTopics() {
        var topics = topicWatchService.getWatchedTopicsDtoForCurrentUser();
        return ResponseEntity.ok(topics);
    }
}
