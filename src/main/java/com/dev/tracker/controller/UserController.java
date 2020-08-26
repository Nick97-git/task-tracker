package com.dev.tracker.controller;

import com.dev.tracker.model.dto.user.UserDeleteDto;
import com.dev.tracker.model.dto.user.UserPutDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public UserResponseDto getUserData() {
        return null;
    }

    @PutMapping
    public UserResponseDto editUserData(UserPutDto userPutDto) {
        return null;
    }

    @DeleteMapping
    public void delete(UserDeleteDto userDeleteDto) {

    }
}
