package io.mhetko.lor.kafka;

import io.mhetko.lor.repository.TopicWatchRepository;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CommentEventListener {

    private final TopicWatchRepository topicWatchRepository;
    private final AppUserRepository appUserRepository;
    private final TopicRepository topicRepository;
    private final NotificationService notificationService;

    @Bean
    public Consumer<CommentEvent> commentEvents() {
        return event -> {
            log.info("📥 Received event in consumer: {} [{}]", event, event.getClass().getSimpleName());

            if (event instanceof CommentCreatedEvent created) {
                handleCreated(created);
            } else if (event instanceof CommentUpdatedEvent updated) {
                log.info("🔄 Comment updated: {}", updated.commentId());
            } else if (event instanceof CommentRemovedEvent removed) {
                log.info("❌ Comment removed: {}", removed.commentId());
            }
        };
    }

    private void handleCreated(CommentCreatedEvent event) {
        var topicId = event.topicId();
        var topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) return;

        log.info("Handling CommentCreatedEvent for topicId={}", topicId);

        var watchers = topicWatchRepository.findAllByTopicId(topicId);
        for (var watch : watchers) {
            var user = appUserRepository.findById(watch.getUser().getId()).orElse(null);
            if (user == null) continue;
            notificationService.createNotification(
                    user,
                    "New comment in a watched topic",
                    topicId,
                    topic.getTitle()
            );
        }
    }
}
