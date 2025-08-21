package io.mhetko.lor.controller;

import io.mhetko.lor.dto.WatchedTopicDTO;
import io.mhetko.lor.mapper.WatchedProposedTopicMapper;
import io.mhetko.lor.repository.ProposedTopicRepository;
import io.mhetko.lor.service.TopicWatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topics")
public class TopicWatchController {

    private final TopicWatchService topicWatchService;
    private final ProposedTopicRepository proposedTopicRepository;
    private final WatchedProposedTopicMapper watchedProposedTopicMapper;

    @PostMapping("/{topicId}/watch")
    @Operation(
            summary = "Watch a topic",
            description = "Allows the user to add a topic to their watchlist.",
            tags = {"TopicWatch"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic added to watchlist"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    public ResponseEntity<Void> watchTopic(@PathVariable Long topicId) {
        topicWatchService.watchTopic(topicId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/proposed/{proposedTopicId}/watch")
    @Operation(
            summary = "Watch a proposed topic",
            description = "Allows the user to watch a proposed topic.",
            tags = {"TopicWatch"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proposed topic added to watchlist"),
            @ApiResponse(responseCode = "404", description = "Proposed topic not found")
    })
    public ResponseEntity<Void> watchProposedTopic(@PathVariable Long proposedTopicId) {
        topicWatchService.watchProposedTopic(proposedTopicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/watched")
    @Operation(
            summary = "Get watched topics",
            description = "Returns a list of topics watched by the current user.",
            tags = {"TopicWatch"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of watched topics",
                    content = @Content(schema = @Schema(implementation = WatchedTopicDTO.class))
            )
    })
    public ResponseEntity<List<WatchedTopicDTO>> getWatchedTopics() {
        var topics = topicWatchService.getWatchedTopicsDtoForCurrentUser();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/proposed-topics")
    public List<WatchedTopicDTO> getProposedTopics() {
        return proposedTopicRepository.findAll().stream()
                .map(watchedProposedTopicMapper::toDto)
                .toList();
    }

    @DeleteMapping("/{topicId}/watch")
    public ResponseEntity<Void> unfollowTopic(@PathVariable Long topicId) {
        topicWatchService.unwatchTopic(topicId);
        return ResponseEntity.ok().build();
    }
}