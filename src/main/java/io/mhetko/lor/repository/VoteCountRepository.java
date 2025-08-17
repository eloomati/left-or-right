package io.mhetko.lor.repository;

import io.mhetko.lor.entity.VoteCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteCountRepository extends JpaRepository<VoteCount, Long> {
    Optional<VoteCount> findByTopicId(Long topicId);
}
