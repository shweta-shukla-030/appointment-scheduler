package com.example.appointmentscheduler.service;

import com.example.appointmentscheduler.dto.ChatMessage;
import com.example.appointmentscheduler.dto.ChatRequest;
import com.example.appointmentscheduler.dto.ChatResponse;
import com.example.appointmentscheduler.dto.request.ClarificationRequest;
import com.example.appointmentscheduler.dto.response.ClarificationResponse;
import com.example.appointmentscheduler.model.Doctor;
import com.example.appointmentscheduler.repository.DoctorRepository;
import com.example.appointmentscheduler.utils.ChatIntents;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class AIAgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIAgentService.class);
    
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private ConversationalBookingService conversationalBookingService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Store symptom analysis context temporarily for booking transition
    private final Map<String, ChatResponse> userSymptomContext = new HashMap<>();

    /**
     * Process chat message and return AI response with additional data
     */
    public ChatResponse processChat(String userMessage, String userId) {
        try {
            // Check if user is in an active booking conversation first
            if (conversationalBookingService.isInBookingConversation(userId)) {
                // Route directly to booking conversation service
                return conversationalBookingService.processBookingConversation(userMessage, userId);
            }
            
            // Create chat request for AI service
            ChatMessage chatMessage = new ChatMessage("user", userMessage);
            ChatRequest aiRequest = new ChatRequest(Arrays.asList(chatMessage), userId);
            
            // Call Python AI service
            ChatResponse aiResponse = callAIService(aiRequest);
            
            if (aiResponse == null) {
                return createErrorResponse("AI service is unavailable");
            }
            
            // Handle clarification scenarios first
            if (aiResponse.isRequiresClarification()) {
                logger.info("AI service requested clarification for user: {}", userId);
                // Store original message for potential retry
                // Note: In a stateless implementation, we don't persist this
                return aiResponse; // Return clarification response to frontend
            }
            
            // Process based on intent
            switch (aiResponse.getIntent().toLowerCase()) {
                case ChatIntents.SYMPTOM_CHECK:
                case ChatIntents.DOCTOR_RECOMMENDATION:
                    // Store symptom analysis context for potential booking
                    ChatResponse response = processSymptomCheck(aiResponse);
                    if (response.getRecommendedDoctors() != null && !response.getRecommendedDoctors().isEmpty()) {
                        // Store the symptom analysis for potential booking
                        userSymptomContext.put(userId, response);
                        response.setReply(response.getReply() + "\n\nWould you like to book an appointment with one of these doctors? Just say 'book appointment' and I'll help you.");
                    }
                    return response;
                case ChatIntents.GENERAL_CHAT:
                    // Check if general chat contains symptom information that we should process
                    if (containsSymptoms(userMessage)) {
                        // Directly process symptoms without calling AI service again
                        ChatResponse symptomResponse = processSymptomsDirect(userMessage);
                        if (symptomResponse.getRecommendedDoctors() != null && !symptomResponse.getRecommendedDoctors().isEmpty()) {
                            // Store the symptom analysis for potential booking
                            userSymptomContext.put(userId, symptomResponse);
                            symptomResponse.setReply(symptomResponse.getReply() + "\n\nWould you like to book an appointment with one of these doctors? Just say 'book appointment' and I'll help you.");
                        }
                        return symptomResponse;
                    }
                    return aiResponse; // Return as regular general chat
                case ChatIntents.BOOK_APPOINTMENT:
                    // Check if we have previous symptom analysis for this user
                    ChatResponse storedSymptomContext = userSymptomContext.get(userId);
                    if (storedSymptomContext != null && storedSymptomContext.getRecommendedDoctors() != null && !storedSymptomContext.getRecommendedDoctors().isEmpty()) {
                        // Start booking with pre-analyzed symptoms
                        String originalSymptoms = extractOriginalSymptoms(storedSymptomContext);
                        ChatResponse bookingResponse = conversationalBookingService.startBookingWithSymptoms(
                            originalSymptoms, 
                            storedSymptomContext.getRecommendedDoctors(), 
                            userId
                        );
                        // Clear the context after using it
                        userSymptomContext.remove(userId);
                        return bookingResponse;
                    } else {
                        // No prior symptom analysis, set flag for frontend to handle
                        aiResponse.setIntent(ChatIntents.BOOKING_START);
                        return aiResponse;
                    }
                case ChatIntents.CONFIRMATION:
                    return processConfirmation(aiResponse, userId);
                default:
                    return aiResponse; // Return general chat response as-is
            }
            
        } catch (Exception e) {
            logger.error("Error processing chat message", e);
            return createErrorResponse("Sorry, I encountered an error. Please try again.");
        }
    }
    
    /**
     * Process symptoms directly without calling AI service (prevents loops)
     */
    private ChatResponse processSymptomsDirect(String userMessage) {
        try {
            // Rule-based symptom analysis
            String specialty = determineSpecialty(userMessage);
            List<Doctor> doctors = new ArrayList<>();
            
            if (specialty != null) {
                doctors = doctorRepository.findBySpecialityContainingIgnoreCase(specialty);
            }
            
            if (doctors.isEmpty()) {
                // Fallback to general medicine
                doctors = doctorRepository.findBySpecialityContainingIgnoreCase("General Medicine");
            }
            
            ChatResponse response = new ChatResponse();
            if (!doctors.isEmpty()) {
                response.setReply(String.format("Based on your symptoms, I recommend seeing a %s specialist. I found %d doctors available.", 
                    specialty != null ? specialty : "General Medicine", doctors.size()));
                response.setIntent(ChatIntents.SYMPTOM_CHECK);
                response.setRecommendedDoctors(doctors);
                response.setSuccess(true);
            } else {
                response.setReply("I understand you have symptoms, but I couldn't find specific doctors at the moment. Please try again later.");
                response.setIntent(ChatIntents.GENERAL_CHAT);
                response.setSuccess(true);
            }
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error in direct symptom processing", e);
            return createErrorResponse("Sorry, I had trouble analyzing your symptoms. Please try again.");
        }
    }
    
    /**
     * Determine medical specialty based on symptoms (rule-based)
     */
    private String determineSpecialty(String message) {
        if (message == null) return null;
        
        String lowerMessage = message.toLowerCase();
        
        // Heart/Chest related
        if (lowerMessage.contains("chest pain") || lowerMessage.contains("heart")) {
            return "Cardiology";
        }
        
        // Skin related
        if (lowerMessage.contains("skin") || lowerMessage.contains("rash")) {
            return "Dermatology";
        }
        
        // Respiratory
        if (lowerMessage.contains("cough") || lowerMessage.contains("breathing") || lowerMessage.contains("lung")) {
            return "Pulmonology";
        }
        
        // Digestive
        if (lowerMessage.contains("stomach") || lowerMessage.contains("nausea") || lowerMessage.contains("digestive")) {
            return "Gastroenterology";
        }
        
        // Bone/Joint
        if (lowerMessage.contains("joint") || lowerMessage.contains("back pain") || lowerMessage.contains("bone")) {
            return "Orthopedics";
        }
        
        // Eye related
        if (lowerMessage.contains("eye") || lowerMessage.contains("vision")) {
            return "Ophthalmology";
        }
        
        // ENT
        if (lowerMessage.contains("ear") || lowerMessage.contains("throat") || lowerMessage.contains("nose")) {
            return "ENT";
        }
        
        // Neurological
        if (lowerMessage.contains("headache") || lowerMessage.contains("migraine") || lowerMessage.contains("dizzy")) {
            return "Neurology";
        }
        
        // Default to General Medicine
        return "General Medicine";
    }

    /**
     * Check if user message contains symptom-related keywords
     */
    private boolean containsSymptoms(String message) {
        if (message == null) return false;
        
        String lowerMessage = message.toLowerCase();
        
        // Common symptom keywords
        String[] symptomKeywords = {
            "headache", "fever", "pain", "ache", "hurt", "sick", "ill", 
            "cough", "cold", "flu", "nausea", "dizzy", "tired", "fatigue",
            "stomach", "chest", "back", "throat", "ear", "eye", "skin",
            "rash", "bleeding", "swollen", "infection", "symptom", "problem",
            "i have", "i feel", "experiencing", "suffering"
        };
        
        for (String keyword : symptomKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Extract original symptoms from stored context
     */
    private String extractOriginalSymptoms(ChatResponse storedContext) {
        // Try to extract from entities first
        if (storedContext.getEntities() != null && storedContext.getEntities().containsKey("symptoms")) {
            Object symptomsObj = storedContext.getEntities().get("symptoms");
            if (symptomsObj instanceof List) {
                List<?> symptomsList = (List<?>) symptomsObj;
                return symptomsList.stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse("various symptoms");
            } else if (symptomsObj instanceof String) {
                return (String) symptomsObj;
            }
        }
        
        // Fallback: extract from reply text (look for patterns)
        String reply = storedContext.getReply();
        if (reply != null && reply.contains("symptoms")) {
            // Simple extraction - could be improved with regex
            return "symptoms mentioned in previous conversation";
        }
        
        return "your symptoms";
    }

    /**
     * Check if user message indicates booking intent
     */
    private boolean isBookingIntent(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("book") && lowerMessage.contains("appointment") ||
               lowerMessage.contains("schedule") && lowerMessage.contains("appointment") ||
               lowerMessage.contains("make appointment") ||
               lowerMessage.contains("i have") && (lowerMessage.contains("pain") || lowerMessage.contains("fever") || 
                   lowerMessage.contains("headache") || lowerMessage.contains("cough") || lowerMessage.contains("symptom"));
    }
    
    /**
     * Call the Python AI service
     */
    private ChatResponse callAIService(ChatRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
            
            String url = aiServiceUrl + "/chat";
            logger.info("Calling AI service at: {}", url);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return parseAIResponse(response.getBody());
            }
            
        } catch (Exception e) {
            logger.error("Error calling AI service", e);
        }
        
        return null;
    }
    
    /**
     * Parse AI service JSON response with enhanced fields
     */
    private ChatResponse parseAIResponse(String jsonResponse) {
        try {
            JsonNode node = objectMapper.readTree(jsonResponse);
            
            ChatResponse response = new ChatResponse();
            response.setReply(node.get("reply").asText());
            response.setIntent(node.get("intent").asText());
            
            // Parse entities
            Map<String, Object> entities = new HashMap<>();
            JsonNode entitiesNode = node.get("entities");
            if (entitiesNode != null) {
                entitiesNode.fields().forEachRemaining(entry -> {
                    JsonNode valueNode = entry.getValue();
                    if (valueNode.isArray()) {
                        // Handle arrays (like clarification_questions)
                        List<String> arrayValues = new ArrayList<>();
                        valueNode.forEach(item -> arrayValues.add(item.asText()));
                        entities.put(entry.getKey(), arrayValues);
                    } else {
                        entities.put(entry.getKey(), valueNode.asText());
                    }
                });
            }
            response.setEntities(entities);
            
            // Parse new enhanced fields from entities
            if (entitiesNode != null) {
                // Map confidence level
                if (entitiesNode.has("confidence")) {
                    double confidence = entitiesNode.get("confidence").asDouble();
                    response.setConfidenceLevel(mapConfidenceLevel(confidence));
                }
                
                // Map requires clarification
                if (entitiesNode.has("requires_clarification")) {
                    response.setRequiresClarification(entitiesNode.get("requires_clarification").asBoolean());
                }
                
                // Map clarification questions
                if (entitiesNode.has("clarification_questions")) {
                    JsonNode questionsNode = entitiesNode.get("clarification_questions");
                    if (questionsNode.isArray()) {
                        List<String> questions = new ArrayList<>();
                        questionsNode.forEach(q -> questions.add(q.asText()));
                        response.setClarificationQuestions(questions);
                    }
                }
                
                // Map fallback level
                if (entitiesNode.has("fallback_level")) {
                    response.setFallbackLevel(entitiesNode.get("fallback_level").asText());
                }
            }
            
            response.setSuccess(true);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error parsing AI response", e);
            return createErrorResponse("Failed to process AI response");
        }
    }
    
    /**
     * Map numeric confidence to string levels
     */
    private String mapConfidenceLevel(double confidence) {
        if (confidence >= 0.8) {
            return "high";
        } else if (confidence >= 0.5) {
            return "medium";
        } else {
            return "low";
        }
    }
    
    /**
     * Process clarification response from user
     * Combines original message with clarification and retries AI analysis
     */
    public ClarificationResponse processClarification(ClarificationRequest request) {
        try {
            logger.info("Processing clarification for user: {}", request.getUserId());
            
            // Combine original message with clarification response
            String combinedMessage = request.getOriginalMessage() + ". " + 
                "Additional details: " + request.getClarificationResponse();
            
            logger.info("Combined message for retry: {}", combinedMessage);
            
            // Create new chat request with combined message
            ChatMessage combinedChatMessage = new ChatMessage("user", combinedMessage);
            ChatRequest retryRequest = new ChatRequest(Arrays.asList(combinedChatMessage), request.getUserId());
            
            // Call AI service again with enhanced context
            ChatResponse aiResponse = callAIService(retryRequest);
            
            if (aiResponse == null) {
                return createClarificationErrorResponse("AI service is unavailable for clarification processing");
            }
            
            // Convert ChatResponse to ClarificationResponse
            ClarificationResponse clarificationResponse = new ClarificationResponse();
            clarificationResponse.setReply(aiResponse.getReply());
            clarificationResponse.setIntent(aiResponse.getIntent());
            clarificationResponse.setEntities(aiResponse.getEntities());
            clarificationResponse.setSuccess(aiResponse.isSuccess());
            clarificationResponse.setConfidenceLevel(aiResponse.getConfidenceLevel());
            clarificationResponse.setRequiresFurtherClarification(aiResponse.isRequiresClarification());
            clarificationResponse.setAdditionalQuestions(aiResponse.getClarificationQuestions());
            clarificationResponse.setFallbackLevel(aiResponse.getFallbackLevel());
            clarificationResponse.setProcessedMessage(combinedMessage);
            
            // If we have a specialty, find recommended doctors
            if (aiResponse.getIntent().equals(ChatIntents.SYMPTOM_CHECK) && aiResponse.getEntities() != null) {
                String specialty = (String) aiResponse.getEntities().get("specialty");
                if (specialty != null) {
                    List<Doctor> doctors = doctorRepository.findBySpecialityContainingIgnoreCase(specialty);
                    clarificationResponse.setRecommendedDoctors(doctors);
                    
                    if (!doctors.isEmpty()) {
                        String enhancedReply = clarificationResponse.getReply() + 
                            String.format("\n\nBased on your clarification, I found %d %s specialists available. " +
                            "Would you like to book an appointment?", doctors.size(), specialty);
                        clarificationResponse.setReply(enhancedReply);
                    }
                }
            }
            
            return clarificationResponse;
            
        } catch (Exception e) {
            logger.error("Error processing clarification for user: {}", request.getUserId(), e);
            return createClarificationErrorResponse("Error processing clarification. Please try again.");
        }
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
     * Process symptom check and find recommended doctors
     */
    private ChatResponse processSymptomCheck(ChatResponse aiResponse) {
        String specialty = (String) aiResponse.getEntities().get("specialty");
        
        if (specialty != null) {
            List<Doctor> doctors = doctorRepository.findBySpecialityContainingIgnoreCase(specialty);
            aiResponse.setRecommendedDoctors(doctors);
            
            if (!doctors.isEmpty()) {
                Doctor topDoctor = doctors.get(0); // Get highest rated or first doctor
                String enhancedReply = aiResponse.getReply() + 
                    String.format("\n\nI found Dr. %s who specializes in %s. " +
                    "Rating: %.1f⭐, Experience: %d years. Would you like to book an appointment?",
                    topDoctor.getName(), topDoctor.getSpeciality(), 
                    topDoctor.getRating(), topDoctor.getYearsOfExperience());
                aiResponse.setReply(enhancedReply);
            } else {
                aiResponse.setReply(aiResponse.getReply() + 
                    "\n\nI'm sorry, we don't currently have any doctors available in " + specialty + 
                    ". Please try contacting us directly for assistance.");
            }
        }
        
        return aiResponse;
    }
    
    /**
     * Process booking request
     */
    private ChatResponse processBookingRequest(ChatResponse aiResponse, String userId) {
        // This is a simplified version - in a real app you'd need more validation
        Map<String, Object> entities = aiResponse.getEntities();
        
        try {
            String specialty = (String) entities.get("specialty");
            String dateStr = (String) entities.get("date");
            String timeStr = (String) entities.get("time");
            
            if (specialty != null) {
                List<Doctor> doctors = doctorRepository.findBySpecialityContainingIgnoreCase(specialty);
                if (!doctors.isEmpty()) {
                    Doctor doctor = doctors.get(0); // Select first available doctor
                    
                    // For now, just return confirmation - actual booking would need more implementation
                    String reply = String.format("I found Dr. %s for your %s appointment. " +
                        "To complete the booking, please provide your preferred date and time, " +
                        "and I'll check availability.", doctor.getName(), specialty);
                    
                    aiResponse.setReply(reply);
                    aiResponse.setRecommendedDoctors(Arrays.asList(doctor));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing booking request", e);
            aiResponse.setReply("I encountered an issue while processing your booking request. " +
                "Please try again or contact support.");
        }
        
        return aiResponse;
    }
    
    /**
     * Process confirmation
     */
    private ChatResponse processConfirmation(ChatResponse aiResponse, String userId) {
        // Handle appointment confirmations
        return aiResponse;
    }
    
    /**
     * Create error response
     */
    private ChatResponse createErrorResponse(String message) {
        ChatResponse response = new ChatResponse();
        response.setReply(message);
        response.setIntent(ChatIntents.ERROR);
        response.setSuccess(false);
        response.setEntities(new HashMap<>());
        return response;
    }
    
    /**
     * Get doctors by specialty (public method for other services)
     */
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialityContainingIgnoreCase(specialty);
    }
}
