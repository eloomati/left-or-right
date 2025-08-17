package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.VoteCount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VoteCountMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "version", source = "version")
    VoteCountDTO toDto(VoteCount entity);

    @Mapping(target = "topic", source = "topicId", qualifiedByName = "topicFromId")
    @Mapping(target = "version", source = "version")
    VoteCount toEntity(VoteCountDTO dto);

    @Named("topicFromId")
    default Topic topicFromId(Long id) {
        if (id == null) return null;
        Topic topic = new Topic();
        topic.setId(id);
        return topic;
    }
}