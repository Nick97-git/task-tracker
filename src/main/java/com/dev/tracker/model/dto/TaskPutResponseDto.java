package com.dev.tracker.model.dto;

import lombok.Data;

@Data
public class TaskPutResponseDto {
    private String title;
    private String description;
    private String status;
    private String userEmail;
}
