package com.dev.tracker.model.dto.user;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPutDto {
    @NotBlank(message = "Email can't be null or blank!")
    private String email;
    private String newEmail;
    private String firstName;
    private String lastName;
}
