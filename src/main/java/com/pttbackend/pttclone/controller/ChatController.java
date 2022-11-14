package com.pttbackend.pttclone.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.pttbackend.pttclone.model.Message;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ChatController {
    // provides methods for sending messages to a user
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private")
    public void response(@Payload Message mes){
        simpMessagingTemplate.convertAndSendToUser(mes.getSender(), "/messages", mes.getText());
    }
}
