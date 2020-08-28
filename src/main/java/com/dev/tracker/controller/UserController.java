package com.dev.tracker.controller;

import com.dev.tracker.mapper.UserMapper;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.user.UserDeleteDto;
import com.dev.tracker.model.dto.user.UserGetRequestDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserPutDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
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
    public List<UserResponseDto> getUsers(@RequestParam @Min(0) int offset,
                                             @RequestParam @Min(1) @Max(10) int limit) {
        List<User> users = userService.getUsers(offset, limit);
        return users.stream()
                .map(userMapper::convertUserToResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public UserGetResponseDto getUser(@RequestBody @Valid UserGetRequestDto userGetRequestDto) {
        User user = userService.findByEmail(userGetRequestDto.getEmail());
        return userMapper.convertUserToUserGetResponseDto(user);
    }

    @PutMapping
    public UserResponseDto updateUser(@RequestBody @Valid UserPutDto userPutDto) {
        User user = userService.findByEmail(userPutDto.getEmail());
        User updatedUser = userMapper.convertUserPutDtoToUser(user, userPutDto);
        return userMapper.convertUserToResponseDto(userService.save(updatedUser));
    }

    @DeleteMapping
    public void deleteUser(@RequestBody @Valid UserDeleteDto userDeleteDto,
                           Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        userService.deleteByEmail(userDeleteDto.getEmail(), user);
    }
}
