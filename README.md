
# Doctor Appointment Scheduler with AI Agent

A full-stack web application for scheduling doctor appointments with **AI-powered symptom analysis** and 3-tier intelligent fallback mechanism for accurate specialty recommendations.

## 🚀 Features

### **🤖 AI-Powered Symptom Analysis**
- ✅ **3-Tier Fallback System**: Static mapping → GPT4All analysis → Clarification modal
- ✅ **Smart Symptom Recognition**: Natural language processing for symptom-to-specialty mapping
- ✅ **Adaptive Confidence Scoring**: High confidence for clear symptoms, low confidence triggers clarification
- ✅ **Interactive Clarification Flow**: Modal popup for vague symptoms with follow-up questions
- ✅ **Real-time Chat Interface**: Conversational AI for symptom analysis and doctor recommendations

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

### **AI Agent Service (FastAPI + GPT4All)**
```
ai-agent-service/
├── ai_server.py              # FastAPI server with 3-tier fallback logic
├── prompts.py                # Symptom mapping & GPT4All prompts
├── requirements.txt          # Python dependencies
└── ai-env/                   # Virtual environment
```

### **Frontend (React 18)**
```
appointment-scheduler-frontend/
├── src/
│   ├── chat/
│   │   ├── components/
│   │   │   ├── ChatWidget.js        # Main chat interface
│   │   │   └── ClarificationModal.js # Clarification popup
│   │   ├── services/
│   │   │   └── ChatService.js       # AI agent API integration
│   │   └── styles/
│   │       └── Chat.css             # Chat styling
│   ├── components/
│   │   ├── DoctorCard.js            # Doctor profile cards
│   │   └── DoctorFilter.js          # Advanced filtering component
│   ├── pages/
│   │   ├── HomePage.js              # Main search & results page
│   │   └── BookingPage.js           # Appointment booking page
│   ├── services/
│   │   └── DoctorAPI.js             # API integration service
│   └── styles/
│       └── App.css                  # Complete styling
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

### **AI Agent Service**
- **FastAPI** - Modern Python web framework for AI service
- **GPT4All** - Local LLM for symptom analysis (orca-mini-3b model)
- **Python 3.11+** - Core language with virtual environment
- **Uvicorn** - ASGI server for FastAPI deployment

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

- ✅ **Python 3.11+** and **pip** for AI agent service
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

### **2. AI Agent Service Setup (FastAPI + GPT4All)**
```bash
# Navigate to AI service directory
cd ai-agent-service

# Create and activate virtual environment
python -m venv ai-env
ai-env\Scripts\activate  # Windows
# source ai-env/bin/activate  # macOS/Linux

# Install dependencies
pip install -r requirements.txt

# Start the AI service (will download GPT4All model on first run)
python ai_server.py

# AI service will start on http://localhost:5000
# Health check: http://localhost:5000/health
```

### **3. Backend Setup (Spring Boot)**
```bash
# Navigate to backend directory
cd appointment-scheduler-backend

# Compile and start the backend server (with memory optimization if needed)
mvn clean compile
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m"

# Server will start on http://localhost:8080
# API endpoints available at http://localhost:8080/api/
```

### **4. Frontend Setup (React)**
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

### **Step 1: AI-Powered Symptom Analysis**
1. Open **http://localhost:3000** in your browser
2. Use the **AI Chat Widget** in the bottom-right corner
3. **Describe your symptoms** in natural language:
   - ✅ **Clear symptoms**: "I have chest pain" → Direct specialty recommendation
   - ✅ **Complex symptoms**: "I feel dizzy and nauseous after eating" → AI analysis
   - ✅ **Vague symptoms**: "I don't feel good" → Clarification modal appears

### **Step 2: Clarification Flow (for vague symptoms)**
1. When the clarification modal appears:
   - Answer the follow-up questions about your symptoms
   - Provide specific details (location, type, duration)
   - Submit clarification for refined recommendation

### **Step 3: Get Specialty Recommendation**
- Receive AI-powered doctor specialty recommendation
- See confidence level and reasoning
- Option to proceed with doctor search in that specialty

### **Step 4: Search & Book Doctors**
1. Use the **4 filter options**:
   - **Location**: Filter by city (New York, Los Angeles, etc.)
   - **Speciality**: Use AI recommendation or select manually
   - **Date**: Select appointment date using calendar picker
   - **Time Slot**: Choose from available hourly slots (9 AM - 5 PM)

### **Step 5: Complete Booking**
1. Click **"Book Appointment"** on any available doctor
2. Review doctor details and appointment summary
3. Fill in **"Reason for Visit"** (can use AI symptom analysis)
4. Add optional additional notes
5. Click **"Confirm Booking"** to complete

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

## 🧠 AI Implementation Details

### **3-Tier Fallback System**

The AI agent uses a sophisticated 3-tier fallback mechanism:

#### **Level 1: Static Symptom Mapping (< 1 second)**
- **Fast lookup** for common symptoms like "chest pain", "back pain"
- **Word boundary matching** prevents false matches (e.g., "ear" in "heart")
- **Direct specialty mapping** with 100% confidence
- **40+ predefined symptoms** covering major specialties

#### **Level 2: GPT4All Dynamic Analysis (2-5 seconds)**
- **Local LLM processing** using orca-mini-3b model
- **Confidence scoring** (0.0-1.0) based on symptom clarity
- **JSON response parsing** with fallback keyword detection
- **Specialty recommendation** for complex symptom descriptions

#### **Level 3: Clarification Flow (Interactive)**
- **Modal popup** for vague symptoms (confidence < 0.7)
- **Follow-up questions** about location, type, duration
- **Combined message processing** after clarification
- **Level 2.5 override** for clearly identified post-clarification symptoms

### **Confidence Logic**
```javascript
// High Confidence (0.8-1.0) - Direct recommendation
"chest pain" → Cardiology (0.9)
"stomach pain after eating" → Gastroenterology (0.8)

// Low Confidence (0.3-0.6) - Requires clarification  
"not feeling good" → Clarification modal (0.4)
"something is wrong" → Clarification modal (0.3)

// Clear Symptom Override
"dizzy and nauseous after eating" → Gastroenterology (boosted to 0.8)
```

### **API Endpoints**

#### **AI Agent Service (Port 5000)**
- `GET /` - Health check and model status
- `POST /chat` - Main symptom analysis endpoint
- `GET /health` - Detailed service health

#### **Backend Integration (Port 8080)**
- `POST /api/chat` - Frontend to backend chat proxy
- `POST /api/clarification` - Clarification processing

## 🏃‍♂️ Development Workflow

### **AI Service Development**
```bash
# Navigate to AI service folder
cd ai-agent-service

# Activate virtual environment
ai-env\Scripts\activate

# Install new dependencies
pip install package_name
pip freeze > requirements.txt

# Run with debug logging
python ai_server.py
```

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

### **AI Service Issues**

**❌ GPT4All model not loading**
```bash
# Solution: Ensure sufficient memory and re-download model
rm -rf ~/.cache/gpt4all/  # Clear model cache
python ai_server.py  # Re-download model
```

**❌ "No module named 'gpt4all'"**
```bash
# Solution: Reinstall in virtual environment
ai-env\Scripts\activate
pip install --upgrade gpt4all fastapi uvicorn
```

**❌ AI service returns wrong specialty**
```bash
# Solution: Check confidence scores in logs
# Look for "GPT4All parsed response - Confidence: X.X"
# Adjust prompts.py CONFIDENCE_RULES if needed
```

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
    