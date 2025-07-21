package com.example.appointmentscheduler.controller;

import com.example.appointmentscheduler.model.Appointment;
import com.example.appointmentscheduler.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookingRequest bookingRequest) {
        try {
            Appointment appointment = appointmentService.createAppointment(bookingRequest);
            return ResponseEntity.ok(new BookingResponse(
                "success",
                "Appointment booked successfully",
                appointment
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new BookingResponse(
                "error", 
                e.getMessage(), 
                null
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new BookingResponse(
                "error",
                "Failed to book appointment: " + e.getMessage(),
                null
            ));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable Long patientId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Inner classes for request/response
    public static class BookingRequest {
        private Long doctorId;
        private Long patientId;
        private String appointmentDate;
        private String startTime;
        private String endTime;
        private String reasonForVisit;
        private String additionalNotes;
        private String status;

        // Constructors
        public BookingRequest() {}

        public BookingRequest(Long doctorId, Long patientId, String appointmentDate, 
                            String startTime, String endTime, String reasonForVisit, 
                            String additionalNotes, String status) {
            this.doctorId = doctorId;
            this.patientId = patientId;
            this.appointmentDate = appointmentDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.reasonForVisit = reasonForVisit;
            this.additionalNotes = additionalNotes;
            this.status = status;
        }

        // Getters and Setters
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public String getAppointmentDate() { return appointmentDate; }
        public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public String getReasonForVisit() { return reasonForVisit; }
        public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }

        public String getAdditionalNotes() { return additionalNotes; }
        public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class BookingResponse {
        private String status;
        private String message;
        private Appointment appointment;

        public BookingResponse(String status, String message, Appointment appointment) {
            this.status = status;
            this.message = message;
            this.appointment = appointment;
        }

        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Appointment getAppointment() { return appointment; }
        public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    }
}
