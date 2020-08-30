package com.dev.tracker.model.dto.user;

import com.dev.tracker.annotation.EmailConstraint;
import com.dev.tracker.annotation.PasswordsValueMatch;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordsValueMatch
public class UserRegistrationDto {
    @EmailConstraint
    private String email;
    private String password;
    private String repeatPassword;
    @NotBlank(message = "First name can't be null or blank!")
    private String firstName;
    @NotBlank(message = "Last name can't be null or blank!")
    private String lastName;
}
