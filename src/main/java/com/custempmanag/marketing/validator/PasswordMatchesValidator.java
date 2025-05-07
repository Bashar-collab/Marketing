package com.custempmanag.marketing.validator;

import com.custempmanag.marketing.annotation.PasswordMatches;
import com.custempmanag.marketing.request.ChangePasswordRequest;
import com.custempmanag.marketing.request.RegisterRequest;
import jakarta.validation.ConstraintValidatorContext;

import jakarta.validation.ConstraintValidator;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ChangePasswordRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ChangePasswordRequest request, ConstraintValidatorContext context) {
        return request.getNewPassword().equals(request.getConfirmPassword());
    }
}

