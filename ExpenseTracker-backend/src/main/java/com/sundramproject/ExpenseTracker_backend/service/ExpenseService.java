package com.sundramproject.ExpenseTracker_backend.service;

import com.sundramproject.ExpenseTracker_backend.Dto.ExpenseDTO;
import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.exception.ResourceNotFoundException;
import com.sundramproject.ExpenseTracker_backend.repository.ExpenseRepository;
import com.sundramproject.ExpenseTracker_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
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


    private User getLoggedInUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User Not Found with email: " + email));
        } catch (Exception e) {
            throw new RuntimeException("Authentication error: " + e.getMessage());
        }
    }


    public List<ExpenseDTO> getAllExpenses(){
        User user = getLoggedInUser();
        List<Expense> expenses = expenseRepository.findByUserId(user.getId());

        return expenses.stream()
                .map(expense -> new ExpenseDTO(
                        expense.getId(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getDate()
                ))
                .collect(Collectors.toList());
    }

    public ExpenseDTO addExpense(Expense expense) {
        User user = getLoggedInUser();
        expense.setUser(user);
        Expense savedExpense = expenseRepository.save(expense);

        return new ExpenseDTO(
                savedExpense.getId(),
                savedExpense.getDescription(),
                savedExpense.getAmount(),
                savedExpense.getCategory(),
                savedExpense.getDate()
        );
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

    public Page<ExpenseDTO> getAllSearchedExpenses(int page, int size, String sortBy, String direction, String search){
        User user = getLoggedInUser();
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Expense> expensePage;

        if(search != null && !search.trim().isEmpty()){
            expensePage = expenseRepository.searchExpensesByUserId(user.getId(), search, pageable);
        }
        else {
            expensePage = expenseRepository.findByUserId(user.getId(), pageable);
        }

        return expensePage.map(expense -> new ExpenseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate()
        ));
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Not found with id: "+id));

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setCategory(expenseDTO.getCategory());
        expense.setDate(expenseDTO.getDate());

        Expense updated = expenseRepository.save(expense);

        return new ExpenseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate()
        );
    }
}
