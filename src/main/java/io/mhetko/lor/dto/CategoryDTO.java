package io.mhetko.lor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class CategoryDTO {

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
}
