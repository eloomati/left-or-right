package io.mhetko.lor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppUserDTO {

    @NotBlank
    @Size(min = 5, max = 50)
    private String username;
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
}
