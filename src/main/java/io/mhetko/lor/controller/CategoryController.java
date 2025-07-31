package io.mhetko.lor.controller;

import io.mhetko.lor.dto.CategoryDTO;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a category",
            description = "Creates a new category based on the provided data.",
            tags = {"Category"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Returns a list of all categories.",
            tags = {"Category"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of categories",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            )
    })
    public List<CategoryDTO> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by ID",
            description = "Returns a category by its ID.",
            tags = {"Category"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update category",
            description = "Updates the category with the given ID.",
            tags = {"Category"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable("id") Long id,
                                                      @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete category",
            description = "Deletes the category with the given ID.",
            tags = {"Category"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category deleted",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category has been deleted");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}