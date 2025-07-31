package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Continent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContinentRepository extends JpaRepository<Continent, Long> {
    Optional<Continent> findByName(String name);
}