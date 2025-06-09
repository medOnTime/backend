package com.medOnTime.emailService.controller;

import com.medOnTime.emailService.dto.EmailRequestDTO;
import com.medOnTime.emailService.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO request) {
        emailService.sendEmail(request);
        return ResponseEntity.ok("Email sent!");
    }
}

