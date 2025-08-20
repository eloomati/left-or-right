package io.mhetko.lor.repository;

import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndDeletedAtIsNull(AppUser user);

    Optional<Notification> findByUserAndTopicIdAndTopicTitleAndDeletedAtIsNull(
            AppUser user,
            Long topicId,
            String topicTitle
    );

    List<Notification> findByUserAndDeletedAtIsNullAndIsReadFalse(AppUser user);
}
