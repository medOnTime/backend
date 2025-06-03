package com.medOnTime.authService.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}

