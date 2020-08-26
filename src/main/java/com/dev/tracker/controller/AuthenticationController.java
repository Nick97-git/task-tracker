package com.dev.tracker.controller;

import com.dev.tracker.mapper.UserMapper;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.user.UserLoginDto;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import com.dev.tracker.security.AuthenticationService;
import com.dev.tracker.security.jwt.JwtTokenProvider;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        User user = userMapper.convertUserRegistrationDtoToUser(userRegistrationDto);
        User registeredUser = authenticationService.register(user);
        return userMapper.convertUserToResponseDto(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        User user = authenticationService.login(userLoginDto.getEmail(),
                userLoginDto.getPassword());
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toList()));
        return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
    }
}
