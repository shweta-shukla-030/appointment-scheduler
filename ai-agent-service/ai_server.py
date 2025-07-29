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
from prompts import get_prompt_for_conversation, check_static_mapping, create_static_response

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

def has_clear_symptoms(user_message):
    """Check if message contains clear symptom descriptions that should not require clarification"""
    if not user_message:
        return False
    
    user_message_lower = user_message.lower()
    
    # Clear symptom phrases that indicate specific medical issues
    clear_symptom_phrases = [
        "dizzy and nauseous", "nauseous after eating", "dizzy after eating",
        "headaches and tired", "headaches and feeling tired", 
        "joint stiffness", "knees hurt", "joint pain",
        "chest tightness", "shortness of breath",
        "stomach cramps", "abdominal pain",
        "vision problems", "blurry vision",
        "sore throat", "ear ache",
        "skin rash", "itchy skin",
        "trouble sleeping", "can't sleep",
        "feeling anxious", "feeling depressed"
    ]
    
    # Check if any clear symptom phrase is present
    for phrase in clear_symptom_phrases:
        if phrase in user_message_lower:
            return True
    
    # Check for symptom + duration patterns (e.g., "for 3 days", "since yesterday")
    duration_indicators = ["for", "since", "days", "weeks", "hours"]
    has_symptom_words = any(word in user_message_lower for word in ["pain", "hurt", "ache", "feeling", "tired", "dizzy", "nauseous"])
    has_duration = any(word in user_message_lower for word in duration_indicators)
    
    return has_symptom_words and has_duration

def is_confident_response(response_data):
    """Check if GPT4All response has high confidence"""
    try:
        if isinstance(response_data, dict) and "entities" in response_data:
            confidence = response_data["entities"].get("confidence", 0.0)
            requires_clarification = response_data["entities"].get("requires_clarification", False)
            specialty = response_data["entities"].get("specialty", None)
            
            logger.info(f"Confidence check - Confidence: {confidence}, Has Specialty: {specialty is not None}, Requires Clarification: {requires_clarification}")
            
            # If we have a specialty and confidence > 0.6, that's good enough
            # Lower threshold for post-clarification responses
            if specialty and confidence >= 0.6:
                logger.info(f"Confidence check PASSED - Specialty: {specialty}, Confidence: {confidence}")
                return True
                
            # Original threshold for high confidence
            result = confidence >= 0.7 and not requires_clarification
            if result:
                logger.info(f"Confidence check PASSED - High confidence: {confidence}")
            else:
                logger.info(f"Confidence check FAILED - Confidence: {confidence}, Requires Clarification: {requires_clarification}")
            return result
        return False
    except Exception as e:
        logger.error(f"Error in confidence check: {e}")
        return False

def create_clarification_response(user_message):
    """Create a clarification response when symptoms are unclear"""
    return ChatResponse(
        reply="I'd like to help you find the right doctor. Could you provide more specific details about your symptoms? For example, where do you feel discomfort and what type of feeling is it?",
        intent="clarification",
        entities={
            "requires_clarification": True,
            "fallback_level": "clarification",
            "clarification_questions": [
                "Where in your body do you feel the symptoms?",
                "What type of discomfort is it (pain, aching, burning, etc.)?",
                "When did the symptoms start?",
                "What makes the symptoms better or worse?"
            ]
        }
    )

