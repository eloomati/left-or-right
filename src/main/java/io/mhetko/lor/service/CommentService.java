package io.mhetko.lor.service;

import io.mhetko.lor.dto.CommentDTO;
import io.mhetko.lor.dto.CreateCommentRequestDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Comment;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.kafka.CommentEventPublisher;
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
    private final CommentEventPublisher commentEventPublisher;

    public CommentDTO createComment(CreateCommentRequestDTO dto) {
        Topic topic = getTopicOrThrow(dto.getTopicId());
        AppUser user = getCurrentUserOrThrow();

        Comment comment = buildComment(dto, user, topic);
        Comment savedComment = commentRepository.save(comment);

        commentEventPublisher.publishCreated(
                savedComment.getId(),
                topic.getId(),
                user.getId(),
                savedComment.getContent()
        );

        return commentMapper.toDto(savedComment);
    }

    private Topic getTopicOrThrow(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.error("Topic not found for id: {}", topicId);
                    return new IllegalStateException("Topic not found");
                });
    }

    private AppUser getCurrentUserOrThrow() {
        return userUtils.getCurrentUser()
                .orElseThrow(() -> {
                    log.error("Current user not found");
                    return new IllegalStateException("Current user not found");
                });
    }

    private Comment buildComment(CreateCommentRequestDTO dto, AppUser user, Topic topic) {
        Comment comment = new Comment();
        comment.setSide(dto.getSide());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setTopic(topic);
        return comment;
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
        Optional<Comment> maybeComment = commentRepository.findById(id);
        if (maybeComment.isPresent()) {
            Comment comment = maybeComment.get();
            commentRepository.softDeleteById(id, LocalDateTime.now());
            log.info("Comment with id '{}' deleted successfully", id);

            commentEventPublisher.publishDeleted(
                    comment.getId(),
                    comment.getTopic().getId(),
                    comment.getUser().getId()
            );
        } else {
            log.warn("Comment with id '{}' not found for deletion", id);
        }
    }

    public CommentDTO updateComment(Long id, CreateCommentRequestDTO dto) {
        log.info("Updating comment with id: {}", id);
        Optional<Comment> maybeComment = commentRepository.findById(id);
        if (maybeComment.isPresent()) {
            Comment commentUpdate = maybeComment.get();
            commentUpdate.setSide(dto.getSide());
            commentUpdate.setContent(dto.getContent());
            commentUpdate.setUpdatedAt(LocalDateTime.now());
            Comment updateComment = commentRepository.save(commentUpdate);
            log.info("Comment with id '{}' updated successfully", updateComment.getId());

            commentEventPublisher.publishUpdated(
                    updateComment.getId(),
                    updateComment.getTopic().getId(),
                    updateComment.getUser().getId(),
                    updateComment.getContent()
            );

            return commentMapper.toDto(updateComment);
        }
        throw new IllegalArgumentException("Comment not found");
    }
}