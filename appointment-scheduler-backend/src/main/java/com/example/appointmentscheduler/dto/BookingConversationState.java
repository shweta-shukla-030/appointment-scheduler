package com.example.appointmentscheduler.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for tracking booking conversation state
 */
public class BookingConversationState {
    private String step; // "symptoms", "doctors", "location", "date", "time", "reason", "confirm"
    private String symptoms;
    private String originalSymptoms; // Store original symptoms from AI analysis
    private boolean symptomsPreAnalyzed; // Flag to indicate if symptoms were already analyzed
    private List<com.example.appointmentscheduler.model.Doctor> recommendedDoctors;
    private List<String> availableLocations;
    private String selectedLocation;
    private List<com.example.appointmentscheduler.model.Doctor> filteredDoctors;
    private com.example.appointmentscheduler.model.Doctor selectedDoctor;
    private LocalDate selectedDate;
    private List<String> availableTimeSlots;
    private String selectedTimeSlot;
    private String reason;
    private String userId;
    
    public BookingConversationState() {}
    
    public BookingConversationState(String userId) {
        this.userId = userId;
        this.step = "symptoms";
        this.symptomsPreAnalyzed = false;
    }
    
    // Constructor for pre-analyzed symptoms (when coming from AI agent)
    public BookingConversationState(String userId, String originalSymptoms, List<com.example.appointmentscheduler.model.Doctor> recommendedDoctors) {
        this.userId = userId;
        this.originalSymptoms = originalSymptoms;
        this.symptoms = originalSymptoms;
        this.recommendedDoctors = recommendedDoctors;
        this.symptomsPreAnalyzed = true;
        this.step = "location"; // Skip symptom step since they're pre-analyzed
    }
    
    // Getters and setters
    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getOriginalSymptoms() { return originalSymptoms; }
    public void setOriginalSymptoms(String originalSymptoms) { this.originalSymptoms = originalSymptoms; }
    
    public boolean isSymptomsPreAnalyzed() { return symptomsPreAnalyzed; }
    public void setSymptomsPreAnalyzed(boolean symptomsPreAnalyzed) { this.symptomsPreAnalyzed = symptomsPreAnalyzed; }
    
    public List<com.example.appointmentscheduler.model.Doctor> getRecommendedDoctors() { return recommendedDoctors; }
    public void setRecommendedDoctors(List<com.example.appointmentscheduler.model.Doctor> recommendedDoctors) { this.recommendedDoctors = recommendedDoctors; }
    
    public List<String> getAvailableLocations() { return availableLocations; }
    public void setAvailableLocations(List<String> availableLocations) { this.availableLocations = availableLocations; }
    
    public String getSelectedLocation() { return selectedLocation; }
    public void setSelectedLocation(String selectedLocation) { this.selectedLocation = selectedLocation; }
    
    public List<com.example.appointmentscheduler.model.Doctor> getFilteredDoctors() { return filteredDoctors; }
    public void setFilteredDoctors(List<com.example.appointmentscheduler.model.Doctor> filteredDoctors) { this.filteredDoctors = filteredDoctors; }
    
    public com.example.appointmentscheduler.model.Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(com.example.appointmentscheduler.model.Doctor selectedDoctor) { this.selectedDoctor = selectedDoctor; }
    
    public LocalDate getSelectedDate() { return selectedDate; }
    public void setSelectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; }
    
    public List<String> getAvailableTimeSlots() { return availableTimeSlots; }
    public void setAvailableTimeSlots(List<String> availableTimeSlots) { this.availableTimeSlots = availableTimeSlots; }
    
    public String getSelectedTimeSlot() { return selectedTimeSlot; }
    public void setSelectedTimeSlot(String selectedTimeSlot) { this.selectedTimeSlot = selectedTimeSlot; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
