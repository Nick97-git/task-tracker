package com.dev.tracker.annotation;

import com.dev.tracker.model.dto.user.UserRegistrationDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsValueMatchValidator implements
        ConstraintValidator<PasswordsValueMatch, UserRegistrationDto> {

    @Override
    public boolean isValid(UserRegistrationDto userRegistrationDto, ConstraintValidatorContext
            constraintValidatorContext) {
        String password = userRegistrationDto.getPassword();
        String repeatPassword = userRegistrationDto.getRepeatPassword();
        return password != null && password.length() != 0
                && password.equals(repeatPassword);
    }
}
