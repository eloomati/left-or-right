package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.WatchedTopicDTO;
import io.mhetko.lor.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WatchedTopicMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "authorUsername", source = "createdBy.username")
    @Mapping(target = "type", constant = "TOPIC")
    WatchedTopicDTO toDto(Topic topic);
}
