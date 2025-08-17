package io.mhetko.lor.kafka;

import java.time.Instant;

public sealed interface VoteEvent permits VoteCreatedEvent, VoteRemovedEvent, VoteUpdatedEvent {
    Long userId();
    Long topicId();
    String type();
    Instant occurredAt();
    Integer version();
}






