package com.example.appointmentscheduler.repository;

import com.example.appointmentscheduler.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    // Find doctors by location (case-insensitive)
    @Query("SELECT d FROM Doctor d WHERE d.available = true AND " +
           "(:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Doctor> findByLocationContainingIgnoreCase(@Param("location") String location);
    
    // Find doctors by speciality (case-insensitive)
    @Query("SELECT d FROM Doctor d WHERE d.available = true AND " +
           "(:speciality IS NULL OR LOWER(d.speciality) LIKE LOWER(CONCAT('%', :speciality, '%')))")
    List<Doctor> findBySpecialityContainingIgnoreCase(@Param("speciality") String speciality);
    
    // Find doctors by both location and speciality
    @Query("SELECT d FROM Doctor d WHERE d.available = true AND " +
           "(:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:speciality IS NULL OR LOWER(d.speciality) LIKE LOWER(CONCAT('%', :speciality, '%')))")
    List<Doctor> findByLocationAndSpecialityContainingIgnoreCase(
        @Param("location") String location, 
        @Param("speciality") String speciality
    );
    
    // Find all available doctors
    List<Doctor> findByAvailableTrue();
    
    // Get unique locations
    @Query("SELECT DISTINCT d.location FROM Doctor d WHERE d.available = true ORDER BY d.location")
    List<String> findUniqueLocations();
    
    // Get unique specialities
    @Query("SELECT DISTINCT d.speciality FROM Doctor d WHERE d.available = true ORDER BY d.speciality")
    List<String> findUniqueSpecialities();
}
