package io.mhetko.lor.mapper;

import io.mhetko.lor.dto.CommentDTO;
import io.mhetko.lor.dto.CreateCommentRequestDTO;
import org.mapstruct.Mapper;
import io.mhetko.lor.entity.Comment;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "topic", ignore = true)
    Comment toEntity(CreateCommentRequestDTO dto);

    CommentDTO toDto(Comment comment);
}
