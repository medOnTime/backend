package com.medOnTime.authService.service;

import com.medOnTime.authService.config.JwtUtil;
import com.medOnTime.authService.dto.LoginResponse;
import com.medOnTime.authService.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthServiceImpl implements AutheService{

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(HashMap<String,String> request) {
        // 1. Get user by email
        UserDTO user = userServiceClient.getUserDetailsByEmail(request.get("email"));

        // 2. Check if user exists
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        // 3. Validate password
        if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 4. Generate JWT
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRoles().name());

        // 5. Return token and basic user info
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(user.getRoles().name());
        return response;
    }



}
