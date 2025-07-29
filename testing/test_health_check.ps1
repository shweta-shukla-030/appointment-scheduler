# Quick Health Check Test
# Verifies that all endpoints are responding correctly

Write-Host "=== API Health Check ===" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"
$userId = "john_doe"

Write-Host "Checking backend health..." -ForegroundColor Cyan

# Test 1: AI Chat endpoint
try {
    $chatTest = @{
        message = "Hello"
        userId = $userId
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $chatTest
    Write-Host "✓ AI Chat endpoint working" -ForegroundColor Green
} catch {
    Write-Host "✗ AI Chat endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: AI Health endpoint
try {
    $healthResponse = Invoke-RestMethod -Uri "$baseUrl/api/ai/health" -Method GET
    Write-Host "✓ AI Health endpoint working" -ForegroundColor Green
} catch {
    Write-Host "✗ AI Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Booking Health endpoint
try {
    Invoke-RestMethod -Uri "$baseUrl/api/booking/health" -Method GET | Out-Null
    Write-Host "✓ Booking Health endpoint working" -ForegroundColor Green
} catch {
    Write-Host "✗ Booking Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Health Check Complete ===" -ForegroundColor Green
