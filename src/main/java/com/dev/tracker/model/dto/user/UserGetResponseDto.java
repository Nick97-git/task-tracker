package com.dev.tracker.model.dto.user;

import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import java.util.List;
import lombok.Data;

@Data
public class UserGetResponseDto {
    private String email;
    private String firstName;
    private String lastName;
    private List<TaskGetResponseDto> tasks;
}
