package com.MedOnTime.userManagementService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PharmacyServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pharmacy-service.base-url}")
    private String pharmacyServiceUrl;

    public String getSecretKeyByPharmacyId(String pharmacyId) {
        Integer pharmacyIdInt = Integer.parseInt(pharmacyId);
        String url = pharmacyServiceUrl + "/get-pharmacy-key?pharmacyId=" + pharmacyIdInt;
        return restTemplate.getForObject(url, String.class);
    }

}
