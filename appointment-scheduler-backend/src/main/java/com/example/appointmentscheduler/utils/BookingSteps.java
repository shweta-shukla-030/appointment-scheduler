package com.example.appointmentscheduler.utils;

/**
 * Constants for booking conversation steps
 */
public class BookingSteps {
    
    // Booking conversation flow steps
    public static final String SYMPTOMS = "symptoms";
    public static final String LOCATION = "location";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String REASON = "reason";
    public static final String CONFIRM = "confirm";
    public static final String COMPLETE = "complete";
    
    // Private constructor to prevent instantiation
    private BookingSteps() {
        throw new IllegalStateException("Utility class");
    }
}
