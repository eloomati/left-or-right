package io.mhetko.lor.controller;

import io.mhetko.lor.dto.CommentDTO;
import io.mhetko.lor.dto.CreateCommentRequestDTO;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.mhetko.lor.entity.enums.Side;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(
            summary = "Create a comment",
            description = "Creates a new comment based on the provided data.",
            tags = {"Comment"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment created successfully",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<CommentDTO> createComment(@RequestBody CreateCommentRequestDTO createCommentRequestDTO){
        return ResponseEntity.ok(commentService.createComment(createCommentRequestDTO));
    }

    @GetMapping
    @Operation(
            summary = "Get all comments",
            description = "Returns a list of all comments.",
            tags = {"Comment"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of comments",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            )
    })
    public List<CommentDTO> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/topic/{topicId}")
    @Operation(
            summary = "Get comments by topic ID",
            description = "Returns a list of comments for the given topic ID.",
            tags = {"Comment"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of comments for topic",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            )
    })
    public List<CommentDTO> getAllCommentsByTopicId(@PathVariable("topicId") Long topicId) {
        return commentService.getCommentsByTopicId(topicId);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get comment by ID",
            description = "Returns a comment by its ID.",
            tags = {"Comment"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment found",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable("id") Long id){
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update comment",
            description = "Updates the comment with the given ID.",
            tags = {"Comment"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment updated",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    public ResponseEntity<CommentDTO> updateComment(@PathVariable("id") Long id,
                                                    @RequestBody CreateCommentRequestDTO createCommentRequestDTO) {
        return ResponseEntity.ok(commentService.updateComment(id, createCommentRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete comment",
            description = "Deletes the comment with the given ID.",
            tags = {"Comment"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment deleted",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found"
            )
    })
    public ResponseEntity<Map<String, Object>> deleteCommment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment deleted successfully");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-topic-and-side")
    public List<CommentDTO> getCommentsByTopicAndSide(
            @RequestParam Long topicId,
            @RequestParam Side side
    ) {
        return commentService.getCommentsByTopicAndSide(topicId, side);
    }
}