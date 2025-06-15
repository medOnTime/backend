package com.MedOnTime.chatBotService.service;

import com.MedOnTime.chatBotService.dto.ChatParam;
import com.MedOnTime.chatBotService.dto.IntentType;

public interface ParameterExtracterService {

    ChatParam extractParams(String userPrompt, IntentType intentType);

}
