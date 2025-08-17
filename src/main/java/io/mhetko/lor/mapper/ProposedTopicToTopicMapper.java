package io.mhetko.lor.mapper;

import io.mhetko.lor.entity.ProposedTopic;
import io.mhetko.lor.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProposedTopicToTopicMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "desctription", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "category", source = "category")
        // Dodaj mapowanie pozostałych pól według potrzeb
    Topic toTopic(ProposedTopic proposedTopic);
}
