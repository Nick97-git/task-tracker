package com.dev.tracker.model.dto.user;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String newEmail;
    private String firstName;
    private String lastName;
}
