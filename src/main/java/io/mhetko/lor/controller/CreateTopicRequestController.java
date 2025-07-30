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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/topic-requests")
@RequiredArgsConstructor
public class CreateTopicRequestController {

    private final TopicService topicService;

    @PostMapping
    @Operation(
            summary = "Create a new topic",
            description = "Creates a new topic based on the provided data.",
            tags = {"Topic"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Topic successfully created",
                    content = @Content(schema = @Schema(implementation = Topic.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody CreateTopicRequestDTO dto) {
        Topic created = topicService.createTopic(dto);
        return ResponseEntity.ok(created);
    }
}
