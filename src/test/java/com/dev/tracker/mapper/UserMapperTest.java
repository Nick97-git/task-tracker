package com.dev.tracker.mapper;

import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserMapperTest {
    private UserMapper userMapper;
    private UserRegistrationDto userRegistrationDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userMapper = new UserMapper();
        setUserRegistrationDto();
        setUser();
    }

    @Test
    public void checkConvertToUserIsOk() {
        User expected = new User();
        expected.setEmail("email");
        expected.setFirstName("first");
        expected.setLastName("last");
        User actual = userMapper.convertUserRegistrationDtoToUser(userRegistrationDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertToUserResponseDtoIsOk() {
        UserResponseDto expected = new UserResponseDto();
        expected.setEmail("email");
        expected.setFirstName("first");
        expected.setLastName("last");
        UserResponseDto actual = userMapper.convertUserToResponseDto(user);
        Assertions.assertEquals(expected, actual);
    }

    private void setUserRegistrationDto() {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("email");
        userRegistrationDto.setFirstName("first");
        userRegistrationDto.setLastName("last");
    }

    private void setUser() {
        user = new User();
        user.setEmail("email");
        user.setFirstName("first");
        user.setLastName("last");
    }

}
