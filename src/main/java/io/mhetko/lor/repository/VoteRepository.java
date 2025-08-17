package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndTopicId(Long userId, Long topicId);

    List<Vote> findAllByTopicId(Long topicId);

    List<Vote> findAllByUserId(Long userId);

    Optional<Vote> findByUserIdAndTopicIdAndIsDeletedFalse(Long userId, Long topicId);

    List<Vote> findAllByUserIdAndIsDeletedFalse(Long userId);

    @Modifying
    @Query("UPDATE Vote v SET v.isDeleted = true, v.deletedAt = CURRENT_TIMESTAMP, v.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE v.user.id = :userId AND v.isDeleted = false")
    void softDeleteByUserId(@Param("userId") Long userId);
}
