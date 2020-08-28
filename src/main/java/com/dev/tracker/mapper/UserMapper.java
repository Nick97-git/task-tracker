package com.dev.tracker.mapper;

import com.dev.tracker.model.User;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserPutDto;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
import java.util.Set;
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
        user.setEmail(getData(user.getEmail(), userPutDto.getNewEmail()));
        user.setFirstName(getData(user.getFirstName(), userPutDto.getFirstName()));
        user.setLastName(getData(user.getLastName(), userPutDto.getLastName()));
        return user;
    }

    public UserGetResponseDto convertUserToUserGetResponseDto(User user) {
        UserGetResponseDto userGetResponseDto = new UserGetResponseDto();
        userGetResponseDto.setEmail(user.getEmail());
        userGetResponseDto.setFirstName(user.getFirstName());
        userGetResponseDto.setLastName(user.getLastName());
        userGetResponseDto.setTasks(getTasks(user));
        return userGetResponseDto;
    }

    private String getData(String oldData, String newData) {
        return newData != null ? newData : oldData;
    }

    private Set<TaskGetResponseDto> getTasks(User user) {
        return user.getTasks().stream()
                .map(taskMapper::convertTaskToTaskGetResponseDto)
                .collect(Collectors.toSet());
    }
}
