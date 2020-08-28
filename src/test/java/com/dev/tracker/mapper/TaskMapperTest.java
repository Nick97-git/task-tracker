package com.dev.tracker.mapper;

import com.dev.tracker.exception.TaskStatusException;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.TaskStatus;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskPostDto;
import com.dev.tracker.model.dto.task.TaskPutDto;
import com.dev.tracker.model.dto.task.TaskPutResponseDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaskMapperTest {
    private TaskMapper taskMapper;
    private Task task;
    private TaskPostDto taskPostDto;
    private TaskPutDto taskPutDto;

    @BeforeEach
    public void setUp() {
        taskMapper = new TaskMapper();
        setTask();
        setTaskCreationDto();
        setTaskPutDto();
    }

    @Test
    public void checkConvertToTaskIsOk() {
        Task expected = new Task();
        User user = new User();
        user.setEmail("Email");
        expected.setUser(user);
        expected.setStatus(TaskStatus.DONE);
        expected.setDescription("Description");
        expected.setTitle("Title");
        User userForCreation = new User();
        userForCreation.setEmail(taskPostDto.getEmail());
        Task actual = taskMapper.convertTaskCreationDtoToTask(taskPostDto, userForCreation);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertToTaskWithWrongStatus() {
        taskPostDto.setStatus("status");
        Assertions.assertThrows(TaskStatusException.class,
                () -> taskMapper.convertTaskCreationDtoToTask(taskPostDto, null));
    }

    @Test
    public void checkConvertToTaskResponseDtoIsOk() {
        TaskResponseDto expected = new TaskResponseDto();
        expected.setStatus(TaskStatus.DONE.getName());
        expected.setTitle("Title");
        expected.setEmail("Email");
        TaskResponseDto actual = taskMapper.convertTaskToTaskResponseDto(task);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertToTaskResponseDtoWithNullUser() {
        task.setUser(null);
        Assertions.assertThrows(NullPointerException.class,
                () -> taskMapper.convertTaskToTaskResponseDto(task));
    }

    @Test
    public void checkConvertTaskPutDtoToTaskIsOk() {
        Task expected = new Task();
        expected.setTitle("New title");
        User user = new User();
        user.setEmail("Email");
        expected.setUser(user);
        expected.setStatus(TaskStatus.IN_PROGRESS);
        Task actual = taskMapper.convertTaskPutDtoToTask(task, taskPutDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertTaskToTaskPutResponseDtoIsOk() {
        TaskPutResponseDto expected = new TaskPutResponseDto();
        expected.setTitle("Title");
        expected.setStatus(TaskStatus.DONE.getName());
        expected.setEmail("Email");
        TaskPutResponseDto actual = taskMapper
                .convertTaskToTaskPutResponseDto(task);
        Assertions.assertEquals(expected, actual);
    }

    private void setTaskPutDto() {
        taskPutDto = new TaskPutDto();
        taskPutDto.setCurrentTitle("Title");
        taskPutDto.setNewTitle("New title");
        taskPutDto.setNewStatus(TaskStatus.IN_PROGRESS.getName());
    }

    private void setTaskCreationDto() {
        taskPostDto = new TaskPostDto();
        taskPostDto.setEmail("Email");
        taskPostDto.setStatus("Done");
        taskPostDto.setDescription("Description");
        taskPostDto.setTitle("Title");
    }

    private void setTask() {
        task = new Task();
        task.setStatus(TaskStatus.DONE);
        task.setTitle("Title");
        User user = new User();
        user.setEmail("Email");
        task.setUser(user);
    }
}
