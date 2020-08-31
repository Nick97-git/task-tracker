package com.dev.tracker.controller;

import com.dev.tracker.mapper.UserMapper;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.user.UserDeleteDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import com.dev.tracker.model.dto.user.UserUpdateDto;
import com.dev.tracker.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponseDto> getUsers(@RequestParam(defaultValue = "0") @Min(0) int offset,
                                             @RequestParam(defaultValue = "1")
                                             @Min(1) @Max(10) int limit) {
        List<User> users = userService.getUsers(offset, limit);
        return users.stream()
                .map(userMapper::convertUserToUserResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public UserGetResponseDto getUser(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return userMapper.convertUserToUserGetResponseDto(user);
    }

    @PutMapping
    public UserResponseDto updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto,
                                      Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        User updatedUser = userMapper.convertUserPutDtoToUser(user, userUpdateDto);
        return userMapper.convertUserToUserResponseDto(userService.save(updatedUser));
    }

    @DeleteMapping
    public void deleteUser(@RequestBody @Valid UserDeleteDto userDeleteDto,
                           Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        userService.deleteByEmail(userDeleteDto.getEmail(), user);
    }
}
