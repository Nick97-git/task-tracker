package com.dev.tracker.mapper;

import com.dev.tracker.model.Task;
import com.dev.tracker.model.TaskStatus;
import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserPutDto;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {
    private UserMapper userMapper;
    private UserRegistrationDto userRegistrationDto;
    private UserPutDto userPutDto;
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
        expected.setEmail("email");
        expected.setFirstName("first");
        expected.setLastName("last");
        User actual = userMapper.convertUserRegistrationDtoToUser(userRegistrationDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertToUserResponseDtoIsOk() {
        UserResponseDto expected = new UserResponseDto();
        expected.setEmail("email");
        expected.setFirstName("first");
        expected.setLastName("last");
        UserResponseDto actual = userMapper.convertUserToResponseDto(user);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertUserPutDtoToUserIsOk() {
        User expected = new User();
        expected.setEmail("newEmail");
        expected.setFirstName("first");
        expected.setLastName("last");
        User actual = userMapper.convertUserPutDtoToUser(user, userPutDto);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkConvertUserToUserGetResponseDtoIsOk() {
        Task firstTask = new Task();
        firstTask.setTitle("title");
        firstTask.setStatus(TaskStatus.VIEW);
        Task secondTask = new Task();
        secondTask.setTitle("title");
        secondTask.setStatus(TaskStatus.DONE);
        Set<Task> tasks = Set.of(firstTask, secondTask);
        user.setTasks(tasks);
        UserGetResponseDto expected = new UserGetResponseDto();
        expected.setEmail("email");
        expected.setFirstName("first");
        expected.setLastName("last");
        TaskGetResponseDto firstTaskGetResponseDto = new TaskGetResponseDto();
        firstTaskGetResponseDto.setTitle("title");
        firstTaskGetResponseDto.setStatus(TaskStatus.VIEW.getName());
        TaskGetResponseDto secondTaskGetResponseDto = new TaskGetResponseDto();
        secondTaskGetResponseDto.setTitle("title");
        secondTaskGetResponseDto.setStatus(TaskStatus.DONE.getName());
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
        userRegistrationDto.setEmail("email");
        userRegistrationDto.setFirstName("first");
        userRegistrationDto.setLastName("last");
    }

    private void setUser() {
        user = new User();
        user.setEmail("email");
        user.setFirstName("first");
        user.setLastName("last");
    }

    private void setUserPutDto() {
        userPutDto = new UserPutDto();
        userPutDto.setEmail("email");
        userPutDto.setNewEmail("newEmail");
    }

}
