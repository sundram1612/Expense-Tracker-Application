package com.sundramproject.expensetracker.service;

import com.sundramproject.expensetracker.integration.kafka.NotificationKafkaProducer;
import com.sundramproject.expensetracker.model.dto.NotificationDTO;
import com.sundramproject.expensetracker.model.entity.Expense;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.repository.ExpenseRepository;
import com.sundramproject.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final NotificationKafkaProducer notificationProducer;

    public User updateBudget(User user, Double amount) {
        user.setMonthlyBudget(amount);
        return userRepository.save(user);
    }

    public void checkAndNotifyBudgetExceed(User user) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Expense> thisMonthExpenses = expenseRepository.findByUserIdAndTypeAndDateBetween(user.getId(), "EXPENSE", start, end);
        double totalSpent = thisMonthExpenses.stream().mapToDouble(Expense::getAmount).sum();

        if (user.getMonthlyBudget() != null && totalSpent > user.getMonthlyBudget()) {
            notificationProducer.sendNotification(new NotificationDTO(
                    user.getId(),
                    "Budget Alert",
                    "Warning! You have exceeded your monthly budget of ₹" + user.getMonthlyBudget() + ". Total spent: ₹" + totalSpent,
                    "WARNING"
            ));
        }
    }
}
