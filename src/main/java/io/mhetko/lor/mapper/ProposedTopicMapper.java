package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.entity.ProposedTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProposedTopicMapper {

    @Mapping(target = "proposedById", source = "proposedBy.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "popularityScore", source = "popularityScore")
    ProposedTopicDTO toDto(ProposedTopic entity);

    @Mapping(target = "proposedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "popularityScore", expression = "java(dto.getPopularityScore() != null ? dto.getPopularityScore() : 0)")
    ProposedTopic toEntity(ProposedTopicDTO dto);
}
