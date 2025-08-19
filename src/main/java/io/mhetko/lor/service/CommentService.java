package io.mhetko.lor.service;

import io.mhetko.lor.dto.CommentDTO;
import io.mhetko.lor.dto.CreateCommentRequestDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Comment;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.ProposedTopic;
import io.mhetko.lor.kafka.CommentEventPublisher;
import io.mhetko.lor.mapper.CommentMapper;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.CommentRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.ProposedTopicRepository;
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
    private final ProposedTopicRepository proposedTopicRepository; // dodaj to
    private final CommentMapper commentMapper;
    private final UserUtils userUtils;
    private final CommentEventPublisher commentEventPublisher;

    public CommentDTO createComment(CreateCommentRequestDTO dto) {
        AppUser user = getCurrentUserOrThrow();
        Comment comment = new Comment();
        comment.setSide(dto.getSide());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUser(user);

        if (dto.getTopicId() != null) {
            Topic topic = getTopicOrThrow(dto.getTopicId());
            comment.setTopic(topic);
        } else if (dto.getProposedTopicId() != null) {
            ProposedTopic proposedTopic = getProposedTopicOrThrow(dto.getProposedTopicId());
            comment.setProposedTopic(proposedTopic);
        } else {
            throw new IllegalArgumentException("TopicId lub ProposedTopicId musi być podany");
        }

        Comment savedComment = commentRepository.save(comment);

        // EventPublisher - rozbuduj jeśli chcesz obsłużyć oba przypadki
        commentEventPublisher.publishCreated(
                savedComment.getId(),
                savedComment.getTopic() != null ? savedComment.getTopic().getId() : null,
                savedComment.getProposedTopic() != null ? savedComment.getProposedTopic().getId() : null,
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

    private ProposedTopic getProposedTopicOrThrow(Long id) {
        return proposedTopicRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ProposedTopic not found for id: {}", id);
                    return new IllegalStateException("ProposedTopic not found");
                });
    }

    private AppUser getCurrentUserOrThrow() {
        return userUtils.getCurrentUser()
                .orElseThrow(() -> {
                    log.error("Current user not found");
                    return new IllegalStateException("Current user not found");
                });
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

    public List<CommentDTO> getCommentsByProposedTopicId(Long id) {
        log.info("Fetching comments for proposed topic with id: {}", id);
        return commentRepository.findAllByProposedTopicIdAndDeletedAtIsNull(id)
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
                    comment.getTopic() != null ? comment.getTopic().getId() : null,
                    comment.getProposedTopic() != null ? comment.getProposedTopic().getId() : null,
                    comment.getUser().getId(),
                    comment.getContent()
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
                    updateComment.getTopic() != null ? updateComment.getTopic().getId() : null,
                    updateComment.getProposedTopic() != null ? updateComment.getProposedTopic().getId() : null,
                    updateComment.getUser().getId(),
                    updateComment.getContent()
            );

            return commentMapper.toDto(updateComment);
        }
        throw new IllegalArgumentException("Comment not found");
    }

    public List<CommentDTO> getCommentsByTopicAndSide(Long topicId, io.mhetko.lor.entity.enums.Side side) {
        return commentRepository.findByTopicIdAndSideAndDeletedAtIsNull(topicId, side)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }
}