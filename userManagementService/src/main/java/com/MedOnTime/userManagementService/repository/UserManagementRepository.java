package com.MedOnTime.userManagementService.repository;

import com.MedOnTime.userManagementService.dto.UserDTO;

public interface UserManagementRepository {
    String registerUser(UserDTO userDetails);

    boolean checkUserByEmail(String email);

    UserDTO getUserDetailsByEmail(String email);
}
