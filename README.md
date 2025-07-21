
# Doctor Appointment Scheduler

A full-stack web application for scheduling doctor appointments with real-time availability checking and comprehensive filtering capabilities.

## 🚀 Features

### **Advanced Doctor Search & Filtering**
- ✅ Search doctors by location and specialty
- ✅ Real-time availability checking with date/time selection
- ✅ Interactive calendar date picker
- ✅ Time slot selection (9 AM - 5 PM hourly slots)
- ✅ Combined multi-filter search functionality
- ✅ Responsive design for all devices

### **Complete Appointment Booking System**
- ✅ End-to-end appointment booking flow
- ✅ Doctor profile display with ratings & reviews
- ✅ Patient information management (sample patient)
- ✅ Appointment confirmation with booking details
- ✅ Real-time conflict detection and prevention
- ✅ Professional booking interface

### **Backend API & Database**
- ✅ RESTful API with Spring Boot 3.0
- ✅ PostgreSQL database with JPA/Hibernate
- ✅ Real-time availability checking system
- ✅ Appointment management with CRUD operations
- ✅ Doctor booking conflict resolution
- ✅ Comprehensive error handling

## 🏗️ Architecture

### **Frontend (React 18)**
```
appointment-scheduler-frontend/
├── src/
│   ├── components/
│   │   ├── DoctorCard.js          # Doctor profile cards
│   │   └── DoctorFilter.js        # Advanced filtering component
│   ├── pages/
│   │   ├── HomePage.js            # Main search & results page
│   │   └── BookingPage.js         # Appointment booking page
│   ├── services/
│   │   └── DoctorAPI.js           # API integration service
│   └── styles/
│       └── App.css                # Complete styling
```

### **Backend (Spring Boot 3.0)**
```
appointment-scheduler-backend/
├── src/main/java/com/example/appointmentscheduler/
│   ├── controller/
│   │   ├── DoctorController.java      # Doctor search & filtering API
│   │   └── AppointmentController.java # Booking & appointment API
│   ├── service/
│   │   ├── DoctorService.java         # Doctor business logic
│   │   ├── DoctorBookingService.java  # Availability checking
│   │   └── AppointmentService.java    # Appointment management
│   ├── model/
│   │   ├── Doctor.java                # Doctor entity
│   │   ├── DoctorReview.java          # Review entity
│   │   ├── DoctorBooking.java         # Booking conflict tracking
│   │   └── Appointment.java           # Appointment entity
│   └── repository/
│       ├── DoctorRepository.java      # Doctor data access
│       ├── DoctorBookingRepository.java # Booking data access
│       └── AppointmentRepository.java  # Appointment data access
```

### **Database (PostgreSQL)**
```sql
doctors              # Doctor profiles with specialties & ratings
├── doctor_reviews   # Patient reviews and ratings
├── doctor_booking   # Time slot occupancy tracking
└── appointments     # Complete appointment records
```

## 🛠️ Technologies Used

### **Frontend**
- **React 18** - Modern UI library with hooks
- **React Router DOM** - Client-side routing for navigation
- **CSS3** - Custom styling with gradients and animations
- **Modern JavaScript (ES6+)** - Arrow functions, async/await, destructuring

### **Backend** 
- **Spring Boot 3.0.0** - Enterprise Java framework
- **Spring Data JPA** - Database abstraction layer
- **Hibernate** - ORM for PostgreSQL integration
- **Maven** - Dependency management and build tool
- **Jakarta Persistence** - JPA annotations for entities

### **Database**
- **PostgreSQL 16.9** - Production-ready relational database
- **Custom port 5433** - Non-standard port configuration
- **Hibernate DDL** - Automatic table creation and management

## 📋 Prerequisites

Before running this application, make sure you have:

- ✅ **Node.js 16+** and **npm** for React frontend
- ✅ **Java 17+** and **Maven 3.6+** for Spring Boot backend  
- ✅ **PostgreSQL 16.9** running on **port 5433**
- ✅ **Git** for cloning the repository

