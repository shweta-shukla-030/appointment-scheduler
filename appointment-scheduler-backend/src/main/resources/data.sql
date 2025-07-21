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
