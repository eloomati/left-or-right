package io.mhetko.lor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import static java.time.Instant.now;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final StreamBridge streamBridge;
    private static final String OUT_BINDING = "comment-events-out";

    public void publish(CommentEvent event) {
        Message<CommentEvent> msg = MessageBuilder.withPayload(event)
                .setHeader("type", event.type())
                .setHeader("eventVersion", event.version())
                .build();
        boolean sent = streamBridge.send(OUT_BINDING, msg);
        if (!sent) {
            log.error("Failed to publish comment event: {}", event);
            throw new IllegalStateException("Kafka publish failed");
        }
        log.debug("Published comment event: {}", event);
    }

    public void publishCreated(Long commentId, Long topicId, Long userId, String content) {
        publish(new CommentCreatedEvent(commentId, topicId, userId, content, now(), 1));
    }

    public void publishDeleted(Long commentId, Long topicId, Long userId, String content) {
        publish(new CommentRemovedEvent(commentId, topicId, userId, content, now(), 1));
    }

    public void publishUpdated(Long commentId, Long topicId, Long userId, String content) {
        publish(new CommentUpdatedEvent(commentId, topicId, userId, content, now(), 1));
    }
}