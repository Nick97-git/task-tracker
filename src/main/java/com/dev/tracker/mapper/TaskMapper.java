package com.dev.tracker.mapper;

import com.dev.tracker.exception.TaskStatusException;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskCreateDto;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import com.dev.tracker.model.dto.task.TaskUpdateDto;
import com.dev.tracker.model.dto.task.TaskUpdateResponseDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task convertTaskPostDtoToTask(TaskCreateDto taskCreateDto, User user) {
        Task task = new Task();
        task.setTitle(taskCreateDto.getTitle());
        task.setDescription(taskCreateDto.getDescription());
        task.setStatus(getStatus(taskCreateDto.getStatus()));
        task.setUser(user);
        return task;
    }

    public TaskResponseDto convertTaskToTaskResponseDto(Task task) {
        TaskResponseDto taskResponseDto = new TaskResponseDto();
        taskResponseDto.setTitle(task.getTitle());
        taskResponseDto.setEmail(task.getUser().getEmail());
        taskResponseDto.setStatus(task.getStatus().getName());
        return taskResponseDto;
    }

    public Task convertTaskPutDtoToTask(Task task, TaskUpdateDto taskUpdateDto) {
        task.setStatus(getStatus(getValue(task.getStatus().getName(),
                taskUpdateDto.getNewStatus())));
        task.setTitle(getValue(task.getTitle(), taskUpdateDto.getNewTitle()));
        task.setDescription(getValue(task.getDescription(), taskUpdateDto.getNewDescription()));
        return task;
    }

    @SneakyThrows
    public Task.TaskStatus getStatus(String status) {
        Task.TaskStatus[] taskStatuses = Task.TaskStatus.values();
        for (Task.TaskStatus taskStatus : taskStatuses) {
            if (taskStatus.getName().equalsIgnoreCase(status)) {
                return taskStatus;
            }
        }
        throw new TaskStatusException("There is no such status for task: " + status + "!");
    }

    public TaskUpdateResponseDto convertTaskToTaskPutResponseDto(Task task) {
        TaskUpdateResponseDto taskUpdateResponseDto = new TaskUpdateResponseDto();
        taskUpdateResponseDto.setTitle(task.getTitle());
        taskUpdateResponseDto.setDescription(task.getDescription());
        taskUpdateResponseDto.setStatus(task.getStatus().getName());
        taskUpdateResponseDto.setEmail(task.getUser().getEmail());
        return taskUpdateResponseDto;
    }

    public TaskGetResponseDto convertTaskToTaskGetResponseDto(Task task) {
        TaskGetResponseDto taskGetResponseDto = new TaskGetResponseDto();
        taskGetResponseDto.setTitle(task.getTitle());
        taskGetResponseDto.setDescription(task.getDescription());
        taskGetResponseDto.setStatus(task.getStatus().getName());
        return taskGetResponseDto;
    }

    private String getValue(String currentValue, String newValue) {
        return newValue != null ? newValue : currentValue;
    }
}
