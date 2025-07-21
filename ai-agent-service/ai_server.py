"""
AI Agent Service using GPT4All
FastAPI server that provides chat endpoints for the appointment scheduler
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
import json
import logging
from gpt4all import GPT4All
from prompts import get_prompt_for_conversation

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="AI Agent Service", version="1.0.0")

# Enable CORS for React frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Global variable to hold the model
model = None

class ChatMessage(BaseModel):
    role: str  # "user" or "assistant"
    content: str

class ChatRequest(BaseModel):
    messages: List[ChatMessage]
    user_id: Optional[str] = None

class ChatResponse(BaseModel):
    reply: str
    intent: str
    entities: Dict[str, Any]
    raw_response: Optional[str] = None

@app.on_event("startup")
async def load_model():
    """Load GPT4All model on startup"""
    global model
    try:
        logger.info("Loading GPT4All model...")
        # Download and load a smaller, faster model
        model = GPT4All("orca-mini-3b-gguf2-q4_0.gguf")
        logger.info("Model loaded successfully!")
    except Exception as e:
        logger.error(f"Failed to load model: {e}")
        # For development, we can continue without the model
        model = None

@app.get("/")
def read_root():
    """Health check endpoint"""
    return {
        "service": "AI Agent Service", 
        "status": "running",
        "model_loaded": model is not None
    }

@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """
    Main chat endpoint that processes user messages and returns structured responses
    """
    try:
        if not model:
            # Fallback response when model isn't loaded
            return get_fallback_response(request.messages[-1].content)
        
        # Generate prompt with conversation history
        prompt = get_prompt_for_conversation(request.messages)
        
        # Get response from GPT4All
        logger.info(f"Sending prompt to model: {prompt[:100]}...")
        raw_response = model.generate(
            prompt, 
            max_tokens=300, 
            temp=0.7,
            top_k=40,
            top_p=0.9,
            repeat_penalty=1.1
        )
        
        logger.info(f"Raw model response: {raw_response}")
        
        # Try to parse JSON response
        try:
            # Clean the response - sometimes models add extra text
            json_start = raw_response.find('{')
            json_end = raw_response.rfind('}') + 1
            
            if json_start >= 0 and json_end > json_start:
                json_text = raw_response[json_start:json_end]
                parsed_response = json.loads(json_text)
                
                return ChatResponse(
                    reply=parsed_response.get("reply", "I'm here to help you find doctors and book appointments."),
                    intent=parsed_response.get("intent", "general_chat"),
                    entities=parsed_response.get("entities", {}),
                    raw_response=raw_response
                )
            else:
                raise ValueError("No valid JSON found in response")
                
        except (json.JSONDecodeError, ValueError) as e:
            logger.warning(f"Failed to parse JSON response: {e}")
            # Fallback to rule-based response
            return get_fallback_response(request.messages[-1].content)
            
    except Exception as e:
        logger.error(f"Error in chat endpoint: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

def get_fallback_response(user_message: str) -> ChatResponse:
    """
    Fallback rule-based response when AI model fails
    """
    user_message_lower = user_message.lower()
    
    # Simple keyword matching for symptoms
    symptom_keywords = {
        "chest pain": "Cardiology",
        "heart": "Cardiology",
        "skin": "Dermatology",
        "rash": "Dermatology",
        "cough": "Pulmonology",
        "breathing": "Pulmonology",
        "stomach": "Gastroenterology",
        "nausea": "Gastroenterology",
        "joint pain": "Orthopedics",
        "back pain": "Orthopedics",
        "eye": "Ophthalmology",
        "vision": "Ophthalmology",
        "ear": "ENT",
        "throat": "ENT"
    }
    
    # Check for booking keywords
    booking_keywords = ["book", "appointment", "schedule", "reserve"]
    
    # Determine intent
    if any(keyword in user_message_lower for keyword in booking_keywords):
        intent = "book_appointment"
        reply = "I'll help you book an appointment. Let me process your request."
        entities = {"action": "book"}
    else:
        # Check for symptoms
        detected_specialty = None
        detected_symptoms = []
        
        for symptom, specialty in symptom_keywords.items():
            if symptom in user_message_lower:
                detected_specialty = specialty
                detected_symptoms.append(symptom)
                break
        
        if detected_specialty:
            intent = "symptom_check"
            reply = f"Based on your symptoms, I recommend seeing a {detected_specialty} specialist. Would you like me to help you book an appointment?"
            entities = {"specialty": detected_specialty, "symptoms": detected_symptoms}
        else:
            intent = "general_chat"
            reply = "I'm here to help you find doctors based on your symptoms or book appointments. You can tell me about your symptoms or ask to book with a specific type of doctor."
            entities = {}
    
    return ChatResponse(
        reply=reply,
        intent=intent,
        entities=entities,
        raw_response="fallback_response"
    )

@app.get("/health")
def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "model_status": "loaded" if model else "not_loaded",
        "service": "ai-agent-service"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000)
