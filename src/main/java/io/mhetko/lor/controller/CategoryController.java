package io.mhetko.lor.controller;

import io.mhetko.lor.entity.Category;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public Category createCategory(@RequestBody  Category category){
        return categoryService.createCategory(category);
    }

    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id,
                                                  @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Kategoria została usunięta");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }


}
