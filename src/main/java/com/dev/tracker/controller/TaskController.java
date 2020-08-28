package com.dev.tracker.controller;

import com.dev.tracker.mapper.TaskMapper;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskDeleteDto;
import com.dev.tracker.model.dto.task.TaskGetRequestDto;
import com.dev.tracker.model.dto.task.TaskPostDto;
import com.dev.tracker.model.dto.task.TaskPutDto;
import com.dev.tracker.model.dto.task.TaskPutResponseDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import com.dev.tracker.model.dto.task.TaskStatusChangeDto;
import com.dev.tracker.model.dto.task.TaskUserChangeDto;
import com.dev.tracker.service.TaskService;
import com.dev.tracker.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public List<TaskResponseDto> getTasks(@RequestBody @Valid TaskGetRequestDto taskGetRequestDto) {
        List<Task> tasks = taskService
                .getTasks(taskMapper.getStatus(taskGetRequestDto.getStatus()));
        return tasks.stream()
                .map(taskMapper::convertTaskToTaskResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TaskResponseDto createTask(@RequestBody @Valid TaskPostDto taskPostDto) {
        User user = userService.findByEmail(taskPostDto.getEmail());
        Task task = taskMapper.convertTaskCreationDtoToTask(taskPostDto, user);
        Task createdTask = taskService.save(task);
        return taskMapper.convertTaskToTaskResponseDto(createdTask);
    }

    @PutMapping
    public TaskPutResponseDto updateTask(@RequestBody @Valid TaskPutDto taskPutDto) {
        Task task = taskService.findByTitleAndUserEmail(taskPutDto.getCurrentTitle(),
                taskPutDto.getEmail());
        Task updatedTask = taskMapper.convertTaskPutDtoToTask(task, taskPutDto);
        return taskMapper.convertTaskToTaskPutResponseDto(taskService.save(updatedTask));
    }

    @PutMapping("/user")
    public TaskResponseDto updateTaskUser(
            @RequestBody @Valid TaskUserChangeDto taskUserChangeDto) {
        Task task = taskService.findByTitleAndUserEmail(taskUserChangeDto.getTitle(),
                taskUserChangeDto.getEmail());
        User newUser = userService.findByEmail(taskUserChangeDto.getNewEmail());
        task.setUser(newUser);
        return taskMapper.convertTaskToTaskResponseDto(taskService.save(task));
    }

    @PutMapping("/status")
    public TaskResponseDto updateTaskStatus(
            @RequestBody @Valid TaskStatusChangeDto taskStatusChangeDto) {
        Task task = taskService.findByTitleAndUserEmail(taskStatusChangeDto.getTitle(),
                taskStatusChangeDto.getEmail());
        task.setStatus(taskMapper.getStatus(taskStatusChangeDto.getNewStatus()));
        return taskMapper.convertTaskToTaskResponseDto(taskService.save(task));
    }

    @DeleteMapping
    public void deleteTask(@RequestBody @Valid TaskDeleteDto taskDeleteDto) {
        User user = userService.findByEmail(taskDeleteDto.getEmail());
        taskService.deleteByTitleAndUserId(taskDeleteDto.getTitle(), user);
    }
}
