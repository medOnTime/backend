package com.MedOnTime.chatBotService.service;

import com.MedOnTime.chatBotService.dto.IntentType;

public interface IntentDetecticeService {

    IntentType detectIntent(String prompt);

}
