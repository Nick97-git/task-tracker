package com.dev.tracker.model.dto.task;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskStatusChangeDto {
    @NotBlank(message = "New status can't be null or blank!")
    private String newStatus;
    @NotBlank(message = "Title can't be null or blank!")
    private String title;
    @NotBlank(message = "User email can't be null or blank!")
    private String userEmail;
}
