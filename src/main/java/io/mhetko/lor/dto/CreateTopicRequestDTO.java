package io.mhetko.lor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateTopicRequestDTO {

    @NotBlank
    @Size(min = 5, max = 255)
    private String title;

    @NotBlank
    @Size(min = 50, max = 1000)
    private String desctription;

    @NotBlank
    @Size(max = 100)
    private String country;

    @NotBlank
    @Size(max = 100)
    private String continent;

    @NotNull
    private Long categoryId;

    private Set<Long> tagIds;
}
