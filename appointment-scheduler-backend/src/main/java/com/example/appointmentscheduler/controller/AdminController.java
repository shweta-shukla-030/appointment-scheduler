package com.example.appointmentscheduler.controller;

import com.example.appointmentscheduler.model.Doctor;
import com.example.appointmentscheduler.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    
    @Autowired
    private DoctorService doctorService;
    
    // Add new doctor
    @PostMapping("/doctors")
    public ResponseEntity<?> addDoctor(@RequestBody AddDoctorRequest request) {
        try {
            // In real app, verify admin authentication here
            
            Doctor doctor = new Doctor();
            doctor.setName(request.getName());
            doctor.setSpeciality(request.getSpeciality());
            doctor.setLocation(request.getLocation());
            doctor.setFeesPerHour(request.getFeesPerHour() != null ? BigDecimal.valueOf(request.getFeesPerHour()) : null);
            doctor.setRating(request.getRating() != null ? BigDecimal.valueOf(request.getRating()) : BigDecimal.valueOf(4.0));
            doctor.setAvailable(true);
            doctor.setExperience(request.getExperience());
            doctor.setQualifications(request.getQualifications());
            doctor.setAbout(request.getAbout());
            doctor.setContactNumber(request.getContactNumber());
            doctor.setEmail(request.getEmail());
            
            Doctor savedDoctor = doctorService.saveDoctor(doctor);
            
            return ResponseEntity.ok(new AdminResponse("success", "Doctor added successfully", savedDoctor));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AdminResponse("error", "Failed to add doctor: " + e.getMessage(), null));
        }
    }
    
    // Update doctor
    @PutMapping("/doctors/{doctorId}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long doctorId, @RequestBody AddDoctorRequest request) {
        try {
            Doctor existingDoctor = doctorService.findById(doctorId);
            if (existingDoctor == null) {
                return ResponseEntity.badRequest().body(new AdminResponse("error", "Doctor not found", null));
            }
            
            existingDoctor.setName(request.getName());
            existingDoctor.setSpeciality(request.getSpeciality());
            existingDoctor.setLocation(request.getLocation());
            existingDoctor.setFeesPerHour(request.getFeesPerHour() != null ? BigDecimal.valueOf(request.getFeesPerHour()) : null);
            existingDoctor.setRating(request.getRating() != null ? BigDecimal.valueOf(request.getRating()) : null);
            existingDoctor.setExperience(request.getExperience());
            existingDoctor.setQualifications(request.getQualifications());
            existingDoctor.setAbout(request.getAbout());
            existingDoctor.setContactNumber(request.getContactNumber());
            existingDoctor.setEmail(request.getEmail());
            
            Doctor updatedDoctor = doctorService.saveDoctor(existingDoctor);
            
            return ResponseEntity.ok(new AdminResponse("success", "Doctor updated successfully", updatedDoctor));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AdminResponse("error", "Failed to update doctor: " + e.getMessage(), null));
        }
    }
    
    // Delete doctor
    @DeleteMapping("/doctors/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        try {
            doctorService.deleteById(doctorId);
            return ResponseEntity.ok(new AdminResponse("success", "Doctor deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AdminResponse("error", "Failed to delete doctor: " + e.getMessage(), null));
        }
    }
    
    // Get all doctors for admin management
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctorsForAdmin() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctorsIncludingUnavailable();
            return ResponseEntity.ok(new AdminResponse("success", "Doctors retrieved successfully", doctors));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AdminResponse("error", "Failed to retrieve doctors: " + e.getMessage(), null));
        }
    }
    
    // Toggle doctor availability
    @PutMapping("/doctors/{doctorId}/availability")
    public ResponseEntity<?> toggleDoctorAvailability(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorService.findById(doctorId);
            if (doctor == null) {
                return ResponseEntity.badRequest().body(new AdminResponse("error", "Doctor not found", null));
            }
            
            doctor.setAvailable(!doctor.isAvailable());
            Doctor updatedDoctor = doctorService.saveDoctor(doctor);
            
            String message = updatedDoctor.isAvailable() ? "Doctor is now available" : "Doctor is now unavailable";
            return ResponseEntity.ok(new AdminResponse("success", message, updatedDoctor));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AdminResponse("error", "Failed to update availability: " + e.getMessage(), null));
        }
    }
    
    // Inner Classes
    public static class AddDoctorRequest {
        private String name;
        private String speciality;
        private String location;
        private Double feesPerHour;
        private Double rating;
        private String experience;
        private String qualifications;
        private String about;
        private String contactNumber;
        private String email;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSpeciality() { return speciality; }
        public void setSpeciality(String speciality) { this.speciality = speciality; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getFeesPerHour() { return feesPerHour; }
        public void setFeesPerHour(Double feesPerHour) { this.feesPerHour = feesPerHour; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }
        public String getQualifications() { return qualifications; }
        public void setQualifications(String qualifications) { this.qualifications = qualifications; }
        public String getAbout() { return about; }
        public void setAbout(String about) { this.about = about; }
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class AdminResponse {
        private String status;
        private String message;
        private Object data;
        
        public AdminResponse(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
        
        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
