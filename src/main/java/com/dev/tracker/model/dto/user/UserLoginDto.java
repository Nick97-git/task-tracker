package com.dev.tracker.model.dto.user;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank(message = "Email can't be null or blank!")
    private String email;
    @NotBlank(message = "Password can't be null or blank!")
    private String password;
}