## ⚡ Quick Start

### **1. Database Setup**
```bash
# Start PostgreSQL and create database
psql -U postgres -h localhost -p 5433
CREATE DATABASE appointment_scheduler;

# Run the setup script to create tables and sample data
psql -U postgres -h localhost -p 5433 -d appointment_scheduler -f database/setup.sql
```

### **2. Backend Setup (Spring Boot)**
```bash
# Navigate to backend directory
cd appointment-scheduler-backend

# Compile and start the backend server
mvn clean compile
mvn spring-boot:run

# Server will start on http://localhost:8080
# API endpoints available at http://localhost:8080/api/
```

### **3. Frontend Setup (React)**
```bash
# Navigate to frontend directory
cd appointment-scheduler-frontend

# Install dependencies
npm install

# Install React Router (if not already installed)
npm install react-router-dom

# Start the React development server
npm start

# Application will open at http://localhost:3000
```

## 🎯 How to Use

### **Step 1: Search Doctors**
1. Open **http://localhost:3000** in your browser
2. Use the **4 filter options**:
   - **Location**: Filter by city (New York, Los Angeles, etc.)
   - **Speciality**: Filter by medical specialty (Cardiology, etc.)
   - **Date**: Select appointment date using calendar picker
   - **Time Slot**: Choose from available hourly slots (9 AM - 5 PM)

### **Step 2: View Available Doctors**
- See real-time search results with doctor profiles
- View doctor details: name, specialty, experience, fees, rating
- Check availability status based on your selected date/time

### **Step 3: Book Appointment**
1. Click **"Book Appointment"** on any available doctor
2. Review doctor details and appointment summary
3. Fill in **"Reason for Visit"** (required field)
4. Add optional additional notes
5. Click **"Confirm Booking"** to complete

### **Step 4: Confirmation**
- Get booking confirmation with all appointment details
- Appointment is saved to database with status "CONFIRMED"
- View success page with booking reference

## 📊 Database Schema

### **Core Tables**
```sql
-- Doctors with profiles and specialties
doctors (id, name, speciality, location, fees_per_hour, rating, available)

-- Patient reviews and ratings  
doctor_reviews (id, doctor_id, review_text, rating)

-- Time slot occupancy tracking for availability
doctor_booking (id, doctor_id, booking_date, start_time, end_time)

-- Complete appointment records
appointments (id, patient_id, doctor_id, appointment_date, start_time, 
             end_time, reason_for_visit, additional_notes, status)
```

### **Sample Data Included**
- **10 doctors** across 4 cities and 8 specialties
- **20 patient reviews** with realistic feedback
- **Full availability checking** system ready for testing

## 🔌 API Documentation

### **Doctor Search API**
```javascript
// Get all available doctors
GET /api/doctors/all

// Advanced search with availability checking
GET /api/doctors/search?location={city}&speciality={specialty}&date={YYYY-MM-DD}&timeSlot={HH:MM AM - HH:MM AM}

// Get filter dropdown options
GET /api/doctors/locations
GET /api/doctors/specialities
```

### **Appointment Booking API**
```javascript
// Create new appointment
POST /api/appointments/book
Content-Type: application/json

{
  "doctorId": 1,
  "patientId": 1,
  "appointmentDate": "2025-07-22",
  "startTime": "10:00 AM",
  "endTime": "11:00 AM",
  "reasonForVisit": "Regular checkup",
  "additionalNotes": "First time patient",
  "status": "CONFIRMED"
}

// Response
{
  "status": "success",
  "message": "Appointment booked successfully",
  "appointment": { ...appointmentDetails }
}
```

## 🏃‍♂️ Development Workflow

### **Backend Development**
```bash
# Navigate to backend folder first
cd appointment-scheduler-backend

# Clean and compile
mvn clean compile

# Run with proper Maven configuration
mvn -f pom.xml spring-boot:run

# Backend runs on port 8080
```

### **Frontend Development**  
```bash
# Navigate to frontend folder
cd appointment-scheduler-frontend

# Start development server with hot reload
npm start

# Frontend runs on port 3000 with proxy to backend
```

