package com.sundramproject.expensetracker.model.dto;

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
    private String type;
    private String paymentMethod;
    private LocalDate date;
}
