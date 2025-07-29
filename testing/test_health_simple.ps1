# Quick Health Check Test
Write-Host "=== API Health Check ===" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"
$userId = "john_doe"

Write-Host "Checking backend health..." -ForegroundColor Cyan

# Test AI Chat endpoint
try {
    $chatTest = @{
        message = "Hello"
        userId = $userId
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "$baseUrl/api/ai/chat" -Method POST -ContentType "application/json" -Body $chatTest | Out-Null
    Write-Host "✓ AI Chat endpoint working" -ForegroundColor Green
} catch {
    Write-Host "✗ AI Chat endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test AI Health endpoint
try {
    Invoke-RestMethod -Uri "$baseUrl/api/ai/health" -Method GET | Out-Null
    Write-Host "✓ AI Health endpoint working" -ForegroundColor Green
} catch {
    Write-Host "✗ AI Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Booking Health endpoint
try {
    Invoke-RestMethod -Uri "$baseUrl/api/booking/health" -Method GET | Out-Null
    Write-Host "✓ Booking Health endpoint working" -ForegroundColor Green
} catch {
    Write-Host "✗ Booking Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Health Check Complete ===" -ForegroundColor Green
