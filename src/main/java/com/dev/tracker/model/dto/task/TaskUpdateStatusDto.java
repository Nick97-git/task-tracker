package com.dev.tracker.model.dto.task;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskUpdateStatusDto {
    @NotBlank(message = "New status can't be null or blank!")
    private String newStatus;
    @NotBlank(message = "Title can't be null or blank!")
    private String title;
    @NotBlank(message = "Email can't be null or blank!")
    private String email;
}
