package io.mhetko.lor.kafka;

import io.mhetko.lor.entity.enums.Side;

import java.time.Instant;

public record VoteUpdatedEvent(
        Long userId,
        Long topicId,
        Side oldSide,
        Side newSide,
        Instant occurredAt,
        Integer version
) implements VoteEvent {
    @Override public String type() { return "VOTE_UPDATED"; }
}

