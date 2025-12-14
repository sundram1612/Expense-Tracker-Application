package com.sundramproject.ExpenseTracker_backend.controller;

import com.sundramproject.ExpenseTracker_backend.Dto.ExpenseDTO;
import com.sundramproject.ExpenseTracker_backend.Security.JwtUtil;
import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.repository.ExpenseRepository;
import com.sundramproject.ExpenseTracker_backend.repository.UserRepository;
import com.sundramproject.ExpenseTracker_backend.service.ExpenseService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/expenses")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {


    @Autowired
    private ExpenseService expenseService;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @Getter
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
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-sort")
    public ResponseEntity<Page<ExpenseDTO>> getSearchedExpense(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "") String search
    ){
        Page<ExpenseDTO> expensePage = expenseService.getAllSearchedExpenses(page, size, sortBy, direction, search);
        return ResponseEntity.ok(expensePage);
    }

    @PutMapping("/update-expense/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(updatedExpense);
    }

}
