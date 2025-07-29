# Manual Testing Guide: Symptom Persistence Flow

## Quick Test Steps (Manual)

### Prerequisites
1. Start AI Service: `cd ai-agent-service && python ai_server.py`
2. Start Backend: `cd appointment-scheduler-backend && mvn spring-boot:run`
3. Wait for both services to be ready

### Test Scenario: Symptom Persistence

#### Step 1: Health Check
```bash
# Test AI Service
curl http://localhost:5000/health

# Test Backend
curl http://localhost:8080/api/health
```

#### Step 2: Symptom Analysis
```bash
curl -X POST http://localhost:8080/api/ai-agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "I have headache and fever",
    "userId": "test123"
  }'
```

**Expected Result:** Should return doctor recommendations and suggest booking.

#### Step 3: Booking Request (THE CRITICAL TEST)
```bash
curl -X POST http://localhost:8080/api/ai-agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "book appointment", 
    "userId": "test123"
  }'
```

**Expected Result:** 
- ✅ **SUCCESS**: `intent: "booking_location_selection"` - means symptoms were preserved
- ❌ **FAILURE**: `intent: "booking_start"` - means symptoms were lost

#### Step 4: Location Selection (if Step 3 succeeded)
```bash
curl -X POST http://localhost:8080/api/booking/conversation \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Mumbai",
    "userId": "test123"
  }'
```

## Automated Test Scripts

### Option 1: PowerShell (Recommended for Windows)
```powershell
# Run the comprehensive test
./test_symptom_flow.ps1
```

### Option 2: Python (Cross-platform)
```bash
# Run the detailed test suite
python test_symptom_flow.py
```

### Option 3: Full Automated (Windows)
```bash
# Starts services and runs tests automatically
./run_tests.bat
```

## Expected Results

### 🎯 Success Indicators
- AI Service returns `intent: "symptom_check"` for symptoms
- Backend stores symptom context in memory
- Booking request returns `intent: "booking_location_selection"`
- User skips symptom re-entry and goes directly to location selection

### ❌ Failure Indicators
- Booking request returns `intent: "booking_start"`
- User is asked to re-enter symptoms
- No location selection presented

## Troubleshooting

### Common Issues
1. **Services not starting**: Check ports 5000 and 8080 are available
2. **Database errors**: Ensure PostgreSQL is running
3. **Compilation errors**: Run `mvn clean compile` in backend
4. **Python errors**: Install requirements `pip install -r requirements.txt`

### Debug Commands
```bash
# Check service logs
tail -f ai-agent-service/app.log
tail -f appointment-scheduler-backend/target/logs/app.log

# Test individual endpoints
curl http://localhost:5000/  # AI service root
curl http://localhost:8080/  # Backend root
```

## Test Results Interpretation

| Intent Response | Meaning | Status |
|----------------|---------|---------|
| `booking_location_selection` | Symptoms preserved, location step | ✅ SUCCESS |
| `booking_start` | Fresh booking, no symptom context | ❌ NEEDS FIX |
| `general_chat` | Not recognized as booking | ❌ NEEDS FIX |

## Next Steps After Testing

### If Tests Pass ✅
- Move to frontend integration testing
- Test with real user scenarios
- Performance testing with multiple users

### If Tests Fail ❌
- Check service logs for errors
- Verify database connectivity
- Debug symptom context storage
- Check API endpoint mappings
