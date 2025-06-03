package com.medOnTime.authService.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String userId;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private Roles roles; // At least one of: PATIENT, CARETAKER, PHARMACIST
    private String pharmacyId; // Required only if role contains PHARMACIST

}
