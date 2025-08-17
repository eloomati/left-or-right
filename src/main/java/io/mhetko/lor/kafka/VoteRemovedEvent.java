package io.mhetko.lor.kafka;

import io.mhetko.lor.entity.enums.Side;

import java.time.Instant;

public record VoteRemovedEvent(
        Long userId,
        Long topicId,
        Side side,
        Instant occurredAt,
        Integer version
) implements VoteEvent {
    @Override public String type() { return "VOTE_REMOVED"; }
}
