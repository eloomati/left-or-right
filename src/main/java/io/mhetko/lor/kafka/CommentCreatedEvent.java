package io.mhetko.lor.kafka;

import java.time.Instant;

public record CommentCreatedEvent(
        Long commentId,
        Long topicId,
        Long userId,
        String content,
        Instant occurredAt,
        Integer version
) implements CommentEvent {
    @Override public String type() { return "COMMENT_CREATED"; }
}