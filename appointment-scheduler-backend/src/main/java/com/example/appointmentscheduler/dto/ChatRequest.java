package com.example.appointmentscheduler.dto;

import java.util.List;

public class ChatRequest {
    private List<ChatMessage> messages;
    private String userId;

    public ChatRequest() {}

    public ChatRequest(List<ChatMessage> messages, String userId) {
        this.messages = messages;
        this.userId = userId;
    }

    // Getters and Setters
    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
