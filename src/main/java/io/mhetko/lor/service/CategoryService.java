package io.mhetko.lor.service;

import io.mhetko.lor.entity.Category;
import io.mhetko.lor.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        log.info("Fetching all active categories");
        return categoryRepository.findAllByDeletedAtIsNull();
    }

    public Optional<Category> getCategoryById(Long id) {
        log.info("Fetching category with id {}", id);
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category){
        log.info("Creating category: {}", category.getName());
        if (categoryRepository.findByNameAndDeletedAtIsNull(category.getName()).isPresent()) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
        category.setCreatedAt(LocalDateTime.now());
        log.info("Category '{}' created successfully", category.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        categoryRepository.softDeleteById(id, LocalDateTime.now());
        log.info("Category with ID '{}' deleted successfully", id);
    }

    public Category updateCategory(Long id, Category category){
        Optional<Category> maybeCategory = categoryRepository.findById(id);
        log.info("Updating category with ID: {}", id);
        if(maybeCategory.isPresent()){
            Category categoryUpdate = maybeCategory.get();
            categoryUpdate.setName(category.getName());
            log.info("Category '{}' updated successfully", category.getName());
            return categoryRepository.save(categoryUpdate);
        }

        throw new IllegalArgumentException("Category with ID '"+category.getId()+"' not found");
    }
}
