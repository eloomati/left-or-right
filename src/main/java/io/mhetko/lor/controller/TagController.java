package io.mhetko.lor.controller;

import io.mhetko.lor.dto.TagDTO;
import io.mhetko.lor.entity.Tag;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a tag",
            description = "Creates a new tag based on the provided data.",
            tags = {"Tag"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag created successfully",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.createTag(tagDTO));
    }

    @GetMapping
    @Operation(
            summary = "Get all tags",
            description = "Returns a list of all tags.",
            tags = {"Tag"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tags",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))
            )
    })
    public List<TagDTO> getAllTags(){
        return tagService.getAllTags();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get tag by ID",
            description = "Returns a tag by its ID.",
            tags = {"Tag"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag found",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found"
            )
    })
    public ResponseEntity<TagDTO> getTagById(@PathVariable("id") Long id) {
        return tagService.getTagById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update tag",
            description = "Updates the tag with the given ID.",
            tags = {"Tag"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag updated",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found"
            )
    })
    public ResponseEntity<TagDTO> updateTag(@PathVariable("id") Long id,
                                            @RequestBody TagDTO tagDTO) {
        return ResponseEntity.ok(tagService.updateTag(id, tagDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete tag",
            description = "Deletes the tag with the given ID.",
            tags = {"Tag"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag deleted",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found"
            )
    })
    public ResponseEntity<Map<String, Object>> deleteTag(@PathVariable("id") Long id) {
        tagService.deleteTag(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tag has been deleted");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}