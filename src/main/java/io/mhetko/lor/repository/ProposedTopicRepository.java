package io.mhetko.lor.repository;

import io.mhetko.lor.entity.ProposedTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposedTopicRepository extends JpaRepository<ProposedTopic, Long> {
}
