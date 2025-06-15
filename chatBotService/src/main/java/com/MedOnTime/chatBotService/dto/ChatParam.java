package com.MedOnTime.chatBotService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatParam {

    private String status;
    private String date;
    private String medicineName;


}
