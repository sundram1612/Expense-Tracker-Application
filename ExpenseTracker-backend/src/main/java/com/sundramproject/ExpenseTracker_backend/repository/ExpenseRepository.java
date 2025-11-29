package com.sundramproject.ExpenseTracker_backend.repository;

import com.sundramproject.ExpenseTracker_backend.entity.Expense;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUser(User user);
    Page<Expense> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND " +
            "(LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.category) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR CAST(e.amount AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Expense> searchExpensesByUserId(@Param("userId") Long userId,
                                         @Param("search") String search,
                                         Pageable pageable);
}
