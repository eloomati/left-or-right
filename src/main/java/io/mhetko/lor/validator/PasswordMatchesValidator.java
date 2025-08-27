package io.mhetko.lor.validator;

import io.mhetko.lor.adnotation.PasswordMatches;
import io.mhetko.lor.dto.RegisterUserDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterUserDTO> {
    @Override
    public boolean isValid(RegisterUserDTO dto, ConstraintValidatorContext context) {
        return dto.getPassword() != null && dto.getPassword().equals(dto.getConfirmPassword());
    }
}
