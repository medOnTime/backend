package com.medOnTime.emailService.service;

import com.medOnTime.emailService.dto.EmailRequestDTO;


public interface EmailService {
    void sendEmail(EmailRequestDTO request);
}
