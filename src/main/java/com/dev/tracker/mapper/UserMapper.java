package com.dev.tracker.mapper;

import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserPutDto;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private final TaskMapper taskMapper;

    public User convertUserRegistrationDtoToUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setPassword(userRegistrationDto.getPassword());
        return user;
    }

    public UserResponseDto convertUserToResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        return userResponseDto;
    }

    public User convertUserPutDtoToUser(User user, UserPutDto userPutDto) {
        user.setEmail(getValue(user.getEmail(), userPutDto.getNewEmail()));
        user.setFirstName(getValue(user.getFirstName(), userPutDto.getFirstName()));
        user.setLastName(getValue(user.getLastName(), userPutDto.getLastName()));
        return user;
    }

    private String getValue(String currentData, String newData) {
        return newData != null ? newData : currentData;
    }

    public UserGetResponseDto convertUserToUserGetResponseDto(User user) {
        UserGetResponseDto userGetResponseDto = new UserGetResponseDto();
        userGetResponseDto.setEmail(user.getEmail());
        userGetResponseDto.setFirstName(user.getFirstName());
        userGetResponseDto.setLastName(user.getLastName());
        List<TaskGetResponseDto> tasks = user.getTasks().stream()
                .map(taskMapper::convertTaskToTaskGetResponseDto)
                .collect(Collectors.toList());
        userGetResponseDto.setTasks(tasks);
        return userGetResponseDto;
    }
}
