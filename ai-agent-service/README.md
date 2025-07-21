# AI Agent Service

This Python service provides AI-powered chat functionality using GPT4All for the appointment scheduler application.

## Features

- Natural language processing for symptom analysis
- Intent recognition (symptom checking vs appointment booking)
- Entity extraction (dates, times, symptoms, specialties)
- RESTful API endpoints for integration with Java backend

## Setup

1. **Install Python 3.8+** (if not already installed)

2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

3. **Start the service**:
   ```bash
   python ai_server.py
   ```
   Or use the batch file:
   ```bash
   start-ai-service.bat
   ```

4. **Service will be available at**: http://localhost:5000

## API Endpoints

### POST /chat
Send messages to the AI agent and receive structured responses.

**Request**:
```json
{
  "messages": [
    {"role": "user", "content": "I have chest pain"}
  ]
}
```

**Response**:
```json
{
  "reply": "Based on your symptoms, I recommend seeing a Cardiologist...",
  "intent": "symptom_check",
  "entities": {
    "specialty": "Cardiology",
    "symptoms": ["chest pain"]
  }
}
```

### GET /health
Check service status and model loading status.

## How it Works

1. **Model Loading**: Downloads and loads GPT4All model on startup (first run may take a few minutes)
2. **Prompt Engineering**: Uses structured prompts to get consistent JSON responses
3. **Fallback Logic**: If AI fails, uses rule-based keyword matching
4. **CORS Enabled**: Allows requests from React frontend and Java backend

## Supported Specialties

- Cardiology (heart, chest pain, blood pressure)
- Dermatology (skin, rashes, acne)
- Pulmonology (lungs, breathing, cough)
- Gastroenterology (stomach, digestion)
- Orthopedics (bones, joints, muscles)
- And more...

## Notes

- First startup will download the GPT4All model (~3-4GB)
- Model runs entirely offline after download
- Service includes fallback responses if AI model fails
- Logs are written to console for debugging
