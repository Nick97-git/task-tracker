package com.dev.tracker.service;

import com.dev.tracker.model.User;

public interface UserService {

    User save(User user);

    User findByEmail(String email);
}
