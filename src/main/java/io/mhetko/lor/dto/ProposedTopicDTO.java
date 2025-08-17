package io.mhetko.lor.dto;

import io.mhetko.lor.entity.enums.ProposedTopicSource;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProposedTopicDTO {
    private Long id;

    @Size(min = 5, max = 255)
    private String title;

    @Size(min = 50, max = 1000)
    private String description;

    @Size(max = 20)
    private ProposedTopicSource source;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Long proposedById;
    private Long categoryId;
}
