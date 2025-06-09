package com.MedOnTime.userManagementService.service;

import com.MedOnTime.userManagementService.dto.Roles;
import com.MedOnTime.userManagementService.dto.UserDTO;
import com.MedOnTime.userManagementService.repository.UserManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagemetService{

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Autowired
    private PharmacyServiceClient pharmacyServiceClient;

    @Override
    public String userRegistration(UserDTO userDetails) {

        Roles userRole = userDetails.getRoles();

        if (userRole.equals(Roles.PHARMACIST)){
            String secretKey = userDetails.getSecretKey();
            String encodedSecretKey = pharmacyServiceClient.getSecretKeyByPharmacyId(userDetails.getPharmacyId());
            if (!passwordEncoder.matches(secretKey,encodedSecretKey)){
                throw new RuntimeException("Invalid secret key, please check again");
            }
        }

        UserDTO userDTOWithHashedPassword = userDetails;

        userDTOWithHashedPassword.setPassword(passwordEncoder.encode(userDetails.getPassword()));

        return userManagementRepository.registerUser(userDTOWithHashedPassword);
    }

    @Override
    public boolean checkUserByEmail(String email){
        return userManagementRepository.checkUserByEmail(email);
    }

    @Override
    public UserDTO getUserDetailsByEmail(String email){
        return userManagementRepository.getUserDetailsByEmail(email);
    }

    @Override
    public UserDTO getUserDetailsById(String userId){
        return userManagementRepository.getUserDetailsByUserId(Integer.parseInt(userId));
    }

    @Override
    public String updateUser(String userId, UserDTO UpdateUserDetails) throws Exception {

        UserDTO userDTOWithHashedPassword = UpdateUserDetails;

        userDTOWithHashedPassword.setPassword(passwordEncoder.encode(UpdateUserDetails.getPassword()));

        return userManagementRepository.updateUser(Integer.parseInt(userId), userDTOWithHashedPassword);
    }
}
