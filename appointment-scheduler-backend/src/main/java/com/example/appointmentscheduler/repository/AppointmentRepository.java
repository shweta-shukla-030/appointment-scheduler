package com.example.appointmentscheduler.repository;

import com.example.appointmentscheduler.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Find appointments by patient ID, ordered by date (newest first)
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    
    // Find appointments by doctor ID, ordered by date (newest first)
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);
    
    // Find all appointments ordered by date (newest first)
    @Query("SELECT a FROM Appointment a ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<Appointment> findAllByOrderByAppointmentDateDesc();
    
    // Find appointments by status
    List<Appointment> findByStatusOrderByAppointmentDateDesc(String status);
}
