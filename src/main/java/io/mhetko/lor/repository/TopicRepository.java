package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByTitle(String title);

    @Modifying
    @Transactional
    @Query("UPDATE Topic t SET t.popularityScore = :popularity WHERE t.id = :topicId")
    void updatePopularityScore(Long topicId, int popularity);
}
