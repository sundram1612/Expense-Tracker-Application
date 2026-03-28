package com.sundramproject.expensetracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private Double totalBalance;
    private Double thisMonthExpense;
    private Double totalIncome;
    private Double budgetRemaining;
    private Double budgetUsedPercentage;
    private Double incomePercentageChange;
    private Double expensePercentageChange;
    private Double balancePercentageChange;
    private List<Map<String, Object>> expenseBreakdown;
    private List<Map<String, Object>> incomeVsExpense; // e.g., [{"month": "Jan", "income": 500, "expense": 300}, ...]
    private List<ExpenseDTO> recentTransactions;
    private List<Map<String, Object>> smartInsights;
    private Map<String, Integer> heatmapData; // e.g., "2024-07-21" -> 1 (or amount)
}
