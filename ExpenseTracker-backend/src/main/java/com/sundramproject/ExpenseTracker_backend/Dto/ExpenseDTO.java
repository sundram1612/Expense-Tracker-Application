package com.sundramproject.ExpenseTracker_backend.Dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {
    private Long id;
    private String description;
    private Double amount;
    private String category;
    private LocalDate date;
}
