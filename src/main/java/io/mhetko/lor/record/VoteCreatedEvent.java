package io.mhetko.lor.record;

public record VoteCreatedEvent(Long userId, Long topicId, Side side) {}
