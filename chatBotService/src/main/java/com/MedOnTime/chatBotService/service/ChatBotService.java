package com.MedOnTime.chatBotService.service;

import org.openapitools.db_data.client.ApiException;

public interface ChatBotService {

    String handleUserPrompt(String prompt, Integer userId) throws ApiException;

}
