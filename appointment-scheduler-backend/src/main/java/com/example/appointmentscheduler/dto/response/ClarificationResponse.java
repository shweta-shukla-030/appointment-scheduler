package com.example.appointmentscheduler.dto.response;

import com.example.appointmentscheduler.model.Doctor;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for clarification processing results
 * Extended response that includes clarification-specific fields
 */
public class ClarificationResponse {
    
    private String reply;
    private String intent;
    private Map<String, Object> entities;
    private List<Doctor> recommendedDoctors;
    private boolean success;
    
    // Clarification-specific fields
    private String confidenceLevel;
    private boolean requiresFurtherClarification;
    private List<String> additionalQuestions;
    private String fallbackLevel;
    private String processedMessage; // Combined original + clarification
    
    public ClarificationResponse() {}
    
    public ClarificationResponse(String reply, String intent, boolean success) {
        this.reply = reply;
        this.intent = intent;
        this.success = success;
    }
    
    // Getters and Setters
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
    
    public String getIntent() {
        return intent;
    }
    
    public void setIntent(String intent) {
        this.intent = intent;
    }
    
    public Map<String, Object> getEntities() {
        return entities;
    }
    
    public void setEntities(Map<String, Object> entities) {
        this.entities = entities;
    }
    
    public List<Doctor> getRecommendedDoctors() {
        return recommendedDoctors;
    }
    
    public void setRecommendedDoctors(List<Doctor> recommendedDoctors) {
        this.recommendedDoctors = recommendedDoctors;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getConfidenceLevel() {
        return confidenceLevel;
    }
    
    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }
    
    public boolean isRequiresFurtherClarification() {
        return requiresFurtherClarification;
    }
    
    public void setRequiresFurtherClarification(boolean requiresFurtherClarification) {
        this.requiresFurtherClarification = requiresFurtherClarification;
    }
    
    public List<String> getAdditionalQuestions() {
        return additionalQuestions;
    }
    
    public void setAdditionalQuestions(List<String> additionalQuestions) {
        this.additionalQuestions = additionalQuestions;
    }
    
    public String getFallbackLevel() {
        return fallbackLevel;
    }
    
    public void setFallbackLevel(String fallbackLevel) {
        this.fallbackLevel = fallbackLevel;
    }
    
    public String getProcessedMessage() {
        return processedMessage;
    }
    
    public void setProcessedMessage(String processedMessage) {
        this.processedMessage = processedMessage;
    }
}
