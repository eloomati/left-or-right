package io.mhetko.lor.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.mhetko.lor.entity.enums.Side;

@Data
public class CommentDTO {
    private Long id;
    private Side side;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private AppUserDTO user;
    private Long topicId;
}
