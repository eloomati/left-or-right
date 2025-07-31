package io.mhetko.lor.service;

import io.mhetko.lor.dto.CategoryDTO;
import io.mhetko.lor.entity.Category;
import io.mhetko.lor.mapper.CategoryMapper;
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
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> getAllCategories() {
        log.info("Fetching all active categories");
        return categoryRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public Optional<CategoryDTO> getCategoryById(Long id) {
        log.info("Fetching category with id {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        log.info("Creating category: {}", categoryDTO.getName());
        if (categoryRepository.findByNameAndDeletedAtIsNull(categoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setCreatedAt(LocalDateTime.now());
        Category saved = categoryRepository.save(category);
        log.info("Category '{}' created successfully", saved.getName());
        return categoryMapper.toDto(saved);
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        categoryRepository.softDeleteById(id, LocalDateTime.now());
        log.info("Category with ID '{}' deleted successfully", id);
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO){
        log.info("Updating category with ID: {}", id);
        Optional<Category> maybeCategory = categoryRepository.findById(id);
        if(maybeCategory.isPresent()){
            Category categoryUpdate = maybeCategory.get();
            categoryUpdate.setName(categoryDTO.getName());
            Category saved = categoryRepository.save(categoryUpdate);
            log.info("Category '{}' updated successfully", saved.getName());
            return categoryMapper.toDto(saved);
        }
        throw new IllegalArgumentException("Category with ID '"+id+"' not found");
    }
}