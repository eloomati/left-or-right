package io.mhetko.lor.kafka;

import java.time.Instant;

public sealed interface CommentEvent permits CommentCreatedEvent, CommentRemovedEvent, CommentUpdatedEvent {
    Long commentId();
    Long topicId();
    Long userId();
    String content();
    Instant createdAt();
    Integer version();
}
