package com.example.appointmentscheduler.service;

import com.example.appointmentscheduler.controller.AppointmentController.BookingRequest;
import com.example.appointmentscheduler.model.Appointment;
import com.example.appointmentscheduler.model.DoctorBooking;
import com.example.appointmentscheduler.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorBookingService doctorBookingService;

    @Transactional
    public Appointment createAppointment(BookingRequest request) {
        try {
            // Parse date and times
            LocalDate appointmentDate = LocalDate.parse(request.getAppointmentDate());
            LocalTime startTime = parseTime(request.getStartTime());
            LocalTime endTime = parseTime(request.getEndTime());

            // Check if the time slot is still available
            if (!doctorBookingService.isTimeSlotAvailable(request.getDoctorId(), appointmentDate, startTime, endTime)) {
                throw new IllegalStateException("The selected time slot is no longer available");
            }

            // Create the appointment
            Appointment appointment = new Appointment(
                request.getDoctorId(),
                request.getPatientId(),
                appointmentDate,
                startTime,
                endTime,
                request.getReasonForVisit(),
                request.getAdditionalNotes(),
                request.getStatus() != null ? request.getStatus() : "CONFIRMED"
            );

            // Save the appointment
            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Create a doctor booking record to mark the time slot as occupied
            doctorBookingService.createBooking(
                request.getDoctorId(),
                appointmentDate,
                startTime,
                endTime
            );

            return savedAppointment;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create appointment: " + e.getMessage(), e);
        }
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByAppointmentDateDesc();
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
}
