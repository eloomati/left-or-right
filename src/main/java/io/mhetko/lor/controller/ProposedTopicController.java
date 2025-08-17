package io.mhetko.lor.controller;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.service.ProposedTopicService;
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
@RequestMapping("/api/proposed-topics")
@RequiredArgsConstructor
public class ProposedTopicController {

    private final ProposedTopicService proposedTopicService;

    @GetMapping
    @Operation(
            summary = "Get all proposed topics",
            description = "Returns a list of all proposed topics.",
            tags = {"ProposedTopic"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of proposed topics",
                    content = @Content(schema = @Schema(implementation = ProposedTopicDTO.class))
            )
    })
    public List<ProposedTopicDTO> getAll() {
        return proposedTopicService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get proposed topic by ID",
            description = "Returns details of a proposed topic by its ID.",
            tags = {"ProposedTopic"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Proposed topic",
                    content = @Content(schema = @Schema(implementation = ProposedTopicDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Proposed topic not found"
            )
    })
    public ProposedTopicDTO getById(@PathVariable Long id) {
        return proposedTopicService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create a new proposed topic",
            description = "Creates a new proposed topic based on the provided data.",
            tags = {"ProposedTopic"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Proposed topic created",
                    content = @Content(schema = @Schema(implementation = ProposedTopicDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ProposedTopicDTO create(@RequestBody ProposedTopicDTO dto) {
        return proposedTopicService.create(dto);
    }

    @PostMapping("/{id}/move-to-topic")
    @Operation(
            summary = "Move proposed topic to topics",
            description = "Creates a new topic based on the proposal and marks it as deleted (soft delete).",
            tags = {"ProposedTopic"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Proposed topic moved to topics",
                    content = @Content(schema = @Schema(implementation = TopicDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Proposed topic not found"
            )
    })
    public TopicDTO moveToTopic(@PathVariable Long id) {
        return proposedTopicService.moveToTopic(id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete proposed topic",
            description = "Marks the proposed topic as deleted (soft delete).",
            tags = {"ProposedTopic"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Proposed topic marked as deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Proposed topic not found"
            )
    })
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        proposedTopicService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}