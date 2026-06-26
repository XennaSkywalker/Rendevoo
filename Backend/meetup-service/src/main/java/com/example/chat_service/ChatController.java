package com.example.chat_service;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/chat/{meetupID}")
    public void sendMessage(@Payload MessageEntity message, @DestinationVariable("meetupID") String meetupID) {
        chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/meetup/" + meetupID, message);
    }

}
