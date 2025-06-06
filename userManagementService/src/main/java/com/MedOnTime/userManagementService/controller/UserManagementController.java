package com.MedOnTime.userManagementService.controller;

import com.MedOnTime.userManagementService.dto.UserDTO;
import com.MedOnTime.userManagementService.service.UserManagemetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserManagementController {

    @Autowired
    private UserManagemetService userManagemetService;

    @PostMapping("/register")
    public String userRegistration(@RequestBody UserDTO userDetails){
        return userManagemetService.userRegistration(userDetails);
    }

    @GetMapping("/getUserDetailsById")
    public UserDTO getMedicineInventoryByUser(HttpServletRequest request){
        String userId = request.getHeader("X-User-Id");
        return userManagemetService.getUserDetailsById(userId);
    }

    @PostMapping("/update")
    public String userUpdate(HttpServletRequest request, @RequestBody UserDTO updateUserDetails) throws Exception{
        String userId = request.getHeader("X-User-Id");
        return userManagemetService.updateUser(userId, updateUserDetails);
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
