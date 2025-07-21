-- Sample data for doctors
INSERT INTO doctors (name, speciality, location, years_of_experience, fees_per_hour, rating, available) 
VALUES 
    ('Dr. John Smith', 'Cardiology', 'New York', 15, 200.0, 4.8, true),
    ('Dr. Sarah Johnson', 'Dermatology', 'Los Angeles', 12, 150.0, 4.6, true),
    ('Dr. Michael Brown', 'Neurology', 'Chicago', 18, 250.0, 4.9, true),
    ('Dr. Emily Davis', 'Pediatrics', 'Houston', 10, 120.0, 4.7, true),
    ('Dr. David Wilson', 'Orthopedics', 'Phoenix', 14, 180.0, 4.5, true),
    ('Dr. Lisa Miller', 'Gynecology', 'Philadelphia', 11, 160.0, 4.6, true),
    ('Dr. James Garcia', 'Cardiology', 'San Antonio', 16, 210.0, 4.8, true),
    ('Dr. Jennifer Rodriguez', 'Dermatology', 'San Diego', 9, 140.0, 4.4, true),
    ('Dr. Robert Martinez', 'Neurology', 'Dallas', 20, 280.0, 4.9, true),
    ('Dr. Amanda Taylor', 'Pediatrics', 'San Jose', 8, 110.0, 4.3, true),
    ('Dr. Christopher Lee', 'Orthopedics', 'Austin', 13, 190.0, 4.7, true),
    ('Dr. Jessica White', 'Gynecology', 'Jacksonville', 15, 170.0, 4.6, true),
    ('Dr. Matthew Harris', 'Cardiology', 'Fort Worth', 12, 195.0, 4.5, true),
    ('Dr. Ashley Clark', 'Dermatology', 'Columbus', 7, 130.0, 4.2, true),
    ('Dr. Daniel Lewis', 'Neurology', 'Charlotte', 19, 270.0, 4.8, true)
ON CONFLICT (id) DO NOTHING;

-- Sample reviews for some doctors
INSERT INTO doctor_reviews (doctor_id, review_text, rating)
VALUES 
    (1, 'Excellent cardiologist! Very thorough and professional.', 5),
    (1, 'Dr. Smith helped me recover from my heart condition.', 4),
    (2, 'Great dermatologist, solved my skin issues quickly.', 5),
    (3, 'Outstanding neurologist, highly recommend!', 5),
    (4, 'Very good with children, my kids love Dr. Davis.', 4),
    (5, 'Fixed my knee problem, excellent orthopedic surgeon.', 5)
ON CONFLICT DO NOTHING;

-- Sample users data (patients and admins)
INSERT INTO users (email, password, first_name, last_name, phone_number, location, role, created_at, updated_at) VALUES
('patient1@email.com', 'password123', 'John', 'Doe', '+1-555-0101', 'New York', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient2@email.com', 'password123', 'Jane', 'Smith', '+1-555-0102', 'Los Angeles', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient3@email.com', 'password123', 'Mike', 'Johnson', '+1-555-0103', 'Chicago', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient4@email.com', 'password123', 'Sarah', 'Wilson', '+1-555-0104', 'Miami', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient5@email.com', 'password123', 'David', 'Brown', '+1-555-0105', 'New York', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin@hospital.com', 'admin123', 'Hospital', 'Administrator', '+1-555-9999', 'New York', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin2@clinic.com', 'admin123', 'Clinic', 'Manager', '+1-555-9998', 'Los Angeles', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;
