package com.MedOnTime.userManagementService.service;

import com.MedOnTime.userManagementService.dto.UserDTO;
import com.MedOnTime.userManagementService.repository.UserManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagemetService{

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Override
    public String userRegistration(UserDTO userDetails) {

        UserDTO userDTOWithHashedPassword = userDetails;

        userDTOWithHashedPassword.setPassword(passwordEncoder.encode(userDetails.getPassword()));

        return userManagementRepository.registerUser(userDTOWithHashedPassword);
    }
}
