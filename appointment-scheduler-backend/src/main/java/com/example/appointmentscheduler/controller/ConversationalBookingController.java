package com.example.appointmentscheduler.controller;

import com.example.appointmentscheduler.dto.ChatResponse;
import com.example.appointmentscheduler.dto.request.ClarificationRequest;
import com.example.appointmentscheduler.dto.response.ClarificationResponse;
import com.example.appointmentscheduler.service.ConversationalBookingService;
import com.example.appointmentscheduler.service.AIAgentService;
import com.example.appointmentscheduler.utils.ChatIntents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "http://localhost:3000")
public class ConversationalBookingController {

    @Autowired
    private ConversationalBookingService conversationalBookingService;
    
    @Autowired
    private AIAgentService aiAgentService;

    /**
     * Handle conversational booking flow
     */
    @PostMapping("/conversation")
    public ResponseEntity<ChatResponse> processBookingConversation(@RequestBody BookingChatRequest request) {
        try {
            String message = request.getMessage();
            String userId = request.getUserId();
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Message cannot be empty"));
            }
            
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("User ID is required"));
            }

            ChatResponse response = conversationalBookingService.processBookingConversation(message.trim(), userId.trim());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Internal server error occurred"));
        }
    }
    
    /**
     * Handle clarification responses from users
     * Used when AI service needs more details about symptoms
     */
    @PostMapping("/clarification")
    public ResponseEntity<ClarificationResponse> processClarification(@RequestBody ClarificationRequest request) {
        try {
            // Validate request
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createClarificationErrorResponse("User ID is required"));
            }
            
            if (request.getClarificationResponse() == null || request.getClarificationResponse().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createClarificationErrorResponse("Clarification response cannot be empty"));
            }
            
            if (request.getOriginalMessage() == null || request.getOriginalMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createClarificationErrorResponse("Original message is required"));
            }
            
            // Process clarification through AI service
            ClarificationResponse response = aiAgentService.processClarification(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createClarificationErrorResponse("Internal server error occurred"));
        }
    }
    
    /**
     * Health check for booking service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("service", "conversational-booking");
        status.put("status", "healthy");
        return ResponseEntity.ok(status);
    }

    /**
     * Create error response
     */
    private ChatResponse createErrorResponse(String message) {
        ChatResponse response = new ChatResponse();
        response.setReply(message);
        response.setIntent(ChatIntents.ERROR);
        response.setSuccess(false);
        return response;
    }
    
    /**
     * Create error response for clarification processing
     */
    private ClarificationResponse createClarificationErrorResponse(String message) {
        ClarificationResponse response = new ClarificationResponse();
        response.setReply(message);
        response.setIntent(ChatIntents.ERROR);
        response.setSuccess(false);
        response.setConfidenceLevel("low");
        response.setRequiresFurtherClarification(false);
        return response;
    }

    /**
     * Request DTO for booking chat
     */
    public static class BookingChatRequest {
        private String message;
        private String userId;

        public BookingChatRequest() {}

        public BookingChatRequest(String message, String userId) {
            this.message = message;
            this.userId = userId;
        }

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
