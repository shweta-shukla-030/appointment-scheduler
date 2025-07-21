package com.example.appointmentscheduler.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String category;
    
    @Column(nullable = false)
    private String speciality;
    
    @Column(name = "years_of_experience")
    private int yearsOfExperience;
    
    @Column(name = "fees_per_hour")
    private BigDecimal feesPerHour;
    
    private BigDecimal rating;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DoctorReview> reviewEntities = new ArrayList<>();
    
    @Column(nullable = false)
    private String location;
    
    private boolean available;
    
    // Additional fields for admin management
    private String experience; // e.g., "5 years experience in Cardiology"
    
    @Column(length = 1000)
    private String qualifications; // e.g., "MBBS, MD (Cardiology), Fellowship in Interventional Cardiology"
    
    @Column(length = 2000)
    private String about; // Doctor's bio/description
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    private String email;

    // Default constructor
    public Doctor() {}

    // Constructor
    public Doctor(String name, String category, String speciality, int yearsOfExperience, 
                  BigDecimal feesPerHour, BigDecimal rating, String location, boolean available) {
        this.name = name;
        this.category = category;
        this.speciality = speciality;
        this.yearsOfExperience = yearsOfExperience;
        this.feesPerHour = feesPerHour;
        this.rating = rating;
        this.location = location;
        this.available = available;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public BigDecimal getFeesPerHour() { return feesPerHour; }
    public void setFeesPerHour(BigDecimal feesPerHour) { this.feesPerHour = feesPerHour; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public List<DoctorReview> getReviewEntities() { return reviewEntities; }
    public void setReviewEntities(List<DoctorReview> reviewEntities) { this.reviewEntities = reviewEntities; }

    // Helper method to get review texts as List<String>
    @Transient
    public List<String> getReviews() {
        List<String> reviews = new ArrayList<>();
        if (reviewEntities != null) {
            for (DoctorReview review : reviewEntities) {
                reviews.add(review.getReviewText());
            }
        }
        return reviews;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
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