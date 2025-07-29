# Complete End-to-End Booking Flow Test
# Tests the entire booking process from symptoms to final appointment confirmation

Write-Host "=== Complete Booking Flow Test ===" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"
$userId = "john_doe"
$testDate = "2025-07-25"  # Future date for testing

Write-Host "Testing complete booking flow for user: $userId" -ForegroundColor Yellow
Write-Host "Test Date: $testDate" -ForegroundColor Yellow
Write-Host ""

# Step 1: Send symptoms message
Write-Host "Step 1: Sending symptoms..." -ForegroundColor Cyan
$symptomRequest = @{
    message = "I have chest pain and shortness of breath"
    userId = $userId
} | ConvertTo-Json

try {
    $step1Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $symptomRequest
    Write-Host "✓ Symptoms processed" -ForegroundColor Green
    Write-Host "Intent: $($step1Response.intent)" -ForegroundColor White
    Write-Host "Reply: $($step1Response.reply)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Error in Step 1: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Request booking
Write-Host "Step 2: Requesting appointment booking..." -ForegroundColor Cyan
$bookingRequest = @{
    message = "book appointment"
    userId = $userId
} | ConvertTo-Json

try {
    $step2Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $bookingRequest
    Write-Host "✓ Booking initiated" -ForegroundColor Green
    Write-Host "Intent: $($step2Response.intent)" -ForegroundColor White
    Write-Host "Reply: $($step2Response.reply)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Error in Step 2: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Select location
Write-Host "Step 3: Selecting location..." -ForegroundColor Cyan
$locationRequest = @{
    message = "Chicago"
    userId = $userId
} | ConvertTo-Json

try {
    $step3Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $locationRequest
    Write-Host "✓ Location selected" -ForegroundColor Green
    Write-Host "Intent: $($step3Response.intent)" -ForegroundColor White
    Write-Host "Reply: $($step3Response.reply)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Error in Step 3: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Select date
Write-Host "Step 4: Selecting date..." -ForegroundColor Cyan
$dateRequest = @{
    message = $testDate
    userId = $userId
} | ConvertTo-Json

try {
    $step4Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $dateRequest
    Write-Host "✓ Date selected" -ForegroundColor Green
    Write-Host "Intent: $($step4Response.intent)" -ForegroundColor White
    Write-Host "Reply: $($step4Response.reply)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Error in Step 4: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 5: Select time slot
Write-Host "Step 5: Selecting time slot..." -ForegroundColor Cyan
$timeRequest = @{
    message = "2"  # Select 10:00 AM - 11:00 AM
    userId = $userId
} | ConvertTo-Json

try {
    $step5Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $timeRequest
    Write-Host "✓ Time slot selected" -ForegroundColor Green
    Write-Host "Intent: $($step5Response.intent)" -ForegroundColor White
    Write-Host "Reply: $($step5Response.reply)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Error in Step 5: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 6: Provide reason for visit
Write-Host "Step 6: Providing reason for visit..." -ForegroundColor Cyan
$reasonRequest = @{
    message = "Follow-up consultation for chest pain and breathing issues"
    userId = $userId
} | ConvertTo-Json

try {
    $step6Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $reasonRequest
    Write-Host "✓ Reason provided" -ForegroundColor Green
    Write-Host "Intent: $($step6Response.intent)" -ForegroundColor White
    Write-Host "Final Reply: $($step6Response.reply)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Error in Step 6: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test Analysis
Write-Host "=== Test Analysis ===" -ForegroundColor Green
Write-Host ""

# Check if symptoms were correctly identified
if ($step1Response.intent -eq "symptom_check") {
    Write-Host "✓ Symptoms correctly identified" -ForegroundColor Green
} else {
    Write-Host "✗ Symptoms not identified correctly" -ForegroundColor Red
}

# Check if booking was initiated with symptom memory
if ($step2Response.intent -match "booking" -and $step2Response.reply -match "symptom") {
    Write-Host "✓ Booking initiated with symptom context" -ForegroundColor Green
} else {
    Write-Host "✗ Booking did not remember symptoms" -ForegroundColor Red
}

# Check if location was processed
if ($step3Response.intent -match "date") {
    Write-Host "✓ Location processing successful" -ForegroundColor Green
} else {
    Write-Host "✗ Location processing failed" -ForegroundColor Red
}

# Check if date was processed
if ($step4Response.intent -match "time") {
    Write-Host "✓ Date processing successful" -ForegroundColor Green
} else {
    Write-Host "✗ Date processing failed" -ForegroundColor Red
}

# Check if time was processed
if ($step5Response.intent -match "reason") {
    Write-Host "✓ Time processing successful" -ForegroundColor Green
} else {
    Write-Host "✗ Time processing failed" -ForegroundColor Red
}

# Check if appointment was successfully booked
if ($step6Response.intent -eq "booking_success") {
    Write-Host "✓ Appointment successfully booked!" -ForegroundColor Green
    
    # Extract appointment ID if available
    if ($step6Response.reply -match "Booking ID.*?(\d+)") {
        $appointmentId = $matches[1]
        Write-Host "✓ Appointment ID: $appointmentId" -ForegroundColor Green
    }
} else {
    Write-Host "✗ Appointment booking failed" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Complete Booking Flow Test Finished ===" -ForegroundColor Green

# Summary
Write-Host ""
Write-Host "=== SUMMARY ===" -ForegroundColor Yellow
Write-Host "Test Steps Completed: 6/6" -ForegroundColor White
Write-Host "Symptom → Location → Date → Time → Reason → Booking" -ForegroundColor White

if ($step6Response.intent -eq "booking_success") {
    Write-Host "RESULT: ✅ END-TO-END BOOKING SUCCESSFUL" -ForegroundColor Green
} else {
    Write-Host "RESULT: ❌ BOOKING INCOMPLETE" -ForegroundColor Red
}
