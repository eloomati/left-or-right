package io.mhetko.lor.repository;

import io.mhetko.lor.entity.VoteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface VoteCountRepository extends JpaRepository<VoteCount, Long> {

    Optional<VoteCount> findByTopicId(Long topicId);

    @Modifying
    @Query("UPDATE VoteCount v SET v.leftCount = v.leftCount + 1 WHERE v.topic.id = :topicId")
    void incrementLeft(@Param("topicId") Long topicId);

    @Modifying
    @Query("UPDATE VoteCount v SET v.rightCount = v.rightCount + 1 WHERE v.topic.id = :topicId")
    void incrementRight(@Param("topicId") Long topicId);

    @Modifying
    @Query("UPDATE VoteCount v SET v.leftCount = CASE WHEN v.leftCount > 0 THEN v.leftCount - 1 ELSE 0 END " +
            "WHERE v.topic.id = :topicId")
    void decrementLeft(@Param("topicId") Long topicId);

    @Modifying
    @Query("UPDATE VoteCount v SET v.rightCount = CASE WHEN v.rightCount > 0 THEN v.rightCount - 1 ELSE 0 END " +
            "WHERE v.topic.id = :topicId")
    void decrementRight(@Param("topicId") Long topicId);

    @Query("SELECT v.topic.id FROM VoteCount v ORDER BY (v.leftCount + v.rightCount) DESC")
    List<Long> findMostPopularTopics(org.springframework.data.domain.Pageable pageable);

    Optional<VoteCount> findByProposedTopicId(Long proposedTopicId);
}
