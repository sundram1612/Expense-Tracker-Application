package com.sundramproject.expensetracker.service;

import com.sundramproject.expensetracker.model.dto.DashboardDTO;
import com.sundramproject.expensetracker.model.dto.ExpenseDTO;
import com.sundramproject.expensetracker.model.entity.Expense;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;

    public DashboardDTO getDashboardData(User user, Integer year, Integer month) {
        List<Expense> allTx = expenseRepository.findByUserId(user.getId());

        YearMonth currentMonth;
        if (year != null && month != null) {
            currentMonth = YearMonth.of(year, month);
        } else {
            currentMonth = YearMonth.now();
        }
        YearMonth previousMonth = currentMonth.minusMonths(1);

        double totalIncome = 0.0;
        double totalExpense = 0.0;
        double thisMonthExpense = 0.0;
        double thisMonthIncome = 0.0;
        double prevMonthExpense = 0.0;
        double prevMonthIncome = 0.0;

        Map<String, Double> expenseBreakdownMap = new HashMap<>();
        Map<String, Double> incomeByMonth = new HashMap<>();
        Map<String, Double> expenseByMonth = new HashMap<>();
        Map<String, Integer> heatmapMap = new HashMap<>();

        for (Expense tx : allTx) {
            String type = tx.getType() != null ? tx.getType() : "EXPENSE";
            double amount = tx.getAmount();
            LocalDate date = tx.getDate();

            YearMonth txMonth = YearMonth.from(date);
            String monthLabel = txMonth.getMonth().name().substring(0, 3);

            heatmapMap.put(date.toString(), heatmapMap.getOrDefault(date.toString(), 0) + 1);

            if (type.equalsIgnoreCase("INCOME")) {
                totalIncome += amount;
                incomeByMonth.put(monthLabel, incomeByMonth.getOrDefault(monthLabel, 0.0) + amount);
                if (txMonth.equals(currentMonth)) {
                    thisMonthIncome += amount;
                } else if (txMonth.equals(previousMonth)) {
                    prevMonthIncome += amount;
                }
            } else {
                totalExpense += amount;
                String category = tx.getCategory() != null ? tx.getCategory() : "Other";
                expenseBreakdownMap.put(category, expenseBreakdownMap.getOrDefault(category, 0.0) + amount);
                expenseByMonth.put(monthLabel, expenseByMonth.getOrDefault(monthLabel, 0.0) + amount);
                if (txMonth.equals(currentMonth)) {
                    thisMonthExpense += amount;
                } else if (txMonth.equals(previousMonth)) {
                    prevMonthExpense += amount;
                }
            }
        }

        double totalBalance = totalIncome - totalExpense;
        double budget = user.getMonthlyBudget() != null ? user.getMonthlyBudget() : 0;
        double budgetRemaining = Math.max(0, budget - thisMonthExpense);
        double budgetUsedPercentage = budget > 0 ? (thisMonthExpense / budget) * 100 : 0;

        double incomePercentageChange = prevMonthIncome == 0 ? (thisMonthIncome > 0 ? 100.0 : 0.0) : ((thisMonthIncome - prevMonthIncome) / prevMonthIncome) * 100.0;
        double expensePercentageChange = prevMonthExpense == 0 ? (thisMonthExpense > 0 ? 100.0 : 0.0) : ((thisMonthExpense - prevMonthExpense) / prevMonthExpense) * 100.0;
        double thisMonthBalance = thisMonthIncome - thisMonthExpense;
        double prevMonthBalance = prevMonthIncome - prevMonthExpense;
        double balancePercentageChange = prevMonthBalance == 0 ? (thisMonthBalance > 0 ? 100.0 : (thisMonthBalance < 0 ? -100.0 : 0.0)) : ((thisMonthBalance - prevMonthBalance) / Math.abs(prevMonthBalance)) * 100.0;

        List<Map<String, Object>> breakdownList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expenseBreakdownMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", entry.getKey());
            map.put("amount", entry.getValue());
            breakdownList.add(map);
        }

        List<Map<String, Object>> incomeVsExpenseList = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth m = currentMonth.minusMonths(i);
            String label = m.getMonth().name().substring(0, 3);
            Map<String, Object> map = new HashMap<>();
            map.put("month", label);
            map.put("income", incomeByMonth.getOrDefault(label, 0.0));
            map.put("expense", expenseByMonth.getOrDefault(label, 0.0));
            incomeVsExpenseList.add(map);
        }

        List<ExpenseDTO> recent = allTx.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(5)
                .map(tx -> new ExpenseDTO(tx.getId(), tx.getDescription(), tx.getAmount(), tx.getCategory(), tx.getType(), tx.getPaymentMethod(), tx.getDate()))
                .collect(Collectors.toList());

        List<Map<String, Object>> smartInsights = new ArrayList<>();
        if (budgetUsedPercentage >= 80) {
            smartInsights.add(Map.of("type", "warning", "title", "Overspending Alert", "message", "You're nearing your budget limit!"));
        } else {
            smartInsights.add(Map.of("type", "success", "title", "Looking Good", "message", "You're well within your budget."));
        }
        smartInsights.add(Map.of("type", "info", "title", "Insight", "message", "You spent most on " +
                (breakdownList.isEmpty() ? "Nothing yet" : Collections.max(breakdownList, Comparator.comparing(m -> (Double) m.get("amount"))).get("category"))));

        return new DashboardDTO(
                totalBalance,
                thisMonthExpense,
                totalIncome,
                budgetRemaining,
                Math.min(100.0, budgetUsedPercentage),
                incomePercentageChange,
                expensePercentageChange,
                balancePercentageChange,
                breakdownList,
                incomeVsExpenseList,
                recent,
                smartInsights,
                heatmapMap
        );
    }
}
