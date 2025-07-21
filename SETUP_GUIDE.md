# Appointment Scheduler - Database Setup Guide

## Step-by-Step Instructions for Windows

### Prerequisites
1. **Java 17+** - Download from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [OpenJDK](https://openjdk.java.net/)
2. **Maven** - Download from [Apache Maven](https://maven.apache.org/download.cgi)
3. **Node.js 16+** - Download from [nodejs.org](https://nodejs.org/)
4. **PostgreSQL** - Download from [postgresql.org](https://www.postgresql.org/download/)

### Step 1: Setup PostgreSQL Database

1. **Open PostgreSQL Command Line (psql)** as postgres user:
   ```cmd
   psql -U postgres -h localhost
   ```

2. **Run the database setup script**:
   ```sql
   \i "c:\Users\shwet\Downloads\appointment-scheduler\database\setup.sql"
   ```

   **OR manually execute these commands:**
   ```sql
   -- Create database
   CREATE DATABASE appointment_scheduler;
   
   -- Connect to the database
   \c appointment_scheduler;
   
   -- Run the rest of the commands from setup.sql
   ```

3. **Verify the data was inserted:**
   ```sql
   SELECT name, speciality, location FROM doctors;
   SELECT COUNT(*) FROM doctor_reviews;
   ```

### Step 2: Configure Database Connection

1. **Update your PostgreSQL password** in:
   `appointment-scheduler-backend\src\main\resources\application.properties`
   
   Change this line:
   ```properties
   spring.datasource.password=yourpassword
   ```
   
   Replace `yourpassword` with your actual PostgreSQL password.

### Step 3: Start the Backend

1. **Open Command Prompt** and navigate to backend directory:
   ```cmd
   cd "c:\Users\shwet\Downloads\appointment-scheduler\appointment-scheduler-backend"
   ```

2. **Run the Spring Boot application:**
   ```cmd
   mvn clean spring-boot:run
   ```

   **OR if you prefer using Maven wrapper:**
   ```cmd
   .\mvnw.cmd clean spring-boot:run
   ```

3. **Verify backend is running:**
   - Backend should start on: http://localhost:8080
   - Test API: http://localhost:8080/api/doctors/all

### Step 4: Start the Frontend

1. **Open another Command Prompt** and navigate to frontend directory:
   ```cmd
   cd "c:\Users\shwet\Downloads\appointment-scheduler\appointment-scheduler-frontend"
   ```

2. **Install dependencies:**
   ```cmd
   npm install
   ```

3. **Start the React application:**
   ```cmd
   npm start
   ```

4. **Verify frontend is running:**
   - Frontend should start on: http://localhost:3000
   - Should automatically open in your browser

### Step 5: Test the Application

1. **Access the application** at http://localhost:3000
2. **Test filtering by location:** Select "New York" from the location dropdown
3. **Test filtering by speciality:** Select "Cardiology" from the speciality dropdown
4. **Test combined filtering:** Select both location and speciality
5. **Verify data loads from database:** Check the browser console for any errors

### API Endpoints to Test

You can test these endpoints directly in your browser or using Postman:

- **All Doctors:** http://localhost:8080/api/doctors/all
- **Search:** http://localhost:8080/api/doctors/search?location=New York&speciality=Cardiology
- **Locations:** http://localhost:8080/api/doctors/locations
- **Specialities:** http://localhost:8080/api/doctors/specialities

### Troubleshooting

#### Database Connection Issues:
1. **Check PostgreSQL is running:** Look for postgresql service in Windows Services
2. **Verify database exists:** 
   ```sql
   \l
   ```
3. **Check user permissions:**
   ```sql
   \du
   ```

#### Backend Issues:
1. **Check Java version:**
   ```cmd
   java -version
   ```
2. **Check Maven version:**
   ```cmd
   mvn -version
   ```
3. **Clean and rebuild:**
   ```cmd
   mvn clean compile
   ```

#### Frontend Issues:
1. **Check Node.js version:**
   ```cmd
   node -version
   npm -version
   ```
2. **Clear npm cache:**
   ```cmd
   npm cache clean --force
   ```
3. **Reinstall dependencies:**
   ```cmd
   rmdir /s node_modules
   npm install
   ```

### Database Schema

The application uses two main tables:

**doctors table:**
- id (Primary Key)
- name
- category
- speciality
- years_of_experience
- fees_per_hour
- rating
- location
- available
- created_at

**doctor_reviews table:**
- id (Primary Key)
- doctor_id (Foreign Key)
- review_text
- created_at

### Sample Data

The database includes:
- **10 doctors** across different specialties
- **4 locations:** New York, Los Angeles, Chicago, Miami
- **8+ specialties:** Cardiology, Family Medicine, Neurology, etc.
- **20+ reviews** distributed among doctors

### Next Steps

Once everything is running:
1. Test all filtering combinations
2. Verify database connectivity
3. Check API responses match frontend display
4. Test responsive design on different screen sizes
