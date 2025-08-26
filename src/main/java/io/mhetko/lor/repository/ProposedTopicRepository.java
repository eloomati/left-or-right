package io.mhetko.lor.repository;

import io.mhetko.lor.entity.ProposedTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposedTopicRepository extends JpaRepository<ProposedTopic, Long> {
    List<ProposedTopic> findAllByDeletedAtIsNull();
}
