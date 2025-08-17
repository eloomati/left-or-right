package io.mhetko.lor.record;

public record VoteRemovedEvent(Long userId, Long topicId, Side side) {}
