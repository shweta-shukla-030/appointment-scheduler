-- Create database (run this as postgres user)
CREATE DATABASE appointment_scheduler;

-- Connect to the database
\c appointment_scheduler;

-- Create doctors table
CREATE TABLE doctors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    speciality VARCHAR(100) NOT NULL,
    years_of_experience INTEGER,
    fees_per_hour DECIMAL(10,2),
    rating DECIMAL(3,2),
    location VARCHAR(255) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create reviews table (separate table for reviews)
CREATE TABLE doctor_reviews (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT REFERENCES doctors(id) ON DELETE CASCADE,
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create doctor bookings table (for tracking occupied time slots)
CREATE TABLE doctor_booking (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT REFERENCES doctors(id) ON DELETE CASCADE,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create appointments table
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT REFERENCES doctors(id) ON DELETE CASCADE,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    reason_for_visit VARCHAR(500) NOT NULL,
    additional_notes TEXT,
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Insert sample doctors data
INSERT INTO doctors (name, category, speciality, years_of_experience, fees_per_hour, rating, location, available) VALUES
('Dr. John Smith', 'Specialist', 'Cardiology', 15, 200.00, 4.8, 'New York', TRUE),
('Dr. Sarah Johnson', 'General Practitioner', 'Family Medicine', 8, 120.00, 4.5, 'Los Angeles', TRUE),
('Dr. Michael Brown', 'Specialist', 'Neurology', 12, 250.00, 4.9, 'Chicago', TRUE),
('Dr. Emily Davis', 'Specialist', 'Dermatology', 6, 180.00, 4.6, 'New York', TRUE),
('Dr. Robert Wilson', 'Specialist', 'Orthopedics', 20, 300.00, 4.7, 'Miami', TRUE),
('Dr. Lisa Anderson', 'General Practitioner', 'Internal Medicine', 10, 150.00, 4.4, 'Los Angeles', TRUE),
('Dr. David Martinez', 'Specialist', 'Cardiology', 18, 280.00, 4.9, 'Chicago', TRUE),
('Dr. Jennifer Taylor', 'Specialist', 'Pediatrics', 9, 160.00, 4.8, 'Miami', TRUE),
('Dr. Mark Thompson', 'Specialist', 'Psychiatry', 14, 220.00, 4.7, 'New York', TRUE),
('Dr. Amanda White', 'Specialist', 'Gynecology', 11, 190.00, 4.6, 'Los Angeles', TRUE);

-- Insert sample reviews
INSERT INTO doctor_reviews (doctor_id, review_text) VALUES
(1, 'Excellent doctor with great expertise'),
(1, 'Very professional and caring'),
(2, 'Great bedside manner'),
(2, 'Thorough examination and clear explanations'),
(3, 'Outstanding expertise in neurology'),
(3, 'Saved my life with accurate diagnosis'),
(4, 'Very knowledgeable about skin conditions'),
(4, 'Quick and accurate diagnosis'),
(5, 'Experienced surgeon with excellent results'),
(5, 'Professional and skilled orthopedic specialist'),
(6, 'Patient and caring approach'),
(6, 'Listens well to patient concerns'),
(7, 'Top cardiologist in the area'),
(7, 'Highly recommended by other doctors'),
(8, 'Great with children, very gentle'),
(8, 'Excellent pediatric care'),
(9, 'Compassionate psychiatrist'),
(9, 'Helped me through difficult times'),
(10, 'Professional gynecologist'),
(10, 'Thorough and respectful care');

-- Verify data
SELECT d.name, d.speciality, d.location, COUNT(r.id) as review_count 
FROM doctors d 
LEFT JOIN doctor_reviews r ON d.id = r.doctor_id 
GROUP BY d.id, d.name, d.speciality, d.location 
ORDER BY d.name;
