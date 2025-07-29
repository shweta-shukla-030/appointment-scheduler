package com.example.appointmentscheduler.dto;

import com.example.appointmentscheduler.model.Doctor;
import java.util.List;
import java.util.Map;

public class ChatResponse {
    private String reply;
    private String intent;
    private Map<String, Object> entities;
    private List<Doctor> recommendedDoctors;
    private String appointmentId;
    private boolean success;
    
    // New fields for enhanced AI service integration
    private String confidenceLevel;  // "high", "medium", "low"
    private boolean requiresClarification;
    private List<String> clarificationQuestions;
    private String fallbackLevel;  // "static", "dynamic", "clarification"

    public ChatResponse() {}

    public ChatResponse(String reply, String intent, Map<String, Object> entities) {
        this.reply = reply;
        this.intent = intent;
        this.entities = entities;
        this.success = true;
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

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getters and Setters for new fields
    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public boolean isRequiresClarification() {
        return requiresClarification;
    }

    public void setRequiresClarification(boolean requiresClarification) {
        this.requiresClarification = requiresClarification;
    }

    public List<String> getClarificationQuestions() {
        return clarificationQuestions;
    }

    public void setClarificationQuestions(List<String> clarificationQuestions) {
        this.clarificationQuestions = clarificationQuestions;
    }

    public String getFallbackLevel() {
        return fallbackLevel;
    }

    public void setFallbackLevel(String fallbackLevel) {
        this.fallbackLevel = fallbackLevel;
    }
}
