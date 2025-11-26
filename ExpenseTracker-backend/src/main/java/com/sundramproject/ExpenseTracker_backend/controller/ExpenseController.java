package com.sundramproject.ExpenseTracker_backend.controller;

import com.sundramproject.ExpenseTracker_backend.Security.JwtUtil;
import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.repository.ExpenseRepository;
import com.sundramproject.ExpenseTracker_backend.repository.UserRepository;
import com.sundramproject.ExpenseTracker_backend.service.ExpenseService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/expenses")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {

    @Getter
    @Setter
    private ExpenseService expenseService;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final JwtUtil jwtUtil;

    @GetMapping()
    public List<Expense> getAllExpenses(Principal principal){
        String username = principal.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User Not Found."));

        return expenseRepository.findByUser(user);
    }

    @PostMapping()
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense, Principal principal){
        String username = principal.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        expense.setUser(user);
        Expense saved = expenseRepository.save(expense);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, Principal principal){
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense Not Found"));

        String username = principal.getName();
        if(!expense.getUser().getEmail().equals(username)){
            return ResponseEntity.status(403).body("Not Allowed");
        }

        expenseRepository.deleteById(id);
//        return ResponseEntity.ok("Expense Deleted.");
        return ResponseEntity.noContent().build();
    }



}
