package com.example.appointmentscheduler.utils;

/**
 * Constants for all chat intents used throughout the application
 */
public class ChatIntents {
    
    // AI Service Intents (from Python AI service)
    public static final String SYMPTOM_CHECK = "symptom_check";
    public static final String GENERAL_CHAT = "general_chat";
    public static final String BOOK_APPOINTMENT = "book_appointment";
    public static final String DOCTOR_RECOMMENDATION = "doctor_recommendation";
    public static final String CONFIRMATION = "confirmation";
    
    // Booking Flow Intents (from ConversationalBookingService)
    public static final String BOOKING_LOCATION_SELECTION = "booking_location_selection";
    public static final String BOOKING_DATE_SELECTION = "booking_date_selection";
    public static final String BOOKING_TIME_SELECTION = "booking_time_selection";
    public static final String BOOKING_REASON_INPUT = "booking_reason_input";
    public static final String BOOKING_SUCCESS = "booking_success";
    public static final String BOOKING_ERROR = "booking_error";
    
    // System Intents
    public static final String ERROR = "error";
    public static final String BOOKING_START = "booking_start";
    
    // Private constructor to prevent instantiation
    private ChatIntents() {
        throw new IllegalStateException("Utility class");
    }
}
