package com.dev.tracker.mapper;

import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User convertUserRegistrationDtoToUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setPassword(userRegistrationDto.getPassword());
        return user;
    }

    public UserResponseDto convertUserToResponseDto(User registeredUser) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(registeredUser.getEmail());
        userResponseDto.setFirstName(registeredUser.getFirstName());
        userResponseDto.setLastName(registeredUser.getLastName());
        return userResponseDto;
    }
}
