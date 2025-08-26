package io.mhetko.lor.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WatchedTopicDTO(
        Long id,
        String title,
        java.time.LocalDateTime createdAt,
        String description,
        String authorUsername,
        String type,
        boolean isWatched,
        Integer popularityScore,
        List<CategoryDTO> categories,
        List<TagDTO> tags
) {}
