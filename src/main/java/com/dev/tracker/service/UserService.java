package com.dev.tracker.service;

import com.dev.tracker.model.User;
import java.util.List;

public interface UserService {

    User save(User user);

    User findByEmail(String email);

    void deleteByEmail(String email);

    List<User> getUsers(int offset, int limit);
}
