package com.MedOnTime.userManagementService.controller;

import com.MedOnTime.userManagementService.dto.UserDTO;
import com.MedOnTime.userManagementService.service.UserManagemetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserManagementController {

    @Autowired
    private UserManagemetService userManagemetService;

    @PostMapping("/register")
    public String userRegistration(@RequestBody UserDTO userDetails){
        return userManagemetService.userRegistration(userDetails);
    }

    @PostMapping("/update")
    public String userUpdate(){
        return null;
    }

    @PostMapping("checkUserByEmail")
    public boolean checkUserByEmail(@RequestBody String email){
        return userManagemetService.checkUserByEmail(email);
    }

    @PostMapping("/getUserDetailsByEmail")
    public UserDTO getUserDetailsByEmails(@RequestBody String email){
        return userManagemetService.getUserDetailsByEmail(email);
    }

}
