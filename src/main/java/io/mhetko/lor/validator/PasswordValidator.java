package io.mhetko.lor.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[!@#$%^&*()_+\\-={\\}\\[\\];':\"\\\\|,.<>/?].*")) return false;
        String lower = password.toLowerCase();
        if (lower.contains("password") || lower.contains("123456")) return false;
        return true;
    }
}