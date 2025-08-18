package io.mhetko.lor.repository;

import io.mhetko.lor.entity.ProposedTopicWatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProposedTopicWatchRepository extends JpaRepository<ProposedTopicWatch, Long> {
    List<ProposedTopicWatch> findAllByProposedTopicId(Long proposedTopicId);
}
