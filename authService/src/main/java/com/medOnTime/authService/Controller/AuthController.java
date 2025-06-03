package com.medOnTime.authService.Controller;

import com.medOnTime.authService.dto.LoginResponse;
import com.medOnTime.authService.service.AutheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AutheService autheService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody HashMap<String,String> request) {
        LoginResponse response = autheService.login(request);
        return ResponseEntity.ok(response);
    }

}
