package com.MedOnTime.userManagementService.controller;

import com.MedOnTime.userManagementService.dto.UserDTO;
import com.MedOnTime.userManagementService.service.UserManagemetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("userManagementService")
public class UserManagementController {

    @Autowired
    private UserManagemetService userManagemetService;

    @PostMapping("/register")
    public String userRegistration(@RequestBody UserDTO userDetails){
        return userManagemetService.userRegistration(userDetails);
    }

}
