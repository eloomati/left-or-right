package io.mhetko.lor.record;

public record VoteUpdatedEvent(Long userId, Long topicId, Side oldSide, Side newSide) {}
