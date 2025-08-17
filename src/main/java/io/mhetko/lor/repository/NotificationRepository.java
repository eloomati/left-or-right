package io.mhetko.lor.repository;

import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndDeletedAtIsNull(AppUser user);
    List<Notification> findByUserAndTopicIdAndDeletedAtIsNull(AppUser user, Long topicId);
}
