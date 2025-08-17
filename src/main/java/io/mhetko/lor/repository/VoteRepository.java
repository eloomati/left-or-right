package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndTopicId(Long userId, Long topicId);
    List<Vote> findAllByTopicId(Long topicId);
    List<Vote> findAllByUserId(Long userId);
}
