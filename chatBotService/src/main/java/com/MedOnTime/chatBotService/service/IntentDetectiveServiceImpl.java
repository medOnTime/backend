package com.MedOnTime.chatBotService.service;

import com.MedOnTime.chatBotService.dto.IntentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class IntentDetectiveServiceImpl implements IntentDetecticeService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    public IntentDetectiveServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public IntentType detectIntent(String prompt) {
        String context = """
            Classify the user intent into one of the following:
            - GET_SCHEDULE: if they ask about today's schedules or upcoming doses
            - GET_INVENTORY: if they ask about what medicines they currently have
            - GET__SCHEDULES_AND_INVENTORY: for the answer, if need both schedules and inventory data
            - GENERAL_QUESTION: if it's general health or medicine knowledge
            Return only the intent name.
            Prompt: "%s"
        """.formatted(prompt);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", context)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        // Extract response text
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
        String result = parts.get(0).get("text").trim().toUpperCase();

        return IntentType.valueOf(result);
    }
}
