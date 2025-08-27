package io.mhetko.lor.controller;

import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/popular")
    public Page<TopicDTO> getPopularTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return topicService.getAllTopicsSortedByPopularity(PageRequest.of(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        topicService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}