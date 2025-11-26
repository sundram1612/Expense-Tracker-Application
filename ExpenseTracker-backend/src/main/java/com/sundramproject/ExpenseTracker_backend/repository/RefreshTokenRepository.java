package com.sundramproject.ExpenseTracker_backend.repository;

import com.sundramproject.ExpenseTracker_backend.entity.RefreshToken;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}
