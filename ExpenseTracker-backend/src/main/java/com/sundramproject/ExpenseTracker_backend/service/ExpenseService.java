package com.sundramproject.ExpenseTracker_backend.service;

import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.repository.ExpenseRepository;
import com.sundramproject.ExpenseTracker_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getLoggedInUser(){
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public List<Expense> getAllExpenses(){
        User user = getLoggedInUser();
        return expenseRepository.findByUserId(user.getId());
    }

    public Expense addExpense(Expense expense){
        User user = getLoggedInUser();
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id){
        User user = getLoggedInUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense Not Found"));

        if(!expense.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized delete");
        }
        expenseRepository.delete(expense);
    }



}
