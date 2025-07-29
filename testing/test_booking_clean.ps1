Write-Host "=== Complete Booking Flow Test ===" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"
$userId = "john_doe"
$testDate = "2025-07-25"

Write-Host "Testing complete booking flow for user: $userId" -ForegroundColor Yellow
Write-Host ""

# Step 1: Send symptoms
Write-Host "Step 1: Sending symptoms..." -ForegroundColor Cyan
$symptomRequest = @{
    message = "I have chest pain and shortness of breath"
    userId = $userId
} | ConvertTo-Json

try {
    $step1Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $symptomRequest
    Write-Host "SUCCESS: Symptoms processed" -ForegroundColor Green
    Write-Host "Intent: $($step1Response.intent)" -ForegroundColor White
} catch {
    Write-Host "ERROR in Step 1: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Request booking
Write-Host ""
Write-Host "Step 2: Requesting appointment booking..." -ForegroundColor Cyan
$bookingRequest = @{
    message = "book appointment"
    userId = $userId
} | ConvertTo-Json

try {
    $step2Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $bookingRequest
    Write-Host "SUCCESS: Booking initiated" -ForegroundColor Green
    Write-Host "Intent: $($step2Response.intent)" -ForegroundColor White
} catch {
    Write-Host "ERROR in Step 2: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Select location
Write-Host ""
Write-Host "Step 3: Selecting location..." -ForegroundColor Cyan
$locationRequest = @{
    message = "Chicago"
    userId = $userId
} | ConvertTo-Json

try {
    $step3Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $locationRequest
    Write-Host "SUCCESS: Location selected" -ForegroundColor Green
    Write-Host "Intent: $($step3Response.intent)" -ForegroundColor White
} catch {
    Write-Host "ERROR in Step 3: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Select date
Write-Host ""
Write-Host "Step 4: Selecting date..." -ForegroundColor Cyan
$dateRequest = @{
    message = $testDate
    userId = $userId
} | ConvertTo-Json

try {
    $step4Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $dateRequest
    Write-Host "SUCCESS: Date selected" -ForegroundColor Green
    Write-Host "Intent: $($step4Response.intent)" -ForegroundColor White
} catch {
    Write-Host "ERROR in Step 4: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 5: Select time slot
Write-Host ""
Write-Host "Step 5: Selecting time slot..." -ForegroundColor Cyan
$timeRequest = @{
    message = "2"
    userId = $userId
} | ConvertTo-Json

try {
    $step5Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $timeRequest
    Write-Host "SUCCESS: Time slot selected" -ForegroundColor Green
    Write-Host "Intent: $($step5Response.intent)" -ForegroundColor White
} catch {
    Write-Host "ERROR in Step 5: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 6: Provide reason for visit
Write-Host ""
Write-Host "Step 6: Providing reason for visit..." -ForegroundColor Cyan
$reasonRequest = @{
    message = "Follow-up consultation for chest pain and breathing issues"
    userId = $userId
} | ConvertTo-Json

try {
    $step6Response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $reasonRequest
    Write-Host "SUCCESS: Reason provided" -ForegroundColor Green
    Write-Host "Intent: $($step6Response.intent)" -ForegroundColor White
    Write-Host "Final Reply: $($step6Response.reply)" -ForegroundColor Gray
} catch {
    Write-Host "ERROR in Step 6: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Green

if ($step6Response.intent -eq "booking_success") {
    Write-Host "RESULT: END-TO-END BOOKING SUCCESSFUL!" -ForegroundColor Green
} else {
    Write-Host "RESULT: BOOKING INCOMPLETE" -ForegroundColor Red
}
