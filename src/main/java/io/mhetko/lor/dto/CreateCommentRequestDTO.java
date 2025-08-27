package io.mhetko.lor.dto;

import io.mhetko.lor.entity.enums.Side;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequestDTO {

    private Long topicId;
    private Long proposedTopicId;

    @NotNull
    private Side side;

    @NotNull
    @Size(min = 1, max = 2000)
    private String content;
}