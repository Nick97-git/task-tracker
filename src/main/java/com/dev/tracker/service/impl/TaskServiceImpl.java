package com.dev.tracker.service.impl;

import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import com.dev.tracker.repository.TaskRepository;
import com.dev.tracker.service.TaskService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteByTitleAndUserId(String title, User user) {
        taskRepository.deleteByTitleAndUser(title, user);
    }

    @Override
    public Task findByTitleAndUserEmail(String title, String email) {
        return taskRepository.findByTitleAndUserEmail(title, email);
    }

    @Override
    public List<Task> getTasks(Task.TaskStatus status) {
        return taskRepository.findAllByStatusOrderByUserIdDesc(status);
    }
}
