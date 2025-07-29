package com.example.appointmentscheduler.service;

import com.example.appointmentscheduler.dto.BookingConversationState;
import com.example.appointmentscheduler.dto.ChatMessage;
import com.example.appointmentscheduler.dto.ChatRequest;
import com.example.appointmentscheduler.dto.ChatResponse;
import com.example.appointmentscheduler.model.Appointment;
import com.example.appointmentscheduler.model.Doctor;
import com.example.appointmentscheduler.model.User;
import com.example.appointmentscheduler.repository.AppointmentRepository;
import com.example.appointmentscheduler.repository.DoctorRepository;
import com.example.appointmentscheduler.repository.UserRepository;
import com.example.appointmentscheduler.utils.BookingSteps;
import com.example.appointmentscheduler.utils.ChatIntents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationalBookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationalBookingService.class);
    
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Store conversation states in memory (in production, use Redis or database)
    private final Map<String, BookingConversationState> conversationStates = new ConcurrentHashMap<>();
    
    // Available time slots
    private final List<String> timeSlots = Arrays.asList(
        "09:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM",
        "02:00 PM - 03:00 PM", "03:00 PM - 04:00 PM", "04:00 PM - 05:00 PM"
    );
    
    public ChatResponse processBookingConversation(String message, String userId) {
        logger.info("[ConversationalBooking] Processing message: '{}' for user: '{}'", message, userId);
        
        try {
            BookingConversationState state = conversationStates.getOrDefault(userId, new BookingConversationState(userId));
            logger.info("[ConversationalBooking] Current step: {}", state.getStep());
            
            switch (state.getStep()) {
                case BookingSteps.SYMPTOMS:
                    logger.info("[ConversationalBooking] Handling symptoms step");
                    return handleSymptomsStep(message, state);
                case BookingSteps.LOCATION:
                    logger.info("[ConversationalBooking] Handling location step");
                    return handleLocationStep(message, state);
                case BookingSteps.DATE:
                    logger.info("[ConversationalBooking] Handling date step");
                    return handleDateStep(message, state);
                case BookingSteps.TIME:
                    logger.info("[ConversationalBooking] Handling time step");
                    return handleTimeStep(message, state);
                case BookingSteps.REASON:
                    logger.info("[ConversationalBooking] Handling reason step");
                    return handleReasonStep(message, state);
                default:
                    logger.info("[ConversationalBooking] Default case - handling symptoms step");
                    return handleSymptomsStep(message, state);
            }
        } catch (Exception e) {
            logger.error("Error in booking conversation", e);
            conversationStates.remove(userId);
            return createErrorResponse("Something went wrong. Let's start over. Please describe your symptoms.");
        }
    }
    
    /**
     * Start booking conversation with pre-analyzed symptoms and recommended doctors
     * This method is called when user has already provided symptoms to AI agent
     */
    public ChatResponse startBookingWithSymptoms(String originalSymptoms, List<Doctor> recommendedDoctors, String userId) {
        try {
            // Create new conversation state with pre-analyzed data
            BookingConversationState state = new BookingConversationState(userId, originalSymptoms, recommendedDoctors);
            
            // Extract unique locations from recommended doctors
            Set<String> locationSet = recommendedDoctors.stream()
                .map(Doctor::getLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            List<String> locations = new ArrayList<>(locationSet);
            state.setAvailableLocations(locations);
            
            // Store the state
            conversationStates.put(userId, state);
            
            // Generate location selection message
            String locationOptions = locations.stream()
                .map(loc -> "• " + loc)
                .collect(Collectors.joining("\n"));
            
            String reply = String.format(
                "Great! Based on your symptoms (\"%s\"), I found %d suitable doctors.\n\n" +
                "Please select your preferred location:\n%s\n\n" +
                "Just type the location name.",
                originalSymptoms, 
                recommendedDoctors.size(),
                locationOptions
            );
            
            ChatResponse response = new ChatResponse();
            response.setReply(reply);
            response.setIntent(ChatIntents.BOOKING_LOCATION_SELECTION);
            response.setSuccess(true);
            
            return response;
                
        } catch (Exception e) {
            logger.error("Error starting booking with symptoms", e);
            return createErrorResponse("Something went wrong starting your booking. Please try again.");
        }
    }
    
    private ChatResponse handleSymptomsStep(String message, BookingConversationState state) {
        try {
            // Call AI service directly to analyze symptoms
            List<Doctor> recommendedDoctors = analyzeSymptoms(message);
            
            if (recommendedDoctors != null && !recommendedDoctors.isEmpty()) {
                state.setSymptoms(message);
                state.setRecommendedDoctors(recommendedDoctors);
                
                // Extract unique locations
                Set<String> locationSet = recommendedDoctors.stream()
                    .map(Doctor::getLocation)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                
                List<String> locations = new ArrayList<>(locationSet);
                state.setAvailableLocations(locations);
                state.setStep(BookingSteps.LOCATION);
                
                conversationStates.put(state.getUserId(), state);
                
                // Create response with location options
                String reply = String.format("Based on your symptoms, I've found %d doctors who can help you.\\n\\n" +
                    "Please select your preferred location:\\n", recommendedDoctors.size());
                
                for (int i = 0; i < locations.size(); i++) {
                    reply += String.format("%d. %s\\n", i + 1, locations.get(i));
                }
                
                reply += "\\nJust type the number or location name.";
                
                ChatResponse response = new ChatResponse();
                response.setReply(reply);
                response.setIntent(ChatIntents.BOOKING_LOCATION_SELECTION);
                response.setRecommendedDoctors(recommendedDoctors);
                response.setSuccess(true);
                
                Map<String, Object> entities = new HashMap<>();
                entities.put("locations", locations);
                entities.put("step", "location");
                response.setEntities(entities);
                
                return response;
            } else {
                return createErrorResponse("I couldn't find suitable doctors for your symptoms. Please try describing them differently.");
            }
        } catch (Exception e) {
            logger.error("Error analyzing symptoms", e);
            return createErrorResponse("Sorry, I'm having trouble analyzing your symptoms right now. Please try again.");
        }
    }
    
    private ChatResponse handleLocationStep(String message, BookingConversationState state) {
        String selectedLocation = null;
        
        // Try to parse as number
        try {
            int index = Integer.parseInt(message.trim()) - 1;
            if (index >= 0 && index < state.getAvailableLocations().size()) {
                selectedLocation = state.getAvailableLocations().get(index);
            }
        } catch (NumberFormatException e) {
            // Try to match by location name
            for (String location : state.getAvailableLocations()) {
                if (location.toLowerCase().contains(message.toLowerCase().trim())) {
                    selectedLocation = location;
                    break;
                }
            }
        }
        
        if (selectedLocation == null) {
            String reply = "Please select a valid location:\\n";
            for (int i = 0; i < state.getAvailableLocations().size(); i++) {
                reply += String.format("%d. %s\\n", i + 1, state.getAvailableLocations().get(i));
            }
            return createErrorResponse(reply);
        }
        
        // Make final for lambda expression
        final String finalSelectedLocation = selectedLocation;
        
        // Filter doctors by location
        List<Doctor> filteredDoctors = state.getRecommendedDoctors().stream()
            .filter(doctor -> finalSelectedLocation.equals(doctor.getLocation()))
            .collect(Collectors.toList());
        
        state.setSelectedLocation(selectedLocation);
        state.setFilteredDoctors(filteredDoctors);
        state.setStep(BookingSteps.DATE);
        
        conversationStates.put(state.getUserId(), state);
        
        String reply = String.format("Great! I found %d doctors in %s.\\n\\n" +
            "Now, please select your preferred appointment date (YYYY-MM-DD format):\\n" +
            "For example: %s or %s",
            filteredDoctors.size(), selectedLocation,
            LocalDate.now().plusDays(1).toString(),
            LocalDate.now().plusDays(2).toString());
        
        ChatResponse response = new ChatResponse();
        response.setReply(reply);
        response.setIntent(ChatIntents.BOOKING_DATE_SELECTION);
        response.setSuccess(true);
        
        Map<String, Object> entities = new HashMap<>();
        entities.put("selectedLocation", selectedLocation);
        entities.put("filteredDoctors", filteredDoctors);
        entities.put("step", "date");
        response.setEntities(entities);
        
        return response;
    }
    
    private ChatResponse handleDateStep(String message, BookingConversationState state) {
        LocalDate selectedDate = null;
        
        try {
            selectedDate = LocalDate.parse(message.trim());
            
            // Validate date is in the future
            if (selectedDate.isBefore(LocalDate.now().plusDays(1))) {
                return createErrorResponse("Please select a date from tomorrow onwards. Format: YYYY-MM-DD (e.g., " + 
                    LocalDate.now().plusDays(1).toString() + ")");
            }
            
            // Validate date is not too far in the future (e.g., within 3 months)
            if (selectedDate.isAfter(LocalDate.now().plusDays(90))) {
                return createErrorResponse("Please select a date within the next 3 months. Format: YYYY-MM-DD");
            }
            
        } catch (DateTimeParseException e) {
            return createErrorResponse("Please enter a valid date in YYYY-MM-DD format (e.g., " + 
                LocalDate.now().plusDays(1).toString() + ")");
        }
        
        state.setSelectedDate(selectedDate);
        state.setAvailableTimeSlots(timeSlots);
        state.setStep(BookingSteps.TIME);
        
        conversationStates.put(state.getUserId(), state);
        
        String reply = String.format("Perfect! Date selected: %s\\n\\n" +
            "Please select your preferred time slot:\\n",
            selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        
        for (int i = 0; i < timeSlots.size(); i++) {
            reply += String.format("%d. %s\\n", i + 1, timeSlots.get(i));
        }
        
        reply += "\\nJust type the number or time slot.";
        
        ChatResponse response = new ChatResponse();
        response.setReply(reply);
        response.setIntent(ChatIntents.BOOKING_TIME_SELECTION);
        response.setSuccess(true);
        
        Map<String, Object> entities = new HashMap<>();
        entities.put("selectedDate", selectedDate.toString());
        entities.put("timeSlots", timeSlots);
        entities.put("step", "time");
        response.setEntities(entities);
        
        return response;
    }
    
    private ChatResponse handleTimeStep(String message, BookingConversationState state) {
        String selectedTimeSlot = null;
        
        // Try to parse as number
        try {
            int index = Integer.parseInt(message.trim()) - 1;
            if (index >= 0 && index < timeSlots.size()) {
                selectedTimeSlot = timeSlots.get(index);
            }
        } catch (NumberFormatException e) {
            // Try to match by time slot text
            for (String slot : timeSlots) {
                if (slot.toLowerCase().contains(message.toLowerCase().trim()) ||
                    message.toLowerCase().trim().contains(slot.substring(0, 5).toLowerCase())) {
                    selectedTimeSlot = slot;
                    break;
                }
            }
        }
        
        if (selectedTimeSlot == null) {
            String reply = "Please select a valid time slot:\\n";
            for (int i = 0; i < timeSlots.size(); i++) {
                reply += String.format("%d. %s\\n", i + 1, timeSlots.get(i));
            }
            return createErrorResponse(reply);
        }
        
        // Select the best doctor from filtered doctors (e.g., highest rated)
        Doctor selectedDoctor = state.getFilteredDoctors().stream()
            .max(Comparator.comparing(Doctor::getRating))
            .orElse(state.getFilteredDoctors().get(0));
        
        state.setSelectedTimeSlot(selectedTimeSlot);
        state.setSelectedDoctor(selectedDoctor);
        state.setStep(BookingSteps.REASON);
        
        conversationStates.put(state.getUserId(), state);
        
        String reply = String.format("Excellent! Your appointment details so far:\\n\\n" +
            "👨‍⚕️ Doctor: %s (%s)\\n" +
            "📍 Location: %s\\n" +
            "📅 Date: %s\\n" +
            "⏰ Time: %s\\n\\n" +
            "Finally, please briefly describe the reason for your visit:",
            selectedDoctor.getName(), selectedDoctor.getSpeciality(),
            state.getSelectedLocation(),
            state.getSelectedDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
            selectedTimeSlot);
        
        ChatResponse response = new ChatResponse();
        response.setReply(reply);
        response.setIntent(ChatIntents.BOOKING_REASON_INPUT);
        response.setSuccess(true);
        
        Map<String, Object> entities = new HashMap<>();
        entities.put("selectedDoctor", selectedDoctor);
        entities.put("selectedTimeSlot", selectedTimeSlot);
        entities.put("step", "reason");
        response.setEntities(entities);
        
        return response;
    }
    
    private ChatResponse handleReasonStep(String message, BookingConversationState state) {
        if (message.trim().length() < 3) {
            return createErrorResponse("Please provide a brief reason for your visit (at least 3 characters).");
        }
        
        state.setReason(message.trim());
        
        // Now book the appointment
        try {
            // Get patient user - handle guest user specially
            User patient = null;
            if ("guest".equals(state.getUserId())) {
                // For guest users, try to find the guest user account
                Optional<User> guestOpt = userRepository.findByEmail("guest@example.com");
                if (guestOpt.isPresent()) {
                    patient = guestOpt.get();
                } else {
                    conversationStates.remove(state.getUserId());
                    return createErrorResponse("Guest user not found. Please register for full booking functionality.");
                }
            } else {
                // For registered users, parse the user ID
                try {
                    Optional<User> patientOpt = userRepository.findById(Long.parseLong(state.getUserId()));
                    if (patientOpt.isPresent()) {
                        patient = patientOpt.get();
                    } else {
                        conversationStates.remove(state.getUserId());
                        return createErrorResponse("User not found. Please log in again.");
                    }
                } catch (NumberFormatException e) {
                    conversationStates.remove(state.getUserId());
                    return createErrorResponse("Invalid user ID format.");
                }
            }
            
            if (patient == null) {
                conversationStates.remove(state.getUserId());
                return createErrorResponse("Unable to identify patient for booking.");
            }
            
            // Parse time slot
            String[] timeParts = state.getSelectedTimeSlot().split(" - ");
            if (timeParts.length != 2) {
                conversationStates.remove(state.getUserId());
                return createErrorResponse("Invalid time slot format. Please try again.");
            }
            
            // Parse times
            LocalTime startTime = parseTime(timeParts[0].trim());
            LocalTime endTime = parseTime(timeParts[1].trim());
            
            // Create appointment using constructor
            Appointment appointment = new Appointment(
                state.getSelectedDoctor().getId(),
                patient.getId(),
                state.getSelectedDate(),
                startTime,
                endTime,
                state.getReason(),
                null, // No additional notes from conversational booking
                "CONFIRMED"
            );
            
            // Save appointment using the correct method
            Appointment savedAppointment = appointmentRepository.save(appointment);
            
            // Clear conversation state
            conversationStates.remove(state.getUserId());
            
            // Create success response
            String successReply = String.format("✅ **Appointment Booked Successfully!**\\n\\n" +
                "🎫 **Booking ID:** %d\\n" +
                "👨‍⚕️ **Doctor:** %s\\n" +
                "🏥 **Specialty:** %s\\n" +
                "📍 **Location:** %s\\n" +
                "📅 **Date:** %s\\n" +
                "⏰ **Time:** %s\\n" +
                "💰 **Fee:** $%s\\n" +
                "📝 **Reason:** %s\\n\\n" +
                "Your appointment has been confirmed. You will receive a confirmation email shortly.\\n\\n" +
                "Is there anything else I can help you with?",
                savedAppointment.getId(),
                state.getSelectedDoctor().getName(),
                state.getSelectedDoctor().getSpeciality(),
                state.getSelectedLocation(),
                state.getSelectedDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                state.getSelectedTimeSlot(),
                state.getSelectedDoctor().getFeesPerHour(),
                state.getReason());
            
            ChatResponse response = new ChatResponse();
            response.setReply(successReply);
            response.setIntent(ChatIntents.BOOKING_SUCCESS);
            response.setSuccess(true);
            
            Map<String, Object> entities = new HashMap<>();
            entities.put("appointmentId", savedAppointment.getId());
            entities.put("bookingConfirmed", true);
            response.setEntities(entities);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error booking appointment", e);
            conversationStates.remove(state.getUserId());
            
            String errorReply = "❌ **Booking Failed**\\n\\n" +
                "I'm sorry, there was an error booking your appointment. This could be due to:\\n" +
                "• The selected time slot is no longer available\\n" +
                "• A system error occurred\\n\\n" +
                "Please try booking again or contact our support team.\\n\\n" +
                "Would you like to try booking again?";
            
            ChatResponse response = new ChatResponse();
            response.setReply(errorReply);
            response.setIntent(ChatIntents.BOOKING_ERROR);
            response.setSuccess(false);
            
            return response;
        }
    }
    
    private ChatResponse createErrorResponse(String message) {
        ChatResponse response = new ChatResponse();
        response.setReply(message);
        response.setIntent(ChatIntents.ERROR);
        response.setSuccess(false);
        return response;
    }
    
    public void clearConversationState(String userId) {
        conversationStates.remove(userId);
    }
    
    public boolean isInBookingConversation(String userId) {
        return conversationStates.containsKey(userId);
    }
    
    public String getCurrentStep(String userId) {
        BookingConversationState state = conversationStates.get(userId);
        return state != null ? state.getStep() : null;
    }
    
    /**
     * Parse time string like "09:00 AM" into LocalTime
     */
    private LocalTime parseTime(String timeStr) {
        // Expected format: "09:00 AM" or "09:00 PM"
        String[] parts = timeStr.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time format: " + timeStr);
        }

        String timePart = parts[0];
        String amPm = parts[1];

        String[] timeParts = timePart.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Convert to 24-hour format
        if ("PM".equals(amPm) && hour != 12) {
            hour += 12;
        } else if ("AM".equals(amPm) && hour == 12) {
            hour = 0;
        }

        return LocalTime.of(hour, minute);
    }
    
    /**
     * Call AI service to analyze symptoms and return recommended doctors
     */
    private List<Doctor> analyzeSymptoms(String symptoms) {
        try {
            // Create request for AI service
            ChatMessage message = new ChatMessage("user", symptoms);
            ChatRequest request = new ChatRequest(Arrays.asList(message), "system");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

            // Call AI service
            ResponseEntity<String> response = restTemplate.postForEntity(
                aiServiceUrl + "/chat", entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse AI response
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String aiReply = jsonNode.get("reply").asText();

                // Extract specialties and conditions from AI response
                List<String> specialties = extractSpecialties(aiReply);
                List<String> conditions = extractConditions(symptoms);

                // Find doctors based on specialties
                List<Doctor> allDoctors = doctorRepository.findAll();
                return allDoctors.stream()
                    .filter(doctor -> matchesSymptoms(doctor, specialties, conditions))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error calling AI service", e);
        }
        
        // Fallback: return general practitioners if AI service fails
        return doctorRepository.findBySpecialityContainingIgnoreCase("General");
    }
    
    private List<String> extractSpecialties(String aiReply) {
        // Simple keyword matching for specialties
        List<String> specialties = new ArrayList<>();
        String lowerReply = aiReply.toLowerCase();
        
        if (lowerReply.contains("heart") || lowerReply.contains("cardiac") || lowerReply.contains("cardiologist")) {
            specialties.add("Cardiology");
        }
        if (lowerReply.contains("dermat") || lowerReply.contains("skin") || lowerReply.contains("rash")) {
            specialties.add("Dermatology");
        }
        if (lowerReply.contains("neuro") || lowerReply.contains("brain") || lowerReply.contains("headache")) {
            specialties.add("Neurology");
        }
        if (lowerReply.contains("ortho") || lowerReply.contains("bone") || lowerReply.contains("joint")) {
            specialties.add("Orthopedics");
        }
        if (lowerReply.contains("pediatr") || lowerReply.contains("child") || lowerReply.contains("baby")) {
            specialties.add("Pediatrics");
        }
        
        // Default to general medicine if no specific specialty found
        if (specialties.isEmpty()) {
            specialties.add("General");
        }
        
        return specialties;
    }
    
    private List<String> extractConditions(String symptoms) {
        List<String> conditions = new ArrayList<>();
        String lowerSymptoms = symptoms.toLowerCase();
        
        if (lowerSymptoms.contains("fever") || lowerSymptoms.contains("temperature")) {
            conditions.add("fever");
        }
        if (lowerSymptoms.contains("headache") || lowerSymptoms.contains("head pain")) {
            conditions.add("headache");
        }
        if (lowerSymptoms.contains("cough") || lowerSymptoms.contains("cold")) {
            conditions.add("respiratory");
        }
        if (lowerSymptoms.contains("stomach") || lowerSymptoms.contains("abdominal")) {
            conditions.add("digestive");
        }
        
        return conditions;
    }
    
    private boolean matchesSymptoms(Doctor doctor, List<String> specialties, List<String> conditions) {
        // Match by specialty
        for (String specialty : specialties) {
            if (doctor.getSpeciality().toLowerCase().contains(specialty.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
