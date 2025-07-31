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

    @NotNull
    private Long countryId;

    @NotNull
    private Long continentId;

    @NotNull
    private Long categoryId;

    private Set<Long> tagIds;
}
