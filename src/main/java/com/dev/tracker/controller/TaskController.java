package com.dev.tracker.controller;

import com.dev.tracker.mapper.TaskMapper;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskCreateDto;
import com.dev.tracker.model.dto.task.TaskDeleteDto;
import com.dev.tracker.model.dto.task.TaskGetDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import com.dev.tracker.model.dto.task.TaskUpdateDto;
import com.dev.tracker.model.dto.task.TaskUpdateResponseDto;
import com.dev.tracker.model.dto.task.TaskUpdateStatusDto;
import com.dev.tracker.model.dto.task.TaskUpdateUserDto;
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
    public List<TaskResponseDto> getTasks(@RequestBody @Valid TaskGetDto taskGetDto) {
        List<Task> tasks = taskService
                .getTasks(taskMapper.getStatus(taskGetDto.getStatus()));
        return tasks.stream()
                .map(taskMapper::convertTaskToTaskResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TaskResponseDto createTask(@RequestBody @Valid TaskCreateDto taskCreateDto) {
        User user = userService.findByEmail(taskCreateDto.getEmail());
        Task task = taskMapper.convertTaskPostDtoToTask(taskCreateDto, user);
        taskService.save(task);
        return taskMapper.convertTaskToTaskResponseDto(task);
    }

    @PutMapping
    public TaskUpdateResponseDto updateTask(@RequestBody @Valid TaskUpdateDto taskUpdateDto) {
        Task task = taskService.findByTitleAndUserEmail(taskUpdateDto.getTitle(),
                taskUpdateDto.getEmail());
        Task updatedTask = taskMapper.convertTaskPutDtoToTask(task, taskUpdateDto);
        return taskMapper.convertTaskToTaskPutResponseDto(taskService.save(updatedTask));
    }

    @PutMapping("/user")
    public TaskResponseDto updateTaskUser(
            @RequestBody @Valid TaskUpdateUserDto taskUpdateUserDto) {
        Task task = taskService.findByTitleAndUserEmail(taskUpdateUserDto.getTitle(),
                taskUpdateUserDto.getEmail());
        User newUser = userService.findByEmail(taskUpdateUserDto.getNewEmail());
        task.setUser(newUser);
        return taskMapper.convertTaskToTaskResponseDto(taskService.save(task));
    }

    @PutMapping("/status")
    public TaskResponseDto updateTaskStatus(
            @RequestBody @Valid TaskUpdateStatusDto taskUpdateStatusDto) {
        Task task = taskService.findByTitleAndUserEmail(taskUpdateStatusDto.getTitle(),
                taskUpdateStatusDto.getEmail());
        task.setStatus(taskMapper.getStatus(taskUpdateStatusDto.getNewStatus()));
        return taskMapper.convertTaskToTaskResponseDto(taskService.save(task));
    }

    @DeleteMapping
    public void deleteTask(@RequestBody @Valid TaskDeleteDto taskDeleteDto) {
        User user = userService.findByEmail(taskDeleteDto.getEmail());
        taskService.deleteByTitleAndUserId(taskDeleteDto.getTitle(), user);
    }
}
