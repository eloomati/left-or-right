package io.mhetko.lor.dto;

import java.time.LocalDateTime;

public record WatchedTopicDTO(
        Long id,
        String title,
        LocalDateTime createdAt,
        String description,
        String authorUsername,
        String type,
        boolean isWatched
) {}
