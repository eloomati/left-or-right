package io.mhetko.lor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long topicId;
    private String topicTitle;
}
