package io.mhetko.lor.dto;

import lombok.Data;

@Data
public class VoteCountDTO {
    private Long topicId;
    private int leftCount;
    private int rightCount;
    private Long version;
}
