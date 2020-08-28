package com.dev.tracker.mapper;

import com.dev.tracker.exception.TaskStatusException;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.TaskStatus;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.task.TaskPostDto;
import com.dev.tracker.model.dto.task.TaskPutDto;
import com.dev.tracker.model.dto.task.TaskPutResponseDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task convertTaskCreationDtoToTask(TaskPostDto taskPostDto, User user) {
        Task task = new Task();
        task.setTitle(taskPostDto.getTitle());
        task.setDescription(taskPostDto.getDescription());
        task.setStatus(getStatus(taskPostDto.getStatus()));
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

    public Task convertTaskPutDtoToTask(Task task, TaskPutDto taskPutDto) {
        task.setStatus(getStatus(getData(task.getStatus().getName(), taskPutDto.getNewStatus())));
        task.setTitle(getData(task.getTitle(), taskPutDto.getNewTitle()));
        task.setDescription(getData(task.getDescription(), taskPutDto.getNewDescription()));
        return task;
    }

    @SneakyThrows
    public TaskStatus getStatus(String status) {
        TaskStatus[] taskStatuses = TaskStatus.values();
        for (TaskStatus taskStatus : taskStatuses) {
            if (taskStatus.getName().equalsIgnoreCase(status)) {
                return taskStatus;
            }
        }
        throw new TaskStatusException("There is no such status for task: " + status + "!");
    }

    public TaskPutResponseDto convertTaskToTaskPutResponseDto(Task task) {
        TaskPutResponseDto taskPutResponseDto = new TaskPutResponseDto();
        taskPutResponseDto.setTitle(task.getTitle());
        taskPutResponseDto.setDescription(task.getDescription());
        taskPutResponseDto.setStatus(task.getStatus().getName());
        taskPutResponseDto.setEmail(task.getUser().getEmail());
        return taskPutResponseDto;
    }

    public TaskGetResponseDto convertTaskToTaskGetResponseDto(Task task) {
        TaskGetResponseDto taskGetResponseDto = new TaskGetResponseDto();
        taskGetResponseDto.setTitle(task.getTitle());
        taskGetResponseDto.setDescription(task.getDescription());
        taskGetResponseDto.setStatus(task.getStatus().getName());
        return taskGetResponseDto;
    }

    private String getData(String oldData, String newData) {
        return newData == null ? oldData : newData;
    }
}
