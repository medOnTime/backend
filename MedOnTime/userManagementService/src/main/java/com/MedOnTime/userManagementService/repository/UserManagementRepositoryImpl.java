package com.MedOnTime.userManagementService.repository;

import com.MedOnTime.userManagementService.dto.Roles;
import com.MedOnTime.userManagementService.dto.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class UserManagementRepositoryImpl implements UserManagementRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public String registerUser(UserDTO userDetails) {
        // Validate mandatory fields
        if (userDetails.getUserName() == null || userDetails.getEmail() == null ||
                userDetails.getPassword() == null || userDetails.getPhoneNumber() == null ||
                userDetails.getRoles() == null) {
            throw new IllegalArgumentException("Missing required user registration fields");
        }

        // If role is PHARMACIST, pharmacyId is required
        if (userDetails.getRoles() == Roles.PHARMACIST && userDetails.getPharmacyId() == null) {
            throw new IllegalArgumentException("Pharmacy ID is required for pharmacists");
        }

        // Current timestamp
        String now = LocalDateTime.now().toString();

        Query query = entityManager.createNativeQuery(
                "INSERT INTO user_table " +
                        "(user_name, user_email, password, phone_number, role, create_at, pharmacy_id) " +
                        "VALUES (:userName, :email, :password, :phoneNumber, :role, :createAt, :pharmacyId)"
        );

        query.setParameter("userName", userDetails.getUserName());
        query.setParameter("email", userDetails.getEmail());
        query.setParameter("password", userDetails.getPassword());
        query.setParameter("phoneNumber", userDetails.getPhoneNumber());
        query.setParameter("role", userDetails.getRoles());
        query.setParameter("createAt", now);
        query.setParameter("pharmacyId", userDetails.getPharmacyId());

        query.executeUpdate();

        return "User registered successfully";
    }


}
