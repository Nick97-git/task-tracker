package com.dev.tracker.model.dto.task;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskGetRequestDto {
    @NotBlank(message = "Status can't be null or blank!")
    private String status;
}
