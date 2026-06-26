package com.example.chat_service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sender;
    private String content;
    private Long meetupID;
    private LocalDateTime sentAt;

    public MessageEntity() {
    }

    public MessageEntity(String sender, String content, Long meetupID, LocalDateTime sentAt) {
        this.sender = sender;
        this.content = content;
        this.meetupID = meetupID;
        this.sentAt = sentAt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getMeetupID() {
        return meetupID;
    }

    public void setMeetupID(Long meetupID) {
        this.meetupID = meetupID;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