def get_gpt_analysis(request):
    """Get analysis from GPT4All model"""
    try:
        if not model:
            return None
        
        # Generate prompt with conversation history
        prompt = get_prompt_for_conversation(request.messages, "dynamic")
        
        # Get response from GPT4All
        logger.info(f"Sending prompt to GPT4All for dynamic analysis")
        raw_response = model.generate(
            prompt, 
            max_tokens=200,  # Reduced for more focused responses
            temp=0.3,        # Lower temperature for more consistent output
            top_k=20,        # More focused token selection
            top_p=0.8,       # Slightly more focused
            repeat_penalty=1.1
        )
        
        logger.info(f"GPT4All raw response: {raw_response}")
        
        # Clean and extract JSON more aggressively
        cleaned_response = raw_response.strip()
        
        # Try multiple JSON extraction methods
        json_response = None
        
        # Method 1: Look for complete JSON blocks
        json_start = cleaned_response.find('{')
        json_end = cleaned_response.rfind('}') + 1
        
        if json_start >= 0 and json_end > json_start:
            json_str = cleaned_response[json_start:json_end]
            try:
                json_response = json.loads(json_str)
                logger.info(f"Successfully parsed JSON: {json_response}")
            except json.JSONDecodeError as e:
                logger.error(f"JSON parse error: {e}")
                logger.info(f"Attempted to parse: {json_str}")
        
        # Method 2: If no JSON found, create a fallback response
        if not json_response:
            logger.warning("No valid JSON found, creating fallback response")
            # Try to extract any useful information from the raw response
            if "gastro" in cleaned_response.lower() or "stomach" in cleaned_response.lower() or "nausea" in cleaned_response.lower():
                specialty = "Gastroenterology"
            elif "cardio" in cleaned_response.lower() or "heart" in cleaned_response.lower():
                specialty = "Cardiology"
            elif "ortho" in cleaned_response.lower() or "joint" in cleaned_response.lower():
                specialty = "Orthopedics"
            else:
                specialty = "General Medicine"
            
            json_response = {
                "reply": f"Based on your symptoms, I recommend seeing a {specialty} specialist.",
                "intent": "symptom_check",
                "entities": {
                    "specialty": specialty,
                    "symptoms": ["unclear symptoms"],
                    "confidence": 0.6
                }
            }
        
        # Log detailed response analysis for both successful and fallback responses
        confidence = json_response.get("entities", {}).get("confidence", 0.0)
        specialty = json_response.get("entities", {}).get("specialty", "None")
        requires_clarification = json_response.get("entities", {}).get("requires_clarification", False)
        
        logger.info(f"GPT4All parsed response - Confidence: {confidence}, Specialty: {specialty}, Requires Clarification: {requires_clarification}")
        
        # Add fallback level info
        if "entities" not in json_response:
            json_response["entities"] = {}
        json_response["entities"]["fallback_level"] = "dynamic"
        
        return ChatResponse(
            reply=json_response.get("reply", "I understand your concern. Let me help you find the right specialist."),
            intent=json_response.get("intent", "symptom_check"),
            entities=json_response.get("entities", {}),
            raw_response=raw_response
        )
            
    except Exception as e:
        logger.error(f"Error in GPT analysis: {e}")
        return None

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
    Main chat endpoint with 3-tier fallback mechanism:
    1. Static mapping check
    2. GPT4All dynamic analysis  
    3. Clarification questions
    """
    try:
        if not request.messages or len(request.messages) == 0:
            raise HTTPException(status_code=400, detail="No messages provided")
        
        user_message = request.messages[-1].content
        logger.info(f"Processing user message: {user_message}")
        
        # LEVEL 1: Check static mapping first
        logger.info("Level 1: Checking static symptom mapping...")
        static_specialty = check_static_mapping(user_message)
        
        if static_specialty:
            logger.info(f"Static mapping found: {static_specialty}")
            static_response = create_static_response(static_specialty, user_message)
            return ChatResponse(
                reply=static_response["reply"],
                intent=static_response["intent"],
                entities=static_response["entities"]
            )
        
        # LEVEL 2: Use GPT4All for dynamic analysis
        logger.info("Level 2: Using GPT4All for dynamic analysis...")
        gpt_response = get_gpt_analysis(request)
        
        if gpt_response and is_confident_response({"entities": gpt_response.entities}):
            logger.info("GPT4All provided confident response")
            return gpt_response
        elif gpt_response:
            # Log why the response was not confident enough
            confidence = gpt_response.entities.get("confidence", 0.0)
            requires_clarification = gpt_response.entities.get("requires_clarification", False)
            specialty = gpt_response.entities.get("specialty", "None")
            logger.info(f"GPT4All response not confident enough - Confidence: {confidence}, Specialty: {specialty}, Requires Clarification: {requires_clarification}")
        
        # LEVEL 2.5: Check for clear post-clarification symptoms
        # If message contains clear symptom descriptions, override low confidence
        if gpt_response and has_clear_symptoms(user_message):
            logger.info("Clear symptoms detected, using GPT response despite low confidence")
            logger.info(f"Original confidence: {gpt_response.entities.get('confidence', 0.0)} -> Boosted to 0.8")
            # Force confidence and remove clarification requirement
            gpt_response.entities["confidence"] = 0.8
            gpt_response.entities["requires_clarification"] = False
            return gpt_response
        
        # LEVEL 3: Ask for clarification
        logger.info("Level 3: Asking for clarification")
        return create_clarification_response(user_message)
        
    except json.JSONDecodeError as e:
        logger.error(f"JSON parsing error: {e}")
        return get_fallback_response(user_message)
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
