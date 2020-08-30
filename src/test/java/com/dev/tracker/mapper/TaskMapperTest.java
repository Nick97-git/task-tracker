package com.dev.tracker.mapper;

import com.dev.tracker.exception.TaskStatusException;
import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskCreateDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import com.dev.tracker.model.dto.task.TaskUpdateDto;
import com.dev.tracker.model.dto.task.TaskUpdateResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaskMapperTest {
    private TaskMapper taskMapper;
    private Task task;
    private TaskCreateDto taskCreateDto;
    private TaskUpdateDto taskUpdateDto;

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
        user.setEmail("email@ukr.net");
        expected.setUser(user);
        expected.setStatus(Task.TaskStatus.DONE);
        expected.setDescription("description");
        expected.setTitle("title");
        User userForCreation = new User();
        userForCreation.setEmail(taskCreateDto.getEmail());
        Task actual = taskMapper.convertTaskPostDtoToTask(taskCreateDto, userForCreation);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertToTaskWithWrongStatus() {
        taskCreateDto.setStatus("status");
        Assertions.assertThrows(TaskStatusException.class,
                () -> taskMapper.convertTaskPostDtoToTask(taskCreateDto, null));
    }

    @Test
    public void checkConvertToTaskResponseDtoIsOk() {
        TaskResponseDto expected = new TaskResponseDto();
        expected.setStatus(Task.TaskStatus.DONE.getName());
        expected.setTitle("title");
        expected.setEmail("email@ukr.net");
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
        expected.setTitle("new title");
        User user = new User();
        user.setEmail("email@ukr.net");
        expected.setUser(user);
        expected.setStatus(Task.TaskStatus.IN_PROGRESS);
        Task actual = taskMapper.convertTaskPutDtoToTask(task, taskUpdateDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertTaskToTaskPutResponseDtoIsOk() {
        TaskUpdateResponseDto expected = new TaskUpdateResponseDto();
        expected.setTitle("title");
        expected.setStatus(Task.TaskStatus.DONE.getName());
        expected.setEmail("email@ukr.net");
        TaskUpdateResponseDto actual = taskMapper
                .convertTaskToTaskPutResponseDto(task);
        Assertions.assertEquals(expected, actual);
    }

    private void setTaskPutDto() {
        taskUpdateDto = new TaskUpdateDto();
        taskUpdateDto.setTitle("title");
        taskUpdateDto.setNewTitle("new title");
        taskUpdateDto.setNewStatus(Task.TaskStatus.IN_PROGRESS.getName());
    }

    private void setTaskCreationDto() {
        taskCreateDto = new TaskCreateDto();
        taskCreateDto.setEmail("email@ukr.net");
        taskCreateDto.setStatus("done");
        taskCreateDto.setDescription("description");
        taskCreateDto.setTitle("title");
    }

    private void setTask() {
        task = new Task();
        task.setStatus(Task.TaskStatus.DONE);
        task.setTitle("title");
        User user = new User();
        user.setEmail("email@ukr.net");
        task.setUser(user);
    }
}
