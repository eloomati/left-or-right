package io.mhetko.lor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequestDTO {

    private Long topicId;

    @NotBlank
    @Size(min =1, max = 10)
    private String side;

    @NotBlank
    @Size(min = 1, max = 2000)
    private String content;


}
