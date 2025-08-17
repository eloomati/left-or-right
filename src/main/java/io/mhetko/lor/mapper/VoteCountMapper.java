package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.entity.VoteCount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteCountMapper {

    @Mapping(target = "topicId", source = "id")
    VoteCountDTO toDto(VoteCount entity);

    @Mapping(target = "id", source = "topicId")
    VoteCount toEntity(VoteCountDTO dto);
}