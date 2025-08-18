package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.CreateTopicRequestDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isArchive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "continent", ignore = true)
    Topic toEntity(CreateTopicRequestDTO dto);

    @Mapping(target = "category", ignore = true)
    TopicDTO toDto(Topic topic);
}
