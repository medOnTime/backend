package com.MedOnTime.userManagementService.dto;

import lombok.*;

@Data
public class UserDTO {

    private String userId;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private Roles roles; // At least one of: PATIENT, CARETAKER, PHARMACIST
    private String pharmacyId; // Required only if role contains PHARMACIST
    private String secretKey;

}
