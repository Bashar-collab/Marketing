package com.custempmanag.marketing.annotation;

import com.custempmanag.marketing.validator.PasswordMatchesValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "{password.match.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
