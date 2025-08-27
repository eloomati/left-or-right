package io.mhetko.lor.adnotation;

import io.mhetko.lor.validator.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Hasła nie są zgodne";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
