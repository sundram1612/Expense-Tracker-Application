package com.sundramproject.expensetracker.controller;

import com.sundramproject.expensetracker.model.dto.ApiResponse;
import com.sundramproject.expensetracker.model.dto.LoginRequest;
import com.sundramproject.expensetracker.model.dto.RegisterRequest;
import com.sundramproject.expensetracker.model.entity.RefreshToken;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.repository.UserRepository;
import com.sundramproject.expensetracker.security.JwtUtil;
import com.sundramproject.expensetracker.service.AuthService;
import com.sundramproject.expensetracker.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists!"));
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("User Registered Successfully.", "Success"));
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid Credentials"));
        }
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        String token = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(ApiResponse.success("Login Successful", Map.of(
                "token", token,
                "refreshToken", refreshToken.getToken(),
                "expiresIn", 600,
                "user", Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                ))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return ResponseEntity.ok(ApiResponse.success("User Fetched Successfully", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        )));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));
        }

        ResponseEntity<String> response = authService.sendResetLinkToUser(email);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(ApiResponse.success(response.getBody(), null));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(ApiResponse.error(response.getBody()));
        }
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable("token") String token, @RequestBody Map<String, String> request) {
        ResponseEntity<String> response = authService.resetPassword(token, request.get("newPassword"));
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(ApiResponse.success(response.getBody(), null));
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(ApiResponse.error(response.getBody()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token missing."));
        }
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid Refresh Token."));
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User Not Found."));
        }

        User user = userOptional.get();
        String newAccessToken = jwtUtil.generateToken(user);

        return ResponseEntity.ok(ApiResponse.success("Token Refreshed", Map.of("accessToken", newAccessToken)));

    }
}
