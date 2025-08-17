package io.mhetko.lor.repository;

import io.mhetko.lor.entity.VoteCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteCountRepository extends JpaRepository<VoteCount, Long> {
}
