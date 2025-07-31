package io.mhetko.lor.service;

import io.mhetko.lor.dto.CommentDTO;
import io.mhetko.lor.dto.CreateCommentRequestDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Comment;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.mapper.CommentMapper;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.CommentRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final AppUserRepository appUserRepository;
    private final CommentMapper commentMapper;
    private final UserUtils userUtils;

    public CommentDTO createComment(CreateCommentRequestDTO dto) {
        log.info("Creating comment for topicId: {}", dto.getTopicId());

        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> {
                    log.error("Topic not found for id: {}", dto.getTopicId());
                    return new IllegalStateException("Topic not found");
                });

        AppUser user = userUtils.getCurrentUser()
                .orElseThrow(() -> {
                    log.error("Current user not found");
                    return new IllegalStateException("Current user not found");
                });

        Comment comment = new Comment();
        comment.setSide(dto.getSide());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setTopic(topic);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created with id: {}", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }
}