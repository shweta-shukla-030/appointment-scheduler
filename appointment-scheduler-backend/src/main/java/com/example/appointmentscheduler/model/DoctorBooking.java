package com.example.appointmentscheduler.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_booking")
public class DoctorBooking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;
    
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor
    public DoctorBooking() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor
    public DoctorBooking(Long doctorId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
        this();
        this.doctorId = doctorId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}