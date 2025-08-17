package io.mhetko.lor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoteEventPublisher {

    private final StreamBridge streamBridge;

    private static final String OUT_BINDING = "vote-events-out";

    public void publish(VoteEvent event) {
        Message<VoteEvent> msg = MessageBuilder.withPayload(event)
                .setHeader("type", event.type())
                .setHeader("eventVersion", event.version())
                .build();
        boolean sent = streamBridge.send(OUT_BINDING, msg);
        if (!sent) {
            log.error("Failed to publish event: {}", event);
            throw new IllegalStateException("Kafka publish failed");
        }
        log.debug("Published event: {}", event);
    }

    public void publishCreated(Long userId, Long topicId, io.mhetko.lor.entity.enums.Side side) {
        publish(new VoteCreatedEvent(userId, topicId, side, java.time.Instant.now(), 1));
    }

    public void publishRemoved(Long userId, Long topicId, io.mhetko.lor.entity.enums.Side side) {
        publish(new VoteRemovedEvent(userId, topicId, side, java.time.Instant.now(), 1));
    }

    public void publishUpdated(Long userId, Long topicId,
                               io.mhetko.lor.entity.enums.Side oldSide,
                               io.mhetko.lor.entity.enums.Side newSide) {
        publish(new VoteUpdatedEvent(userId, topicId, oldSide, newSide, java.time.Instant.now(), 1));
    }
}

