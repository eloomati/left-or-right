package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.TagDTO;
import io.mhetko.lor.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "topics", ignore = true)
    Tag toEntity(TagDTO dto);

    TagDTO toDto(Tag tag);
}
