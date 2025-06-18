package com.MedOnTime.chatBotService.service;

import com.MedOnTime.chatBotService.dto.ChatParam;
import com.MedOnTime.chatBotService.dto.IntentType;
import com.MedOnTime.chatBotService.repository.ChatBotServiceRepository;
import com.google.gson.Gson;
import io.pinecone.clients.Index;
import io.pinecone.configs.PineconeConfig;
import io.pinecone.configs.PineconeConnection;
import org.openapitools.db_data.client.ApiException;
import org.openapitools.db_data.client.model.Hit;
import org.openapitools.db_data.client.model.SearchRecordsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatBotService {

    @Autowired
    private ChatBotServiceRepository chatBotServiceRepository;

    @Autowired
    private IntentDetecticeService intentDetecticeService;

    @Autowired
    private ParameterExtracterService parameterExtracterService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private Index medontimefaqindex;

    @Override
    public String handleUserPrompt(String prompt, Integer userId) throws ApiException {

        //first check whether is it faq type question
        List<String> fields = new ArrayList<>();
        fields.add("ID");
        fields.add("text");

        SearchRecordsResponse searchRecordsResponse = medontimefaqindex.searchRecordsByText(prompt, "__default__", fields, 5, null,null);

        double scoreThreshold = 0.50;

        // Get the top hit
        List<Hit> hits = searchRecordsResponse.getResult().getHits();

        if (hits != null && !hits.isEmpty()) {
            Hit topHit = hits.get(0);
            if (topHit.getScore() != null && topHit.getScore() >= scoreThreshold) {
                // This is a relevant FAQ match → generate a natural response
                Object data = topHit.getFields(); // this contains your chunk_text, category, etc.
                return generateNaturalResponseForFAQ(prompt, data);
            } else {
                System.out.println("No relevant FAQ found (below threshold).");
            }
        }

        IntentType intent = intentDetecticeService.detectIntent(prompt);

        switch (intent) {
            case GET_SCHEDULE -> {
                ChatParam params = parameterExtracterService.extractParams(prompt, intent);
                List<HashMap<String, String>> data = chatBotServiceRepository.findScheduledRemindersWithFilters(
                        userId,
                        params.getStatus(),
                        params.getDate() != null ? LocalDate.parse(params.getDate()) : null
                );
                return generateNaturalResponse(prompt, data);
            }

            case GET_INVENTORY -> {
                List<HashMap<String, String>> data = chatBotServiceRepository.getMedicineInventoryByUser(userId);
                return generateNaturalResponse(prompt, data);
            }

            case GET__SCHEDULES_AND_INVENTORY -> {
                ChatParam params = parameterExtracterService.extractParams(prompt, intent);
                List<HashMap<String, String>> schedulesData = chatBotServiceRepository.findScheduledRemindersWithFilters(
                        userId,
                        params.getStatus(),
                        params.getDate() != null ? LocalDate.parse(params.getDate()) : null
                );
                List<HashMap<String, String>> inventoryData = chatBotServiceRepository.getMedicineInventoryByUser(userId);
                return generateNaturalResponse(prompt, schedulesData, inventoryData);
            }

            case GENERAL_QUESTION -> {
                return callGemini(prompt);
            }

            default -> throw new IllegalStateException("Unknown intent");
        }
    }

    private String callGemini(String promptText) {
        String instruction = """
                    You are a healthcare assistant specialized in medicine and diseases.
                    You should only answer questions related to:
                    - medicines (uses, dosage, side effects, schedules)
                    - diseases (symptoms, treatments, interactions)
                    If a question is outside this domain, reply:
                    "I'm only trained to assist with medicine and disease-related questions."

                    Now answer the following:
                    %s
                """.formatted(promptText);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", instruction)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        try {
            Map candidates = ((List<Map>) response.getBody().get("candidates")).get(0);
            Map content = (Map) candidates.get("content");
            List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
            return parts.get(0).get("text");
        } catch (Exception e) {
            return "Something went wrong while processing the Gemini response.";
        }
    }

    private String generateNaturalResponse(String originalPrompt, Object data) {
        String today = LocalDate.now().toString();

        String formattedPrompt = """
                You are a healthcare assistant. Today is %s.

                Analyze the provided schedule data carefully. 
                - Distinguish between **past** and **future** schedules.
                - Mention if there are any **upcoming medicine schedules**.
                - Highlight any **past schedules** that are still in **PENDING** status (i.e., possibly missed).
                - Ignore completed schedules (status = "TAKEN").
                - Clearly communicate the user's current status and what actions might be needed.

                User asked: "%s"

                Schedule Data:
                %s

                Generate a helpful summary for the user:
                """.formatted(today, originalPrompt, new Gson().toJson(data));
        return callGemini(formattedPrompt);
    }

    private String generateNaturalResponse(String originalPrompt, Object schedulesData, Object inventoryData) {
        String today = LocalDate.now().toString();

        String formattedPrompt = """
                You are a healthcare assistant. Today is %s.

                A user asked: "%s"

                You are given two types of information:
                1. **Medicine Schedules**
                2. **Medicine Inventory**

                Analyze and summarize the schedules:
                - Mention medicines that are scheduled for today or upcoming.
                - Highlight medicines from past dates that are still in PENDING or MISSED status.
                - Ignore completed ones (status = "TAKEN" or "CANCEL").
                - Help the user understand what actions might be needed based on this schedule.

                Then, analyze and summarize the inventory:
                - Mention the list of medicines the user currently has.
                - Don't repeat medicines already discussed in schedules unless relevant.

                Ensure your response is polite, clear, and easy to follow.

                Schedule Data:
                %s

                Inventory Data:
                %s

                Provide a clean, user-friendly summary:
                """.formatted(today, originalPrompt, new Gson().toJson(schedulesData), new Gson().toJson(inventoryData));

        return callGemini(formattedPrompt);
    }

    private String generateNaturalResponseForFAQ(String originalPrompt, Object data) {
        String today = LocalDate.now().toString();

        String formattedPrompt = """
        You are a helpful healthcare assistant AI. Today is %s.

        The user asked the following question: "%s"

        Here is the most relevant information retrieved from the FAQ or medical content database:
        %s

        Based on the information, give a clear, concise, and helpful response.
        """.formatted(today, originalPrompt, new Gson().toJson(data));

        return callGemini(formattedPrompt); // This can use Gemini, GPT-4, Claude, etc.
    }


}
