package com.dev.tracker.model.dto.task;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskCreationDto {
    @NotBlank(message = "Title can't be null or blank!")
    private String title;
    @NotBlank(message = "Description can't be null or blank!")
    private String description;
    @NotBlank(message = "Status can't be null or blank!")
    private String status;
    @NotBlank(message = "User email can't be null or blank!")
    private String userEmail;
}