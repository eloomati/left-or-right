package io.mhetko.lor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static java.time.Instant.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final StreamBridge streamBridge;


    public void publish(Object event) {
        streamBridge.send("commentEvents-out-0",
                MessageBuilder.withPayload(event)
                        .setHeader("__TypeId__", event.getClass().getSimpleName()) // <-- KLUCZOWE
                        .build()
        );
    }

    public void publishCreated(Long commentId, Long topicId, Long userId, String content) {
        publish(new CommentCreatedEvent(commentId, topicId, userId, content, now(), 1));
    }

    public void publishUpdated(Long commentId, Long topicId, Long userId, String content) {
        publish(new CommentUpdatedEvent(commentId, topicId, userId, content, now(), 1));
    }

    public void publishDeleted(Long commentId, Long topicId, Long userId, String content) {
        publish(new CommentRemovedEvent(commentId, topicId, userId, content, now(), 1));
    }
}
