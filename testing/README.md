# Testing Instructions

## Available Tests

### 1. Complete Booking Flow Test
**File**: `test_complete_booking_flow.ps1`
**Purpose**: Tests the entire 6-step booking process from symptoms to final appointment
**Duration**: ~30 seconds
**Flow**:
1. Send symptoms: "I have chest pain and shortness of breath"
2. Request booking: "book appointment"
3. Select location: "Chicago"
4. Select date: "2025-07-25"
5. Select time: "2" (10:00 AM - 11:00 AM)
6. Provide reason: "Follow-up consultation for chest pain and breathing issues"

**Expected Result**: Complete appointment booking with confirmation ID

### 2. Health Check Test
**File**: `test_health_check.ps1`
**Purpose**: Quick verification that all API endpoints are responding
**Duration**: ~5 seconds

## Running Tests

### Run All Tests (Recommended)
```powershell
cd c:\Users\shwet\Downloads\appointment-scheduler\testing
.\test_health_check.ps1
.\test_complete_booking_flow.ps1
```

### Run Individual Tests
```powershell
# Health check only
.\test_health_check.ps1

# Complete booking flow only
.\test_complete_booking_flow.ps1
```

## What the Tests Validate

### Complete Booking Flow Test Validates:
- ✅ Symptom recognition and analysis
- ✅ Symptom context persistence across messages
- ✅ Location selection and filtering
- ✅ Date validation and processing
- ✅ Time slot selection
- ✅ Reason collection
- ✅ Final appointment creation in database
- ✅ Booking confirmation with appointment ID

### Success Indicators:
- All 6 steps complete without errors
- Final intent: "booking_success"
- Appointment ID generated
- Database record created

## Prerequisites
- Backend server running on `http://localhost:8080`
- John Doe user exists in database (ID: john_doe)
- Database contains doctors in Chicago with Cardiology specialty
- Future test date (2025-07-25) is valid

## Troubleshooting
- If Step 1 fails: Check if AI service is running
- If Step 3 fails: Verify doctors exist in Chicago location
- If Step 6 fails: Check database connectivity and user permissions
