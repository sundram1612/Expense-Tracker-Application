package com.sundramproject.expensetracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long userId;
    private String title;
    private String message;
    private String type; // "INFO", "SUCCESS", "WARNING", "ERROR"
}
