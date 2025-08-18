package io.mhetko.lor.dto;

import java.time.LocalDateTime;

public record WatchedTopicDTO(
        String title,
        LocalDateTime createdAt,
        String description,
        String authorUsername,
        String type
) {}
