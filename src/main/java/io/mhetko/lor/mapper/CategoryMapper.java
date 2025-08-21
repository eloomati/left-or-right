package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.CategoryDTO;
import io.mhetko.lor.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    Category toEntity(CategoryDTO dto);

    CategoryDTO toDto(Category category);
}