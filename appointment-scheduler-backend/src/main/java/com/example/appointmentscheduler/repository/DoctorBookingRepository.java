package com.example.appointmentscheduler.repository;

import com.example.appointmentscheduler.model.DoctorBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorBookingRepository extends JpaRepository<DoctorBooking, Long> {
    
    // Find all bookings for a specific doctor on a specific date
    List<DoctorBooking> findByDoctorIdAndBookingDate(Long doctorId, LocalDate bookingDate);
    
    // Check if a specific time slot conflicts with existing bookings
    @Query("SELECT db FROM DoctorBooking db WHERE db.doctorId = :doctorId " +
           "AND db.bookingDate = :bookingDate " +
           "AND ((db.startTime <= :startTime AND db.endTime > :startTime) " +
           "OR (db.startTime < :endTime AND db.endTime >= :endTime) " +
           "OR (:startTime <= db.startTime AND :endTime >= db.endTime))")
    List<DoctorBooking> findConflictingBookings(
        @Param("doctorId") Long doctorId,
        @Param("bookingDate") LocalDate bookingDate,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
    
    // Get all booked doctor IDs for a specific date and time range
    @Query("SELECT DISTINCT db.doctorId FROM DoctorBooking db WHERE db.bookingDate = :bookingDate " +
           "AND ((db.startTime <= :startTime AND db.endTime > :startTime) " +
           "OR (db.startTime < :endTime AND db.endTime >= :endTime) " +
           "OR (:startTime <= db.startTime AND :endTime >= db.endTime))")
    List<Long> findBookedDoctorIds(
        @Param("bookingDate") LocalDate bookingDate,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
}
