package com.example.chat_service;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void saveMessage(MessageEntity message) {
        chatRepository.save(message);
    }

    public List<MessageEntity> getLast50Messages(Long meetupID) {
        return chatRepository.findTop50ByMeetupIDOrderBySentAtDesc(meetupID); //REVERSE THE MESSAGES LATER
    }
}
