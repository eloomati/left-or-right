package io.mhetko.lor.dto;

import io.mhetko.lor.entity.enums.Side;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoteDTO {
    private Long id;
    private Long userId;
    private Long topicId;
    private String side;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
}
