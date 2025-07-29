package com.example.appointmentscheduler.dto.request;

/**
 * Request DTO for handling clarification responses from users
 * Used when AI service needs more details about symptoms
 */
public class ClarificationRequest {
    
    private String userId;
    private String originalMessage;
    private String clarificationResponse;
    private String conversationContext; // Optional: for storing conversation history
    
    public ClarificationRequest() {}
    
    public ClarificationRequest(String userId, String originalMessage, String clarificationResponse) {
        this.userId = userId;
        this.originalMessage = originalMessage;
        this.clarificationResponse = clarificationResponse;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getOriginalMessage() {
        return originalMessage;
    }
    
    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }
    
    public String getClarificationResponse() {
        return clarificationResponse;
    }
    
    public void setClarificationResponse(String clarificationResponse) {
        this.clarificationResponse = clarificationResponse;
    }
    
    public String getConversationContext() {
        return conversationContext;
    }
    
    public void setConversationContext(String conversationContext) {
        this.conversationContext = conversationContext;
    }
    
    @Override
    public String toString() {
        return "ClarificationRequest{" +
                "userId='" + userId + '\'' +
                ", originalMessage='" + originalMessage + '\'' +
                ", clarificationResponse='" + clarificationResponse + '\'' +
                ", conversationContext='" + conversationContext + '\'' +
                '}';
    }
}
