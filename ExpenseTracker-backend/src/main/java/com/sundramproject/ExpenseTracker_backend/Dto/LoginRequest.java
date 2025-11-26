package com.sundramproject.ExpenseTracker_backend.Dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
