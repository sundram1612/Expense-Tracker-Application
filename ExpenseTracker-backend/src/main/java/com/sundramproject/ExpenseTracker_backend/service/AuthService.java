package com.sundramproject.ExpenseTracker_backend.service;

import com.sundramproject.ExpenseTracker_backend.Security.JwtUtil;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<String> sendResetLinkToUser(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Email Not Found");
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password/"+token;
        emailService.sendMail(user.getEmail(), user.getName(), resetLink);
        return ResponseEntity.ok("Reset link sent to your email.");

    }

    public ResponseEntity<String> resetPassword(String token, String newPassword){
        User user = userRepository.findByResetToken(token)
                .orElse(null);

        if(user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired token");
        }

        if(user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Token expired. Please request a new password reset link.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password Reset Successfully.");
    }
}
