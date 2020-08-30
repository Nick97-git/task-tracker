package com.dev.tracker.service;

import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import java.util.List;

public interface TaskService {

    Task save(Task task);

    void deleteByTitleAndUserId(String title, User user);

    Task findByTitleAndUserEmail(String title, String email);

    List<Task> getTasks(Task.TaskStatus status);
}
