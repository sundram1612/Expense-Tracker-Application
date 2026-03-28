package com.sundramproject.expensetracker.service;

import com.sundramproject.expensetracker.integration.kafka.NotificationKafkaProducer;
import com.sundramproject.expensetracker.model.dto.DashboardDTO;
import com.sundramproject.expensetracker.model.dto.ExpenseDTO;
import com.sundramproject.expensetracker.model.dto.NotificationDTO;
import com.sundramproject.expensetracker.model.entity.Expense;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.exception.ResourceNotFoundException;
import com.sundramproject.expensetracker.repository.ExpenseRepository;
import com.sundramproject.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final NotificationKafkaProducer notificationProducer;
    private final BudgetService budgetService;
    private final DashboardService dashboardService;

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with email: " + email));
    }

    public List<ExpenseDTO> getAllExpenses() {
        User user = getLoggedInUser();
        List<Expense> expenses = expenseRepository.findByUserId(user.getId());

        return expenses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseDTO addExpense(Expense expense) {
        User user = getLoggedInUser();
        expense.setUser(user);
        if (expense.getType() == null) {
            expense.setType("EXPENSE");
        }
        Expense savedExpense = expenseRepository.save(expense);

        notificationProducer.sendNotification(new NotificationDTO(
                user.getId(),
                "Activity Update",
                "New " + savedExpense.getType().toLowerCase() + " of ₹" + savedExpense.getAmount() + " added in " + savedExpense.getCategory(),
                "INFO"
        ));

        if ("EXPENSE".equalsIgnoreCase(savedExpense.getType())) {
            budgetService.checkAndNotifyBudgetExceed(user);
        }

        return convertToDTO(savedExpense);
    }

    public void deleteExpense(Long id) {
        User user = getLoggedInUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Not Found with id: " + id));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete");
        }
        expenseRepository.delete(expense);
    }

    public Page<ExpenseDTO> getAllSearchedExpenses(int page, int size, String sortBy, String direction, String search) {
        User user = getLoggedInUser();
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Expense> expensePage;
        if (search != null && !search.trim().isEmpty()) {
            expensePage = expenseRepository.searchExpensesByUserId(user.getId(), search, pageable);
        } else {
            expensePage = expenseRepository.findByUserId(user.getId(), pageable);
        }

        return expensePage.map(this::convertToDTO);
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Not found with id: " + id));

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setCategory(expenseDTO.getCategory());
        if (expenseDTO.getType() != null) {
            expense.setType(expenseDTO.getType());
        }
        if (expenseDTO.getPaymentMethod() != null) {
            expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        }
        expense.setDate(expenseDTO.getDate());

        Expense updated = expenseRepository.save(expense);
        return convertToDTO(updated);
    }

    public User updateBudget(Double amount) {
        User user = getLoggedInUser();
        return budgetService.updateBudget(user, amount);
    }

    public DashboardDTO getDashboardData(Integer year, Integer month) {
        User user = getLoggedInUser();
        return dashboardService.getDashboardData(user, year, month);
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getType(),
                expense.getPaymentMethod(),
                expense.getDate()
        );
    }
}
