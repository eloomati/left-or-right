package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.TopicWatch;
import io.mhetko.lor.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicWatchRepository extends JpaRepository<TopicWatch, Long> {
    List<TopicWatch> findAllByTopicId(Long topicId);
    boolean existsByUserAndTopic(AppUser user, Topic topic);
}
