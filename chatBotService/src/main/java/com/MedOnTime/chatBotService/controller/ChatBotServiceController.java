package com.MedOnTime.chatBotService.controller;

import com.MedOnTime.chatBotService.service.ChatBotService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chatbot")
public class ChatBotServiceController {

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping("chat")
    public String chatWithChatBot(HttpServletRequest httpServletRequest, @RequestBody String prompt){
        String userId = httpServletRequest.getHeader("X-User-Id");
        return chatBotService.handleUserPrompt(prompt, Integer.parseInt(userId));
    }


}
