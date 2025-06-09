package com.medOnTime.pharmacyService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {
        private String to;
        private String subject;
        private String name;
        private String secretKey;

}
