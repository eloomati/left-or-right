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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
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
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setTopic(topic);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created with id: {}", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    public List<CommentDTO> getAllComments() {
        log.info("Fetching all comments");
        return commentRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public Optional<CommentDTO> getCommentById(Long id) {
        log.info("Fetching comment with id: {}", id);
        return commentRepository.findById(id)
                .map(commentMapper::toDto);
    }

    public List<CommentDTO> getCommentsByTopicId(Long id) {
        log.info("Fetching comments for topic with id: {}", id);
        return commentRepository.findAllByTopicIdAndDeletedAtIsNull(id)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteComment(Long id) {
        log.info("Deleting comment with id: {}", id);
        commentRepository.softDeleteById(id, LocalDateTime.now());
        log.info("Comment with id '{}' deleted successfully", id);
    }

    public CommentDTO updateComment(Long id, CreateCommentRequestDTO dto) {
        log.info("Updating comment with id: {}", id);
        Optional<Comment> maybeComment = commentRepository.findById(id);
        if(maybeComment.isPresent()) {
            Comment commentUpdate = maybeComment.get();
            commentUpdate.setSide(dto.getSide());
            commentUpdate.setContent(dto.getContent());
            commentUpdate.setUpdatedAt(LocalDateTime.now());
            Comment updateComment = commentRepository.save(commentUpdate);
            log.info("Comment with id '{}' updated successfully", updateComment.getId());
            return commentMapper.toDto(updateComment);
        }
        throw new IllegalArgumentException("Comment not found");
    }
}