package com.dev.tracker.mapper;

import com.dev.tracker.model.Task;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import com.dev.tracker.model.dto.user.UserUpdateDto;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {
    private UserMapper userMapper;
    private UserRegistrationDto userRegistrationDto;
    private UserUpdateDto userUpdateDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userMapper = new UserMapper(new TaskMapper());
        setUserRegistrationDto();
        setUser();
        setUserPutDto();
    }

    @Test
    public void checkConvertToUserIsOk() {
        User expected = new User();
        expected.setEmail("email@ukr.net");
        expected.setFirstName("first");
        expected.setLastName("last");
        User actual = userMapper.convertUserRegistrationDtoToUser(userRegistrationDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertToUserResponseDtoIsOk() {
        UserResponseDto expected = new UserResponseDto();
        expected.setEmail("email@ukr.net");
        expected.setFirstName("first");
        expected.setLastName("last");
        UserResponseDto actual = userMapper.convertUserToUserResponseDto(user);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertUserPutDtoToUserIsOk() {
        User expected = new User();
        expected.setEmail("new email");
        expected.setFirstName("first");
        expected.setLastName("last");
        User actual = userMapper.convertUserPutDtoToUser(user, userUpdateDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertUserToUserGetResponseDtoIsOk() {
        Task firstTask = new Task();
        firstTask.setTitle("title");
        firstTask.setStatus(Task.TaskStatus.VIEW);
        Task secondTask = new Task();
        secondTask.setTitle("title");
        secondTask.setStatus(Task.TaskStatus.DONE);
        Set<Task> tasks = Set.of(firstTask, secondTask);
        user.setTasks(tasks);
        UserGetResponseDto expected = new UserGetResponseDto();
        expected.setEmail("email@ukr.net");
        expected.setFirstName("first");
        expected.setLastName("last");
        TaskGetResponseDto firstTaskGetResponseDto = new TaskGetResponseDto();
        firstTaskGetResponseDto.setTitle("title");
        firstTaskGetResponseDto.setStatus(Task.TaskStatus.VIEW.getName());
        TaskGetResponseDto secondTaskGetResponseDto = new TaskGetResponseDto();
        secondTaskGetResponseDto.setTitle("title");
        secondTaskGetResponseDto.setStatus(Task.TaskStatus.DONE.getName());
        expected.setTasks(Set.of(firstTaskGetResponseDto, secondTaskGetResponseDto));
        UserGetResponseDto actual = userMapper.convertUserToUserGetResponseDto(user);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertUserToUserGetResponseDtoWithNullList() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            user.setTasks(null);
            userMapper.convertUserToUserGetResponseDto(user);
        });
    }

    private void setUserRegistrationDto() {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("email@ukr.net");
        userRegistrationDto.setFirstName("first");
        userRegistrationDto.setLastName("last");
    }

    private void setUser() {
        user = new User();
        user.setEmail("email@ukr.net");
        user.setFirstName("first");
        user.setLastName("last");
    }

    private void setUserPutDto() {
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("email@ukr.net");
        userUpdateDto.setNewEmail("new email");
    }

}
