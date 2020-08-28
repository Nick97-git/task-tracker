package com.dev.tracker.security;

import com.dev.tracker.model.Role;
import com.dev.tracker.model.User;
import com.dev.tracker.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
public class CustomUserDetailsServiceTest {
    private UserDetailsService userDetailsService;
    private User user;
    private Set<Role> roles;
    @MockBean
    private UserService userService;

    @BeforeEach()
    public void setUp() {
        userDetailsService = new CustomUserDetailsService(userService);
        setRoles();
        setUser();
    }

    private void setUser() {
        user = new User();
        user.setEmail("email");
        user.setPassword("1234");
        user.setRoles(roles);
    }

    private void setRoles() {
        Role adminRole = new Role();
        adminRole.setRoleName(Role.RoleName.ADMIN);
        Role userRole = new Role();
        userRole.setRoleName(Role.RoleName.USER);
        roles = Set.of(adminRole, userRole);
    }

    @Test
    public void loadUserByUserNameIsOk() {
        Mockito.when(userService.findByEmail("email"))
                .thenReturn(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername("email");
        Assertions.assertEquals(user.getEmail(), userDetails.getUsername());
        Assertions.assertEquals(user.getPassword(), userDetails.getPassword());
        Assertions.assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    public void loadUserByNonExistentUserName() {

    }

    @Test
    public void loadUserByUserNameWithoutRoles() {
        Mockito.when(userService.findByEmail("wrong email"))
                .thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("wrong email"));
    }
}
