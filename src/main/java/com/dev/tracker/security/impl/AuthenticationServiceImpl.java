package com.dev.tracker.security.impl;

import com.dev.tracker.exception.AuthenticationException;
import com.dev.tracker.model.Role;
import com.dev.tracker.model.User;
import com.dev.tracker.security.AuthenticationService;
import com.dev.tracker.service.RoleService;
import com.dev.tracker.service.UserService;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleService.findRoleByName(Role.RoleName.USER)));
        return userService.save(user);
    }

    @Override
    @SneakyThrows
    public User login(String email, String password) {
        User user = userService.findByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Incorrect username or password!");
        }
        return user;
    }
}
