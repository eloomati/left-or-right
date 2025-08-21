package io.mhetko.lor.mapper;


import io.mhetko.lor.dto.WatchedTopicDTO;
import io.mhetko.lor.entity.ProposedTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WatchedProposedTopicMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "authorUsername", source = "proposedBy.username")
    @Mapping(target = "type", constant = "PROPOSED_TOPIC")
    WatchedTopicDTO toDto(ProposedTopic proposedTopic);
}