package com.dev.tracker.service.impl;

import com.dev.tracker.exception.DeleteUserException;
import com.dev.tracker.exception.NoSuchUserException;
import com.dev.tracker.model.Role;
import com.dev.tracker.model.User;
import com.dev.tracker.repository.UserRepository;
import com.dev.tracker.service.UserService;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @SneakyThrows
    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchUserException("There is no such user with email: " + email + "!");
        }
        return user;
    }

    @SneakyThrows
    @Override
    public void deleteByEmail(String email, User user) {
        if (!hasAdminRole(user) && !user.getEmail().equals(email)) {
            throw new DeleteUserException("You have no right to delete this user!");
        }
        userRepository.deleteByEmail(email);
    }

    @Override
    public List<User> getUsers(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        Page<User> users = userRepository.findAll(pageRequest);
        return users.getContent();
    }

    private boolean hasAdminRole(User user) {
        Set<Role> roles = user.getRoles();
        for (Role role: roles) {
            if (role.getRoleName().equals(Role.RoleName.ADMIN)) {
                return true;
            }
        }
        return false;
    }
}
