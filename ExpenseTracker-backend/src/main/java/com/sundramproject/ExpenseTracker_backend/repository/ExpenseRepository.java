package com.sundramproject.ExpenseTracker_backend.repository;

import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUser(User user);
}
