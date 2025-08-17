package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByTitle(String title);

    @Modifying
    @Query("update Topic t set t.popularityScore = t.popularityScore + 1 where t.id = :topicId")
    void incrementPopularityScore(@Param("topicId") Long topicId);

    @Modifying
    @Query("update Topic t set t.popularityScore = case when t.popularityScore > 0 then t.popularityScore - 1 else 0 end where t.id = :topicId")
    void decrementPopularityScore(@Param("topicId") Long topicId);

    @Modifying
    @Transactional
    @Query("UPDATE Topic t SET t.popularityScore = :popularity WHERE t.id = :topicId")
    void updatePopularityScore(Long topicId, int popularity);
}