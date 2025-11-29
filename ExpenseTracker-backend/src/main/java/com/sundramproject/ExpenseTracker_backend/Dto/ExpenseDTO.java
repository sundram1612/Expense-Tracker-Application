package com.sundramproject.ExpenseTracker_backend.Dto;

import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

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
