package com.dev.tracker.model.dto.task;

import lombok.Data;

@Data
public class TaskGetResponseDto {
    private String title;
    private String description;
    private String status;
}
