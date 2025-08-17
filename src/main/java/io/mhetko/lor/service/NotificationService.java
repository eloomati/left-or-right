
package io.mhetko.lor.service;

import io.mhetko.lor.dto.NotificationDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Notification;
import io.mhetko.lor.mapper.NotificationMapper;
import io.mhetko.lor.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public List<NotificationDTO> getUserNotifications(AppUser user) {
        return notificationRepository.findByUserAndDeletedAtIsNull(user)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void createNotification(AppUser user, String message, Long topicId, String topicTitle) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(java.time.LocalDateTime.now());
        notification.setTopicId(topicId);
        notification.setTopicTitle(topicTitle);
        notificationRepository.save(notification);
    }
}