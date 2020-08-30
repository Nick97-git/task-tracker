package com.dev.tracker.model.dto.task;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskUpdateDto {
    @NotBlank(message = "Title can't be null or blank!")
    private String title;
    @NotBlank(message = "Email can't be null or blank!")
    private String email;
    private String newTitle;
    private String newDescription;
    private String newStatus;
}
