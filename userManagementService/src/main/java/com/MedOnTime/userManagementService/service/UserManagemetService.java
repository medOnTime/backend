package com.MedOnTime.userManagementService.service;

import com.MedOnTime.userManagementService.dto.UserDTO;

import java.util.HashMap;

public interface UserManagemetService {

    String userRegistration(UserDTO userDetails);

    boolean checkUserByEmail(String email);

    UserDTO getUserDetailsByEmail(String email);

    UserDTO getUserDetailsById(String userId);

    String updateUser(String userId, UserDTO UpdateUserDetails) throws Exception;

}
