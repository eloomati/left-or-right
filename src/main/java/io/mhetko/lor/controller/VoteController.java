package io.mhetko.lor.controller;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.dto.VoteDTO;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/vote")
    @Operation(
            summary = "Cast a vote",
            description = "Allows a user to cast a vote for a topic on a given side.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote cast successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Void> vote(@RequestParam Long userId,
                                     @RequestParam Long topicId,
                                     @RequestParam Side side) {
        voteService.vote(userId, topicId, side);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unvote")
    @Operation(
            summary = "Withdraw a vote",
            description = "Allows a user to withdraw their vote from a topic.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote withdrawn successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Void> unvote(@RequestParam Long userId,
                                       @RequestParam Long topicId) {
        voteService.unvote(userId, topicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    @Operation(
            summary = "Get vote count",
            description = "Returns the total number of votes for a topic.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vote count returned",
                    content = @Content(schema = @Schema(implementation = VoteCountDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    public ResponseEntity<VoteCountDTO> getVoteCount(@RequestParam Long topicId) {
        return ResponseEntity.ok(voteService.getVoteCount(topicId));
    }

    @GetMapping("/side-count")
    @Operation(
            summary = "Get vote count by side",
            description = "Returns the number of votes for a topic on a specific side.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote count by side returned"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    public ResponseEntity<Integer> countVotesBySide(@RequestParam Long topicId,
                                                    @RequestParam Side side) {
        return ResponseEntity.ok(voteService.countVotesBySide(topicId, side));
    }

    @GetMapping("/user-votes")
    @Operation(
            summary = "Get user votes",
            description = "Returns a list of votes cast by a specific user.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User votes returned")
    })
    public List<VoteDTO> getUserVotes(@RequestParam Long userId) {
        return voteService.getUserVotesDto(userId);
    }

    @DeleteMapping("/user/{userId}/all")
    @Operation(
            summary = "Delete all user votes",
            description = "Deletes all votes cast by a specific user.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All user votes deleted",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    public ResponseEntity<Map<String, Object>> removeAllVotesByUser(@PathVariable Long userId) {
        voteService.removeAllVotesByUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All user votes have been deleted");
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(
            summary = "Get most popular topics",
            description = "Returns a list of the most popular topic IDs by vote count.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of most popular topics returned",
                    content = @Content(schema = @Schema(implementation = Long.class))
            )
    })
    public ResponseEntity<List<Long>> getMostPopularTopics(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(voteService.getMostPopularTopics(limit));
    }

    @PutMapping("/update")
    @Operation(
            summary = "Update user vote",
            description = "Changes the side of a user's vote for a topic.",
            tags = {"Vote"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User vote updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Void> updateUserVote(@RequestParam Long userId,
                                               @RequestParam Long topicId,
                                               @RequestParam Side newSide) {
        voteService.updateUserVote(userId, topicId, newSide);
        return ResponseEntity.ok().build();
    }
}