package com.dev.tracker.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        String regex = ".+@.+\\..+";
        return email != null && email.matches(regex)
                && email.length() > 8 && email.length() < 30;
    }
}
