package io.mhetko.lor.dto;

import io.mhetko.lor.entity.enums.ProposedTopicSource;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProposedTopicDTO {
    private Long id;

    @Size(min = 5, max = 255)
    private String title;

    @Size(min = 50, max = 1000)
    private String description;

    @Size(max = 20)
    private ProposedTopicSource source;

    private Integer popularityScore;

    private List<CategoryDTO> categories;
    private List<TagDTO> tags;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Long proposedById;
    private Long categoryId;
}
