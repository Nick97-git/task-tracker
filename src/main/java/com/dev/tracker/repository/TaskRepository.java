package com.dev.tracker.repository;

import com.dev.tracker.model.Task;
import com.dev.tracker.model.TaskStatus;
import com.dev.tracker.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    void deleteByTitleAndUser(String title, User user);

    Task findByTitleAndUser(String title, User user);

    List<Task> findAllByStatusOrderByUserIdDesc(TaskStatus status);
}
