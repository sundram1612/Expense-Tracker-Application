package com.sundramproject.ExpenseTracker_backend.controller;

import com.sundramproject.ExpenseTracker_backend.Dto.LoginRequest;
import com.sundramproject.ExpenseTracker_backend.Dto.RegisterRequest;
import com.sundramproject.ExpenseTracker_backend.Security.JwtUtil;
import com.sundramproject.ExpenseTracker_backend.entity.RefreshToken;
import com.sundramproject.ExpenseTracker_backend.entity.User;
import com.sundramproject.ExpenseTracker_backend.repository.UserRepository;
import com.sundramproject.ExpenseTracker_backend.service.AuthService;
import com.sundramproject.ExpenseTracker_backend.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists!"));
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User Registered Successfully."));
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (Exception e){
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Credentials"));
        }
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        String token = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "refreshToken", refreshToken.getToken(),
                "expiresIn", 600,
                "user", Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                )));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));


        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request){
        String email= request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        return authService.sendResetLinkToUser(email);
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable("token") String token, @RequestBody Map<String, String> request){
        return authService.resetPassword(token, request.get("newPassword"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");

        if(refreshToken == null){
            return ResponseEntity.badRequest().body("Refresh token missing.");
        }
        if(!jwtUtil.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found.");
        }

        User user = userOptional.get();
        String newAccessToken = jwtUtil.generateToken(user);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

    }
}
