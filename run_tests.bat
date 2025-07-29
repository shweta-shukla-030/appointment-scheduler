@echo off
echo Symptom Persistence Test Runner
echo ==============================
echo.

echo Step 1: Starting AI Service...
echo --------------------------------
cd /d "%~dp0ai-agent-service"
start "AI Service" cmd /k "python ai_server.py"

echo.
echo Step 2: Starting Backend Service...
echo -----------------------------------
cd /d "%~dp0appointment-scheduler-backend"
start "Backend Service" cmd /k "mvn spring-boot:run"

echo.
echo Step 3: Waiting for services to start...
echo ----------------------------------------
echo Please wait 30 seconds for services to initialize...
timeout /t 30 /nobreak

echo.
echo Step 4: Running Tests...
echo -----------------------
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File "test_symptom_flow.ps1"

echo.
echo Test completed! Press any key to continue...
pause
