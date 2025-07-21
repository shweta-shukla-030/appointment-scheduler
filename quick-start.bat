@echo off
echo ========================================
echo Appointment Scheduler - Quick Start
echo ========================================
echo.

REM Check if PostgreSQL is running
echo Checking PostgreSQL service...
sc query postgresql-x64-14 >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: PostgreSQL service not found or not running
    echo Please start PostgreSQL service manually
    pause
)

echo.
echo Step 1: Setting up database...
echo Please run the database setup first:
echo   1. Open PostgreSQL command line (psql)
echo   2. Run: \i "c:\Users\shwet\Downloads\appointment-scheduler\database\setup.sql"
echo.
pause

echo.
echo Step 2: Starting Backend Server...
echo Opening new window for backend...
start "Backend Server" cmd /k "cd /d appointment-scheduler-backend && mvn clean spring-boot:run"

echo Waiting for backend to start...
timeout /t 10

echo.
echo Step 3: Installing Frontend Dependencies...
cd appointment-scheduler-frontend
call npm install

echo.
echo Step 4: Starting Frontend Server...
echo Opening new window for frontend...
start "Frontend Server" cmd /k "cd /d appointment-scheduler-frontend && npm start"

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo Backend: http://localhost:8080
echo Frontend: http://localhost:3000
echo.
echo Press any key to exit...
pause >nul
