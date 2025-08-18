package io.mhetko.lor.mapper;


import io.mhetko.lor.dto.WatchedTopicDTO;
import io.mhetko.lor.entity.ProposedTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WatchedProposedTopicMapper {
    @Mapping(target = "authorUsername", source = "proposedBy.username")
    @Mapping(target = "type", constant = "PROPOSED_TOPIC")
    WatchedTopicDTO toDto(ProposedTopic proposedTopic);
}
