package com.example.appointmentscheduler.controller;

import com.example.appointmentscheduler.model.Doctor;
import com.example.appointmentscheduler.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    // Get all doctors
    @GetMapping("/all")
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }
    
    // Get doctors by location and/or speciality with availability checking
    @GetMapping("/search")
    public List<Doctor> getDoctorsByFilters(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String speciality,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String timeSlot) {
        return doctorService.findAvailableDoctors(location, speciality, date, timeSlot);
    }
    
    // Get doctors by location only
    @GetMapping("/by-location")
    public List<Doctor> getDoctorsByLocation(@RequestParam String location) {
        return doctorService.findDoctorsByLocation(location);
    }
    
    // Get doctors by speciality only
    @GetMapping("/by-speciality")
    public List<Doctor> getDoctorsBySpeciality(@RequestParam String speciality) {
        return doctorService.findDoctorsBySpeciality(speciality);
    }
    
    // Get unique locations for filter dropdown
    @GetMapping("/locations")
    public List<String> getUniqueLocations() {
        return doctorService.getUniqueLocations();
    }
    
    // Get unique specialities for filter dropdown
    @GetMapping("/specialities")
    public List<String> getUniqueSpecialities() {
        return doctorService.getUniqueSpecialities();
    }
}