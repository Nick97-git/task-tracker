package com.dev.tracker.model.dto.task;

import lombok.Data;

@Data
public class TaskResponseDto {
    String title;
    String status;
    String userEmail;
}