### **Database Management**
```bash
# Connect to PostgreSQL
psql -U postgres -h localhost -p 5433 -d appointment_scheduler

# View appointments
SELECT * FROM appointments ORDER BY created_at DESC;

# Check doctor availability
SELECT * FROM doctor_booking WHERE booking_date = '2025-07-22';
```

## 🔧 Troubleshooting

### **Common Issues & Solutions**

**❌ Backend won't start - "spring-boot plugin not found"**
```bash
# Solution: Use full path to pom.xml
mvn -f "C:\path\to\appointment-scheduler-backend\pom.xml" spring-boot:run
```

**❌ Database connection failed**
```bash
# Check PostgreSQL is running on correct port
pg_ctl status
netstat -an | findstr 5433

# Verify database exists
psql -U postgres -h localhost -p 5433 -l
```

**❌ Frontend can't connect to backend**
```bash
# Check backend is running on port 8080
curl http://localhost:8080/api/doctors/all

# Verify CORS is enabled in DoctorController.java
@CrossOrigin(origins = "http://localhost:3000")
```

**❌ Time slot parsing errors**
```javascript
// Ensure time slots are in correct format
"10:00 AM - 11:00 AM" ✅
"10:00" ❌
```

## 🚀 Production Deployment

### **Environment Configuration**
```properties
# Backend: application.properties
spring.datasource.url=jdbc:postgresql://your-db-host:5432/appointment_scheduler
spring.datasource.username=your-username
spring.datasource.password=your-password
```

```javascript
// Frontend: Update API base URL
const API_BASE_URL = 'https://your-backend-domain.com/api/doctors';
```

## 🤝 Contributing

1. **Fork** the repository
2. **Create** feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** changes (`git commit -m 'Add AmazingFeature'`)
4. **Push** to branch (`git push origin feature/AmazingFeature`)
5. **Open** Pull Request

## 📝 License

This project is licensed under the **MIT License** - see the `LICENSE` file for details.

## 👨‍💻 Author

**Built with ❤️ by GitHub Copilot** - Full-stack appointment scheduling solution

---

### 📞 Support

If you encounter any issues:
1. Check the **Troubleshooting** section above
2. Verify all prerequisites are installed correctly
3. Ensure PostgreSQL is running on port 5433
4. Check browser console and backend terminal for error messages

**Happy Scheduling! 🏥✨**

1. Navigate to `appointment-scheduler-backend`
2. Install Java 17+ and Maven
3. Run the application:

```bash
./mvnw spring-boot:run
```

Or import as Maven project in your IDE and run `AppointmentSchedulerApplication.java`

**Backend URL**: `http://localhost:8080`

### Frontend (React)

1. Navigate to `appointment-scheduler-frontend`
2. Install Node.js 16+ and npm
3. Install dependencies and start:

```bash
npm install
npm start
```

**Frontend URL**: `http://localhost:3000`

## Usage

1. **View All Doctors**: The homepage loads with all available doctors
2. **Filter by Location**: Select a location from the dropdown to see doctors in that area
3. **Filter by Specialty**: Choose a medical specialty to find relevant doctors
4. **Combined Search**: Use both filters together for precise results
5. **Clear Filters**: Remove all filters to see all doctors again
6. **Doctor Details**: Each card shows comprehensive doctor information
7. **Book Appointment**: Click the booking button (UI placeholder for now)

## Development Notes

- The frontend includes fallback mock data if the backend API is not available
- CORS is configured to allow frontend-backend communication
- The application is mobile-responsive
- Error handling is implemented for network issues
- Loading states provide good user experience

## Future Enhancements

- User authentication and profiles
- Real appointment booking functionality
- Doctor availability calendar
- Payment integration
- Email notifications
- Advanced search filters (rating, fees, etc.)
- Doctor profile pages with detailed information

## Notes

- Make sure PostgreSQL is installed and running.
- Environment variables for DB connection should be configured in `application.properties`.
    