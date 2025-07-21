package com.example.appointmentscheduler.service;

import com.example.appointmentscheduler.model.Doctor;
import com.example.appointmentscheduler.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DoctorBookingService doctorBookingService;
    
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findByAvailableTrue();
    }
    
    public List<Doctor> findDoctorsByLocationAndSpeciality(String location, String speciality) {
        if ((location == null || location.trim().isEmpty()) && 
            (speciality == null || speciality.trim().isEmpty())) {
            return getAllDoctors();
        }
        
        return doctorRepository.findByLocationAndSpecialityContainingIgnoreCase(
            location != null && !location.trim().isEmpty() ? location : null,
            speciality != null && !speciality.trim().isEmpty() ? speciality : null
        );
    }
    
    public List<Doctor> findDoctorsByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return getAllDoctors();
        }
        return doctorRepository.findByLocationContainingIgnoreCase(location);
    }
    
    public List<Doctor> findDoctorsBySpeciality(String speciality) {
        if (speciality == null || speciality.trim().isEmpty()) {
            return getAllDoctors();
        }
        return doctorRepository.findBySpecialityContainingIgnoreCase(speciality);
    }
    
    public List<String> getUniqueLocations() {
        return doctorRepository.findUniqueLocations();
    }
    
    public List<String> getUniqueSpecialities() {
        return doctorRepository.findUniqueSpecialities();
    }
    
    public List<Doctor> findAvailableDoctors(String location, String speciality, String date, String timeSlot) {
        List<Doctor> doctors;
        
        // Get doctors based on location and/or speciality filters
        if ((location != null && !location.trim().isEmpty()) && 
            (speciality != null && !speciality.trim().isEmpty())) {
            doctors = findDoctorsByLocationAndSpeciality(location, speciality);
        } else if (location != null && !location.trim().isEmpty()) {
            doctors = findDoctorsByLocation(location);
        } else if (speciality != null && !speciality.trim().isEmpty()) {
            doctors = findDoctorsBySpeciality(speciality);
        } else {
            doctors = getAllDoctors();
        }
        
        // If no date or timeSlot specified, return all doctors from location/speciality filter
        if (date == null || date.trim().isEmpty() || timeSlot == null || timeSlot.trim().isEmpty()) {
            return doctors;
        }
        
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            
            // Parse time slot to get start and end times
            LocalTime[] times = parseTimeSlot(timeSlot);
            LocalTime startTime = times[0];
            LocalTime endTime = times[1];
            
            // Get doctor IDs that are already booked for this date and time slot
            List<Long> bookedDoctorIds = doctorBookingService.getBookedDoctorIds(bookingDate, startTime, endTime);
            
            // Filter out booked doctors
            return doctors.stream()
                    .filter(doctor -> !bookedDoctorIds.contains(doctor.getId()))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            // If date parsing fails, return all doctors from location/speciality filter
            return doctors;
        }
    }
    
    /**
     * Parse time slot string like "09:00 AM - 10:00 AM" into start and end LocalTime
     */
    private LocalTime[] parseTimeSlot(String timeSlot) {
        // Expected format: "09:00 AM - 10:00 AM"
        String[] parts = timeSlot.split(" - ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time slot format: " + timeSlot);
        }
        
        LocalTime startTime = parseTime(parts[0].trim());
        LocalTime endTime = parseTime(parts[1].trim());
        
        return new LocalTime[]{startTime, endTime};
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
    
    // Additional methods for admin management
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    public Doctor findById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }
    
    public void deleteById(Long id) {
        doctorRepository.deleteById(id);
    }
    
    // Get all doctors including unavailable ones (for admin)
    public List<Doctor> getAllDoctorsIncludingUnavailable() {
        return doctorRepository.findAll();
    }
}
