package com.sundramproject.expensetracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    private Long userId;
    private String reportType; // "Spending Report", "Income Report", "Income vs Expense"
    private LocalDate from;
    private LocalDate to;
    private String format; // "pdf", "excel"
}
