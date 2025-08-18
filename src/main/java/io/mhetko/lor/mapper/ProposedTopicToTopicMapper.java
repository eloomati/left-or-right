package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.entity.ProposedTopic;
import io.mhetko.lor.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = io.mhetko.lor.entity.enums.TopicStatus.class)
public interface ProposedTopicToTopicMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(TopicStatus.NEW)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "popularityScore", expression = "java(proposedTopic.getPopularityScore() != null ? proposedTopic.getPopularityScore() : 0)")
    Topic toTopic(ProposedTopic proposedTopic);

    @Mapping(target = "proposedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "popularityScore", expression = "java(dto.getPopularityScore() != null ? dto.getPopularityScore() : 0)")
    ProposedTopic toEntity(ProposedTopicDTO dto);
}