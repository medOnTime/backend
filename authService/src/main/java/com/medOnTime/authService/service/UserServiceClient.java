package com.medOnTime.authService.service;

import com.medOnTime.authService.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean userCheckByEmail(String email) {
        String url = userServiceUrl + "/checkUserByEmail";
        return restTemplate.postForObject(url, email, Boolean.class);
    }

    public UserDTO getUserDetailsByEmail(String email) {
        String url = userServiceUrl + "/getUserDetailsByEmail";
        return restTemplate.postForObject(url, email, UserDTO.class);
    }

}
