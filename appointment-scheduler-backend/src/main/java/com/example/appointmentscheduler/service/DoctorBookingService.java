package com.example.appointmentscheduler.service;

import com.example.appointmentscheduler.model.DoctorBooking;
import com.example.appointmentscheduler.repository.DoctorBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DoctorBookingService {
    
    @Autowired
    private DoctorBookingRepository doctorBookingRepository;

    /**
     * Check if a specific time slot is available for a doctor
     */
    public boolean isTimeSlotAvailable(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<DoctorBooking> conflicts = doctorBookingRepository.findConflictingBookings(
            doctorId, date, startTime, endTime
        );
        return conflicts.isEmpty();
    }

    /**
     * Get all doctor IDs that are booked during a specific time slot
     */
    public List<Long> getBookedDoctorIds(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return doctorBookingRepository.findBookedDoctorIds(date, startTime, endTime);
    }

    /**
     * Book a time slot for a doctor (create a booking record)
     */
    public DoctorBooking createBooking(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // First check if the slot is available
        if (!isTimeSlotAvailable(doctorId, date, startTime, endTime)) {
            throw new IllegalStateException("Time slot is not available");
        }
        
        DoctorBooking booking = new DoctorBooking(doctorId, date, startTime, endTime);
        return doctorBookingRepository.save(booking);
    }

    /**
     * Get all bookings for a doctor on a specific date
     */
    public List<DoctorBooking> getDoctorBookings(Long doctorId, LocalDate date) {
        return doctorBookingRepository.findByDoctorIdAndBookingDate(doctorId, date);
    }

    /**
     * Helper method to parse time slot string (e.g., "14:00") and create end time (1 hour later)
     */
    public LocalTime[] parseTimeSlot(String timeSlot) {
        LocalTime startTime = LocalTime.parse(timeSlot);
        LocalTime endTime = startTime.plusHours(1); // Assume 1-hour appointments
        return new LocalTime[]{startTime, endTime};
    }
}
