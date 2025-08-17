
package io.mhetko.lor.service;

import io.mhetko.lor.dto.NotificationDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Notification;
import io.mhetko.lor.mapper.NotificationMapper;
import io.mhetko.lor.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        Optional<Notification> existing = notificationRepository
                .findByUserAndTopicIdAndTopicTitleAndDeletedAtIsNull(user, topicId, topicTitle);

        if (existing.isPresent()) {
            Notification notif = existing.get();
            notif.setCount(notif.getCount() + 1);
            notif.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notif);
        } else {
            Notification notif = new Notification();
            notif.setUser(user);
            notif.setMessage(message);
            notif.setTopicId(topicId);
            notif.setTopicTitle(topicTitle);
            notif.setCreatedAt(LocalDateTime.now());
            notif.setCount(1);
            notificationRepository.save(notif);
        }
    }
}