package com.example.appointmentscheduler.service;

import com.example.appointmentscheduler.model.User;
import com.example.appointmentscheduler.model.UserRole;
import com.example.appointmentscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Registration
    public User registerUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Save user with encrypted password (in real app, use BCrypt)
        return userRepository.save(user);
    }
    
    // Login
    public Optional<User> loginUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
    
    // Get user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Get all patients
    public List<User> getAllPatients() {
        return userRepository.findByRole(UserRole.PATIENT);
    }
    
    // Get all admins
    public List<User> getAllAdmins() {
        return userRepository.findByRole(UserRole.ADMIN);
    }
    
    // Update user profile
    public User updateUser(Long userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setLocation(updatedUser.getLocation());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    // Delete user
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    // Get users by location
    public List<User> getUsersByLocation(String location) {
        return userRepository.findByLocation(location);
    }
    
    // Validate email format
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    // Check if user is admin
    public boolean isAdmin(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() && user.get().getRole() == UserRole.ADMIN;
    }
}
