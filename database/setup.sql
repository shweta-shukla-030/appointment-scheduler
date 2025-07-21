-- Create database (run this as postgres user)
CREATE DATABASE appointment_scheduler;

-- Connect to the database
\c appointment_scheduler;

-- Create users table for authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    location VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('PATIENT', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create doctors table with additional fields for admin management
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
    experience TEXT,  -- e.g., "5 years experience in Cardiology"
    qualifications TEXT,  -- e.g., "MBBS, MD (Cardiology)"
    about TEXT,  -- Doctor's bio/description
    contact_number VARCHAR(20),
    email VARCHAR(255),
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

-- Insert sample users (patients and admin)
INSERT INTO users (email, password, first_name, last_name, phone_number, location, role) VALUES
-- Sample patients
('patient1@email.com', 'password123', 'John', 'Doe', '+1-555-0101', 'New York', 'PATIENT'),
('patient2@email.com', 'password123', 'Jane', 'Smith', '+1-555-0102', 'Los Angeles', 'PATIENT'),
('patient3@email.com', 'password123', 'Mike', 'Johnson', '+1-555-0103', 'Chicago', 'PATIENT'),
('patient4@email.com', 'password123', 'Sarah', 'Wilson', '+1-555-0104', 'Miami', 'PATIENT'),
('patient5@email.com', 'password123', 'David', 'Brown', '+1-555-0105', 'New York', 'PATIENT'),

-- Sample admin users
('admin@hospital.com', 'admin123', 'Hospital', 'Administrator', '+1-555-9999', 'New York', 'ADMIN'),
('admin2@clinic.com', 'admin123', 'Clinic', 'Manager', '+1-555-9998', 'Los Angeles', 'ADMIN');

-- Update existing doctors with additional information
UPDATE doctors SET 
    experience = '10+ years of experience in ' || speciality,
    qualifications = 'MBBS, MD (' || speciality || ')',
    about = 'Experienced medical professional specializing in ' || speciality || ' with a focus on patient-centered care.',
    contact_number = '+1-555-' || LPAD(id::text, 4, '0'),
    email = LOWER(REPLACE(name, ' ', '.')) || '@hospital.com'
WHERE id <= 10;

-- Verify all data
SELECT 'Users Count' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Doctors Count', COUNT(*) FROM doctors
UNION ALL
SELECT 'Reviews Count', COUNT(*) FROM doctor_reviews
UNION ALL
SELECT 'Bookings Count', COUNT(*) FROM doctor_booking
UNION ALL
SELECT 'Appointments Count', COUNT(*) FROM appointments;

-- Show sample data
SELECT 'SAMPLE USERS:' as info;
SELECT u.id, u.email, u.first_name, u.last_name, u.role, u.location FROM users u ORDER BY u.role, u.id;

SELECT 'SAMPLE DOCTORS:' as info;
SELECT d.id, d.name, d.speciality, d.location, d.fees_per_hour, d.available, d.email 
FROM doctors d 
ORDER BY d.speciality, d.name;
