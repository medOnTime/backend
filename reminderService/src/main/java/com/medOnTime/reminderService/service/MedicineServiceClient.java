package com.medOnTime.reminderService.service;

import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MedicineServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${medicine-service.base-url}")
    private String medicineServiceUrl;


    public String updateInventory(ReminderSchedulesDTO schedulesDTO) {
        String url = medicineServiceUrl + "/updateInventoryAfterScheduleAction";
        return restTemplate.postForObject(url, schedulesDTO, String.class);
    }

}
