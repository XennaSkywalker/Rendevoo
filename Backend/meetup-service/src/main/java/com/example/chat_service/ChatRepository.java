package com.example.chat_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findTop50ByMeetupIDOrderBySentAtDesc(Long meetupID);
}
