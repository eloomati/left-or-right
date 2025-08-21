package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.entity.ProposedTopic;
import io.mhetko.lor.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProposedTopicToTopicMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "description", target = "description")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isArchive", ignore = true)
    Topic toEntity(ProposedTopicDTO dto);

    @Mapping(source = "description", target = "description")
    ProposedTopicDTO toDto(Topic topic);

    // DODAJ TĘ METODĘ:
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "description", target = "description")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isArchive", ignore = true)
    Topic toTopic(ProposedTopic entity);
}