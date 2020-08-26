package com.dev.tracker.security;

import com.dev.tracker.model.User;

public interface AuthenticationService {

    User register(User user);

    User login(String email, String password);
}
