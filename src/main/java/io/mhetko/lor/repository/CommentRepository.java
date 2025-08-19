package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt WHERE c.id = :id")
    void softDeleteById(Long id, LocalDateTime deletedAt);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    List<Comment> findAllByDeletedAtIsNull();

    List<Comment> findAllByTopicIdAndDeletedAtIsNull(Long topicId);

    List<Comment> findAllByProposedTopicIdAndDeletedAtIsNull(Long proposedTopicId);

    List<Comment> findByTopicIdAndSide(Long topicId, io.mhetko.lor.entity.enums.Side side);
}
