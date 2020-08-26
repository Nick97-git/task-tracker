package com.dev.tracker.controller;

import com.dev.tracker.model.Role;
import com.dev.tracker.model.User;
import com.dev.tracker.security.AuthenticationService;
import com.dev.tracker.service.RoleService;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class InjectDataController {
    private final RoleService roleService;
    private final AuthenticationService authenticationService;

    @PostConstruct
    public void init() {
        injectRoles();
        injectUsers();
    }

    private void injectRoles() {
        Role userRole = new Role();
        userRole.setRoleName(Role.RoleName.USER);
        roleService.save(userRole);
    }

    private void injectUsers() {
        User user = new User();
        user.setEmail("email");
        user.setPassword("1234");
        user.setFirstName("first");
        user.setLastName("last");
        authenticationService.register(user);
    }
}
