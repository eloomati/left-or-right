package io.mhetko.lor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TopicDTO {
    private Long id;
    private String title;
    private String desctription;
    private Long countryId;
    private Long continentId;
    private CategoryDTO category;
    private Set<TagDTO> tags;
    private LocalDateTime createdAt;
    private Set<CommentDTO> comments;
    @JsonProperty("isWatched")
    private boolean isWatched;
}