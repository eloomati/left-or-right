package io.mhetko.lor.dto;

import io.mhetko.lor.entity.enums.Side;

import java.time.LocalDateTime;

public class VoteDTO {
    private Long id;
    private Long userId;
    private Side side;
    private LocalDateTime createdAt;
}
