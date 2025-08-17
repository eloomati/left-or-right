package io.mhetko.lor.kafka;

import io.mhetko.lor.kafka.CommentCreatedEvent;
import io.mhetko.lor.repository.TopicWatchRepository;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import io.mhetko.lor.entity.TopicWatch;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {

    private final TopicWatchRepository topicWatchRepository;
    private final AppUserRepository appUserRepository;
    private final TopicRepository topicRepository;
    private final NotificationService notificationService;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        var topicId = event.topicId();
        var topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) return;

        var watchers = topicWatchRepository.findAllByTopicId(topicId);
        for (var watch : watchers) {
            var user = appUserRepository.findById(watch.getUser().getId()).orElse(null);
            if (user == null) continue;
            notificationService.createNotification(
                    user,
                    "Nowy komentarz w obserwowanym temacie",
                    topicId,
                    topic.getTitle()
            );
        }
    }
}