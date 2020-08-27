package com.dev.tracker.service.impl;

import com.dev.tracker.exception.NoSuchUserException;
import com.dev.tracker.model.User;
import com.dev.tracker.repository.UserRepository;
import com.dev.tracker.service.UserService;
import java.util.List;
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

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    @Override
    public List<User> getUsers(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        Page<User> users = userRepository.findAll(pageRequest);
        return users.getContent();
    }
}
