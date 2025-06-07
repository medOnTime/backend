package com.medOnTime.emailService.dto;

import lombok.Data;

@Data
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String name;
    private String secretKey;
}

