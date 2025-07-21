@echo off
echo Starting AI Agent Service...
echo.
echo Installing Python dependencies...
pip install -r requirements.txt

echo.
echo Starting FastAPI server on http://localhost:5000
echo Press Ctrl+C to stop the service
echo.
python ai_server.py
