package com.MedOnTime.userManagementService.repository;

import com.MedOnTime.userManagementService.dto.Roles;
import com.MedOnTime.userManagementService.dto.UserDTO;
import jakarta.persistence.*;
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

        if (countUsersByEmail(userDetails.getEmail(), null) > 0) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Current timestamp
        LocalDateTime now = LocalDateTime.now();

        Query query = entityManager.createNativeQuery(
                "INSERT INTO user_table " +
                        "(user_name, user_email, password, phone_number, role, create_at, pharmacy_id) " +
                        "VALUES (:userName, :email, :password, :phoneNumber, :role, :createAt, :pharmacyId)"
        );



        query.setParameter("userName", userDetails.getUserName());
        query.setParameter("email", userDetails.getEmail());
        query.setParameter("password", userDetails.getPassword());
        query.setParameter("phoneNumber", userDetails.getPhoneNumber());
        query.setParameter("role", userDetails.getRoles().toString());
        query.setParameter("createAt", now);
        query.setParameter("pharmacyId", userDetails.getPharmacyId());

        query.executeUpdate();

        return "User registered successfully";
    }

    @Override
    public boolean checkUserByEmail(String email) {
        Query query = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM user_table WHERE user_email = :email"
        );
        query.setParameter("email", email);

        Number count = (Number) query.getSingleResult(); // Correct method to get the result

        return count.intValue() > 0;
    }

    @Override
    public UserDTO getUserDetailsByEmail(String email) {
        try {
            Tuple tuple = (Tuple) entityManager.createNativeQuery(
                            "SELECT user_id, user_name, user_email, password, phone_number, role, pharmacy_id " +
                                    "FROM user_table WHERE user_email = :email", Tuple.class)
                    .setParameter("email", email)
                    .getSingleResult();

            UserDTO userDTO = new UserDTO();

            userDTO.setUserId(tuple.get("user_id") == null ? null : tuple.get("user_id").toString());
            userDTO.setUserName(tuple.get("user_name") == null ? null : tuple.get("user_name").toString());
            userDTO.setEmail(tuple.get("user_email") == null ? null : tuple.get("user_email").toString());
            userDTO.setPassword(tuple.get("password") == null ? null : tuple.get("password").toString());
            userDTO.setPhoneNumber(tuple.get("phone_number") == null ? null : tuple.get("phone_number").toString());

            // Handle role mapping
            String roleStr = tuple.get("role") == null ? null : tuple.get("role").toString();
            Roles roleEnum = null;
            if (roleStr != null) {
                try {
                    roleEnum = Roles.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid role value: " + roleStr);
                }
            }
            userDTO.setRoles(roleEnum);

            // Set pharmacyId (was incorrectly set to phoneNumber)
            userDTO.setPharmacyId(tuple.get("pharmacy_id") == null ? null : tuple.get("pharmacy_id").toString());

            return userDTO;

        } catch (NoResultException e) {
            // No user found for the given email
            System.out.println("No user found with email: " + email);
            return null; // Or throw custom exception, based on your logic
        }
    }

    @Override
    public UserDTO getUserDetailsByUserId(int userId) {
        try {
            Tuple tuple = (Tuple) entityManager.createNativeQuery(
                            "SELECT user_id, user_name, user_email, password, phone_number, role, pharmacy_id " +
                                    "FROM user_table WHERE user_id = :userId", Tuple.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            UserDTO userDTO = new UserDTO();

            userDTO.setUserId(tuple.get("user_id") == null ? null : tuple.get("user_id").toString());
            userDTO.setUserName(tuple.get("user_name") == null ? null : tuple.get("user_name").toString());
            userDTO.setEmail(tuple.get("user_email") == null ? null : tuple.get("user_email").toString());
            userDTO.setPassword(tuple.get("password") == null ? null : tuple.get("password").toString());
            userDTO.setPhoneNumber(tuple.get("phone_number") == null ? null : tuple.get("phone_number").toString());

            // Handle role mapping
            String roleStr = tuple.get("role") == null ? null : tuple.get("role").toString();
            Roles roleEnum = null;
            if (roleStr != null) {
                try {
                    roleEnum = Roles.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid role value: " + roleStr);
                }
            }
            userDTO.setRoles(roleEnum);

            // Set pharmacyId (was incorrectly set to phoneNumber)
            userDTO.setPharmacyId(tuple.get("pharmacy_id") == null ? null : tuple.get("pharmacy_id").toString());

            return userDTO;

        } catch (NoResultException e) {
            // No user found for the given email
            System.out.println("No user found with email: " + userId);
            return null; // Or throw custom exception, based on your logic
        }
    }

    @Override
    @Transactional
    public String updateUser(int userId, UserDTO updatesUserDetails) throws Exception {

        UserDTO existingUser = getUserDetailsByUserId(userId);

        if (existingUser != null) {

            if (countUsersByEmail(updatesUserDetails.getEmail(), userId) > 0) {
                throw new IllegalArgumentException("Email is already in use by another user");
            }

            Query query = entityManager.createNativeQuery(
                    "UPDATE user_table SET user_name = :userName, user_email = :userEmail, " +
                            "password = :password, role = :role, last_update_at = :nowDate, pharmacy_id = :pharmacyId " +
                            "WHERE user_id = :userId");

            query.setParameter("userName", updatesUserDetails.getUserName());
            query.setParameter("userEmail", updatesUserDetails.getEmail());
            query.setParameter("password", updatesUserDetails.getPassword());
            query.setParameter("role", updatesUserDetails.getRoles().toString()); // Assuming Roles is Enum
            query.setParameter("nowDate", new java.util.Date());
            query.setParameter("pharmacyId", updatesUserDetails.getPharmacyId());
            query.setParameter("userId", userId);

            query.executeUpdate();

            return "User updated successfully.";
        } else {
            throw new Exception("User not found");
        }
    }

    private long countUsersByEmail(String email, Integer excludeUserId) {
        String queryStr = "SELECT COUNT(*) FROM user_table WHERE user_email = :email";

        // If we are updating a user, exclude their current user ID
        if (excludeUserId != null) {
            queryStr += " AND user_id != :excludeId";
        }

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("email", email);
        if (excludeUserId != null) {
            query.setParameter("excludeId", excludeUserId);
        }

        Object result = query.getSingleResult();
        return ((Number) result).longValue();
    }


}
