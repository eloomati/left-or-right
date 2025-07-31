package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.deletedAt = :deletedAt WHERE c.id = :id")
    void softDeleteById(Long id, LocalDateTime deletedAt);

    Optional<Category> findByNameAndDeletedAtIsNull(String name);

    List<Category> findAllByDeletedAtIsNull();

    Optional<Category> findByIdAndDeletedAtIsNull(Long id);
}
