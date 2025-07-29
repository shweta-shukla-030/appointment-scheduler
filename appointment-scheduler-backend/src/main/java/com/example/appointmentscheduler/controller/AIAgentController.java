package com.example.appointmentscheduler.controller;

import com.example.appointmentscheduler.dto.ChatResponse;
import com.example.appointmentscheduler.model.Doctor;
import com.example.appointmentscheduler.service.AIAgentService;
import com.example.appointmentscheduler.utils.ChatIntents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AIAgentController {
    
    @Autowired
    private AIAgentService aiAgentService;
    
    /**
     * Main chat endpoint for AI conversations
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String message = request.getMessage();
            String userId = request.getUserId();
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Message cannot be empty"));
            }
            
            ChatResponse response = aiAgentService.processChat(message, userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get doctor recommendations based on symptoms
     */
    @PostMapping("/recommend")
    public ResponseEntity<DoctorRecommendationResponse> recommendDoctors(
            @RequestBody SymptomRequest request) {
        try {
            String symptoms = request.getSymptoms();
            
            if (symptoms == null || symptoms.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new DoctorRecommendationResponse("Symptoms cannot be empty", null));
            }
            
            // Use AI to analyze symptoms and get specialty
            ChatResponse aiResponse = aiAgentService.processChat(
                "I have these symptoms: " + symptoms, request.getUserId());
            
            List<Doctor> doctors = aiResponse.getRecommendedDoctors();
            String specialty = (String) aiResponse.getEntities().get("specialty");
            
            DoctorRecommendationResponse response = new DoctorRecommendationResponse(
                aiResponse.getReply(), doctors);
            response.setSpecialty(specialty);
            response.setSymptoms(symptoms);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new DoctorRecommendationResponse("Error processing request", null));
        }
    }
    
    /**
     * Get doctors by specialty directly
     */
    @GetMapping("/doctors/{specialty}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialty(@PathVariable String specialty) {
        try {
            List<Doctor> doctors = aiAgentService.getDoctorsBySpecialty(specialty);
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Health check for AI service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "healthy");
        status.put("service", "ai-agent-controller");
        return ResponseEntity.ok(status);
    }
    
    // Helper method to create error response
    private ChatResponse createErrorResponse(String message) {
        ChatResponse response = new ChatResponse();
        response.setReply(message);
        response.setIntent(ChatIntents.ERROR);
        response.setSuccess(false);
        response.setEntities(new HashMap<>());
        return response;
    }
    
    // Inner classes for request/response DTOs
    public static class ChatRequest {
        private String message;
        private String userId;
        
        // Constructors
        public ChatRequest() {}
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class SymptomRequest {
        private String symptoms;
        private String userId;
        
        // Constructors
        public SymptomRequest() {}
        
        // Getters and Setters
        public String getSymptoms() { return symptoms; }
        public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class DoctorRecommendationResponse {
        private String message;
        private List<Doctor> doctors;
        private String specialty;
        private String symptoms;
        
        public DoctorRecommendationResponse(String message, List<Doctor> doctors) {
            this.message = message;
            this.doctors = doctors;
        }
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<Doctor> getDoctors() { return doctors; }
        public void setDoctors(List<Doctor> doctors) { this.doctors = doctors; }
        public String getSpecialty() { return specialty; }
        public void setSpecialty(String specialty) { this.specialty = specialty; }
        public String getSymptoms() { return symptoms; }
        public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    }
}
