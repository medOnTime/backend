package com.MedOnTime.chatBotService.service;

import com.MedOnTime.chatBotService.dto.ChatParam;
import com.MedOnTime.chatBotService.dto.IntentType;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ParameterExtractServiceImpl implements ParameterExtracterService {

    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();

    @Value("${gemini.api.key}")
    private String apiKey;

    public ParameterExtractServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ChatParam extractParams(String userPrompt, IntentType intentType) {
        String today = LocalDate.now().toString();

        String context = """
                Today is: %s

                You are a strict JSON extractor for a chatbot system. Based on the user prompt and intent, extract ONLY the required fields in strict JSON format. DO NOT return explanations or multiline outputs.

                Extraction Rules:
                - For intent GET_SCHEDULE:
                    • Always return a JSON object with at least the "status" field.
                    • If the user mentions a date (e.g., "today", "tomorrow", "on 15th"), also include the "date" field.
                    • Valid values for "status" are: "PENDING", "TAKEN", "MISSED", "CANCEL".
                    • The "date" must be in yyyy-MM-dd format. If the user says "today", use "%s".

                - For intent CALCULATE_DAYS_LEFT:
                    • Return field: "medicineName" (e.g., "Paracetamol").

                Examples:
                {"status": "PENDING", "date": "2025-06-15"}
                {"status": "MISSED"}
                {"medicineName": "Panadol"}

                Respond with JSON only, no comments, no extra formatting.

                User prompt: "%s"
                """.formatted(today, today, userPrompt);


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

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
        String jsonResponse = parts.get(0).get("text").trim();

        return gson.fromJson(jsonResponse, ChatParam.class);
    }
}
