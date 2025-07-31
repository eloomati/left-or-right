package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Tag t SET t.deletedAt = :deletedAt WHERE t.id = :id")
    void softDeleteById(Long id, LocalDateTime deletedAt);

    Optional<Tag> findByNameAndDeletedAtIsNull(String name);

    List<Tag> findAllByDeletedAtIsNull();

    Optional<Tag> findByIdAndDeletedAtIsNull(Long id);


}
