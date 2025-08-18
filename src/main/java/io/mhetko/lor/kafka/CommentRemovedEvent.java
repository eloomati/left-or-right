package io.mhetko.lor.kafka;

import java.time.Instant;

public record CommentRemovedEvent(
        Long commentId,
        Long topicId,
        Long proposedTopicId,
        Long userId,
        String content,
        Instant createdAt,
        Integer version
) implements CommentEvent {}