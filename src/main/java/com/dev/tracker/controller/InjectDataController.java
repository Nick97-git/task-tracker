package com.dev.tracker.controller;

import com.dev.tracker.model.Role;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.TaskStatus;
import com.dev.tracker.model.User;
import com.dev.tracker.security.AuthenticationService;
import com.dev.tracker.service.RoleService;
import com.dev.tracker.service.TaskService;
import com.dev.tracker.service.UserService;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class InjectDataController {
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final TaskService taskService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        injectRoles();
        injectUsers();
        injectTasks();
    }

    private void injectRoles() {
        Role userRole = new Role();
        userRole.setRoleName(Role.RoleName.USER);
        Role adminRole = new Role();
        adminRole.setRoleName(Role.RoleName.ADMIN);
        roleService.save(userRole);
        roleService.save(adminRole);
    }

    private void injectUsers() {
        User primaryUser = new User();
        primaryUser.setEmail("email");
        primaryUser.setPassword(passwordEncoder.encode("1234"));
        primaryUser.setFirstName("first");
        primaryUser.setLastName("last");
        Role adminRole = roleService.findRoleByName(Role.RoleName.ADMIN);
        Role userRole = roleService.findRoleByName(Role.RoleName.USER);
        primaryUser.setRoles(Set.of(adminRole, userRole));
        userService.save(primaryUser);
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setEmail("email" + i);
            user.setPassword("1234");
            user.setFirstName("first");
            user.setLastName("last");
            authenticationService.register(user);
        }
    }

    private void injectTasks() {
        User user = userService.findByEmail("email");
        TaskStatus[] taskStatuses = TaskStatus.values();
        for (int i = 0; i < 20; i++) {
            Task task = new Task();
            task.setUser(user);
            task.setTitle("title" + i);
            task.setDescription("description" + i);
            task.setStatus(taskStatuses[i % 3]);
            taskService.save(task);
        }
    }
}
