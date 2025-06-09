package com.medOnTime.authService.service;

import com.medOnTime.authService.dto.LoginResponse;

import java.util.HashMap;

public interface AutheService {

    LoginResponse login(HashMap<String,String> request);

}
