package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.VoteDTO;
import io.mhetko.lor.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Vote toEntity(VoteDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "topicId", source = "topic.id")
    VoteDTO toDto(Vote vote);
}
