package io.mhetko.lor.kafka;

import java.time.Instant;

public record CommentUpdatedEvent(Long commentId, Long topicId, Long userId, String content, Instant createdAt, Integer version) implements CommentEvent {}
