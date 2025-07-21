package com.example.appointmentscheduler.controller;

import com.example.appointmentscheduler.model.User;
import com.example.appointmentscheduler.model.UserRole;
import com.example.appointmentscheduler.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    // Patient Registration
    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@RequestBody PatientRegistrationRequest request) {
        try {
            // Validate input
            if (!userService.isValidEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", "Invalid email format", null));
            }
            
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", "Password must be at least 6 characters", null));
            }
            
            // Create new patient
            User patient = new User();
            patient.setEmail(request.getEmail());
            patient.setPassword(request.getPassword()); // In real app, hash this
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setPhoneNumber(request.getPhoneNumber());
            patient.setLocation(request.getLocation());
            patient.setRole(UserRole.PATIENT);
            
            User savedPatient = userService.registerUser(patient);
            
            return ResponseEntity.ok(new AuthResponse("success", "Patient registered successfully", savedPatient));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("error", e.getMessage(), null));
        }
    }
    
    // Admin Registration (restricted)
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegistrationRequest request) {
        try {
            // In real app, add admin verification logic here
            
            User admin = new User();
            admin.setEmail(request.getEmail());
            admin.setPassword(request.getPassword()); // In real app, hash this
            admin.setFirstName(request.getFirstName());
            admin.setLastName(request.getLastName());
            admin.setRole(UserRole.ADMIN);
            
            User savedAdmin = userService.registerUser(admin);
            
            return ResponseEntity.ok(new AuthResponse("success", "Admin registered successfully", savedAdmin));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("error", e.getMessage(), null));
        }
    }
    
    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = userService.loginUser(request.getEmail(), request.getPassword());
        
        if (user.isPresent()) {
            return ResponseEntity.ok(new AuthResponse("success", "Login successful", user.get()));
        } else {
            return ResponseEntity.badRequest().body(new AuthResponse("error", "Invalid email or password", null));
        }
    }
    
    // Get user profile
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            Optional<User> user = userService.findByEmail("");
            // In real app, get user by ID from JWT token
            return ResponseEntity.ok(new AuthResponse("success", "Profile retrieved", user.orElse(null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("error", "User not found", null));
        }
    }
    
    // Update profile
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
        try {
            User updated = userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok(new AuthResponse("success", "Profile updated successfully", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("error", e.getMessage(), null));
        }
    }
    
    // Inner Classes for Requests and Responses
    public static class PatientRegistrationRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String location;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }
    
    public static class AdminRegistrationRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class AuthResponse {
        private String status;
        private String message;
        private User user;
        
        public AuthResponse(String status, String message, User user) {
            this.status = status;
            this.message = message;
            this.user = user;
        }
        
        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}
