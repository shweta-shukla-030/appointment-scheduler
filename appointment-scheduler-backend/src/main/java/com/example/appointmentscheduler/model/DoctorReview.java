package com.example.appointmentscheduler.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "doctor_reviews")
public class DoctorReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;
    
    @Column(name = "review_text")
    private String reviewText;
    
    @Column(name = "rating")
    private Integer rating; // Rating from 1-5

    // Default constructor
    public DoctorReview() {}

    // Constructor
    public DoctorReview(Doctor doctor, String reviewText, Integer rating) {
        this.doctor = doctor;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
