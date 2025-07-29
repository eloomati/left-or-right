package io.mhetko.lor.dto;

import io.mhetko.lor.validator.FieldMatch;
import io.mhetko.lor.validator.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(first = "email", second = "confirmEmail", message = "Emails do not match")
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public class AppUserDTO {

    @Schema(description = "Unique username (5-50 characters)", example = "testuser")
    @NotBlank
    @Size(min = 5, max = 50)
    private String username;

    @Schema(description = "User email address", example = "test@example.com")
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Schema(description = "Repeated email address", example = "test@example.com")
    @NotBlank
    @Email
    @Size(max = 100)
    private String confirmEmail;

    @Schema(description = "Password (min. 8 characters, uppercase, lowercase, digit, special character)", example = "StrongP@ssw0rd!")
    @NotBlank
    @ValidPassword
    private String password;

    @Schema(description = "Repeated password", example = "StrongP@ssw0rd!")
    @NotBlank
    private String confirmPassword;

    @Schema(description = "Acceptance of terms and conditions", example = "true")
    @AssertTrue(message = "You must accept the terms and conditions")
    private boolean termsAccepted;
}