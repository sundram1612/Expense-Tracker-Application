package com.sundramproject.ExpenseTracker_backend.service;

import com.sundramproject.ExpenseTracker_backend.entity.RefreshToken;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user){
//        refreshTokenRepository.deleteByUser(user);
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(user);
//        refreshToken.setToken(UUID.randomUUID().toString());
//        refreshToken.setExpiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7));  // 7 days
//
//        return refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        RefreshToken refreshToken;
        if(existingToken.isPresent()){
            //update existing token
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7));
        }
        else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7));
        }

        return  refreshTokenRepository.save(refreshToken);

    }
}
