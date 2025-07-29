"""
Prompt templates for the AI agent
"""

# Static symptom to specialty mapping for fast lookup
STATIC_SYMPTOMS_MAPPING = {
    "chest pain": "Cardiology",
    "heart problems": "Cardiology",
    "heart pain": "Cardiology", 
    "high blood pressure": "Cardiology",
    "shortness of breath": "Cardiology",
    "skin issues": "Dermatology",
    "rashes": "Dermatology",
    "acne": "Dermatology",
    "moles": "Dermatology",
    "skin rash": "Dermatology",
    "cough": "Pulmonology",
    "breathing issues": "Pulmonology",
    "asthma": "Pulmonology",
    "lung problems": "Pulmonology",
    "breathing problems": "Pulmonology",
    "stomach pain": "Gastroenterology",
    "digestion": "Gastroenterology",
    "nausea": "Gastroenterology",
    "stomach issues": "Gastroenterology",
    "joint pain": "Orthopedics",
    "muscle pain": "Orthopedics",
    "arthritis": "Orthopedics",
    "back pain": "Orthopedics",
    "mental health": "Psychiatry",
    "anxiety": "Psychiatry",
    "depression": "Psychiatry",
    "stress": "Psychiatry",
    "eye problems": "Ophthalmology",
    "vision": "Ophthalmology",
    "eye pain": "Ophthalmology",
    "ear": "ENT",
    "nose": "ENT",
    "throat": "ENT",
    "ear pain": "ENT",
    "throat pain": "ENT",
    "women's health": "Gynecology",
    "pregnancy": "Gynecology",
    "general checkup": "General Medicine",
    "fever": "General Medicine",
    "common cold": "General Medicine",
    "cold": "General Medicine"
}

def check_static_mapping(user_input):
    """
    Check if user input matches any symptom in the static mapping
    Returns specialty if found, None otherwise
    """
    if not user_input:
        return None
    
    user_input_lower = user_input.lower()
    
    # Sort symptoms by length (longest first) to prioritize more specific matches
    sorted_symptoms = sorted(STATIC_SYMPTOMS_MAPPING.items(), key=lambda x: len(x[0]), reverse=True)
    
    # Check for matches, prioritizing longer/more specific symptoms
    for symptom, specialty in sorted_symptoms:
        # For single words like "ear", "nose", check word boundaries to avoid partial matches
        if len(symptom.split()) == 1 and len(symptom) <= 4:
            # Use word boundary check for short single words
            import re
            pattern = r'\b' + re.escape(symptom) + r'\b'
            if re.search(pattern, user_input_lower):
                return specialty
        else:
            # For multi-word phrases or longer terms, use substring matching
            if symptom in user_input_lower:
                return specialty
    
    return None

# Prompt for dynamic analysis when static mapping fails
DYNAMIC_ANALYSIS_PROMPT = """You are a medical assistant. Analyze the patient's symptoms and respond ONLY in valid JSON format.

REQUIRED JSON FORMAT:
{
  "reply": "Your response to the patient",
  "intent": "symptom_check", 
  "entities": {
    "specialty": "SPECIALTY_NAME",
    "symptoms": ["list of symptoms"],
    "confidence": 0.8
  }
}

SPECIALTIES (choose one):
Cardiology, Dermatology, Pulmonology, Gastroenterology, Orthopedics, Psychiatry, Ophthalmology, ENT, Gynecology, General Medicine

STRICT CONFIDENCE RULES:
- Use 0.8-1.0 ONLY for specific, clear symptoms (e.g., "chest pain", "stomach pain after eating")
- Use 0.4-0.6 for vague or unclear statements (e.g., "not feeling good", "something is wrong")
- If confidence < 0.7, you MUST add "requires_clarification": true

CLEAR SYMPTOM EXAMPLES (High Confidence 0.8-1.0):
Patient: "chest pain" → {"reply": "I recommend seeing a Cardiology specialist for your chest pain.", "intent": "symptom_check", "entities": {"specialty": "Cardiology", "symptoms": ["chest pain"], "confidence": 0.9}}

Patient: "stomach pain after eating" → {"reply": "Based on your digestive symptoms, I recommend a Gastroenterology specialist.", "intent": "symptom_check", "entities": {"specialty": "Gastroenterology", "symptoms": ["stomach pain", "digestive issues"], "confidence": 0.8}}

VAGUE INPUT EXAMPLES (Low Confidence 0.4-0.6):
Patient: "not feeling good" → {"reply": "I understand you're not feeling well. Can you describe specific symptoms you're experiencing?", "intent": "symptom_check", "entities": {"symptoms": ["general discomfort"], "confidence": 0.4, "requires_clarification": true}}

Patient: "something is wrong" → {"reply": "I'd like to help you find the right specialist. What specific symptoms are you experiencing?", "intent": "symptom_check", "entities": {"symptoms": ["unclear"], "confidence": 0.3, "requires_clarification": true}}

IMPORTANT: Never assign high confidence (>0.7) to vague statements like "not feeling good", "something is wrong", "feel weird", etc.

Respond with ONLY valid JSON, no other text.
"""

# Prompt for asking clarification questions
CLARIFICATION_PROMPT = """You are a medical appointment assistant. The user has provided symptoms that are unclear or ambiguous. 
Your job is to ask specific follow-up questions to clarify the symptoms so you can recommend the right medical specialty.

You must respond in JSON format with these fields:
- "reply": Your conversational response asking for clarification
- "intent": "clarification"
- "entities": {"requires_clarification": true, "clarification_questions": [list of specific questions]}

Ask specific, helpful questions about:
- Location of symptoms
- Type of pain/discomfort (sharp, dull, burning, etc.)
- When symptoms occur (after eating, during activity, etc.)
- Duration and severity
- Associated symptoms

Keep questions simple and easy to understand.

Example:
User: "I feel weird"
Response: {"reply": "I'd like to help you find the right doctor. Could you tell me more about how you're feeling? For example, where in your body do you feel weird, and what kind of feeling is it?", "intent": "clarification", "entities": {"requires_clarification": true, "clarification_questions": ["Where in your body do you feel weird?", "What kind of feeling is it (pain, discomfort, nausea, etc.)?", "When did this start?"]}}
"""

# Legacy system prompt (kept for backward compatibility)
SYSTEM_PROMPT = DYNAMIC_ANALYSIS_PROMPT

def get_prompt_for_conversation(conversation_history, prompt_type="dynamic"):
    """
    Generate full prompt including conversation history
    prompt_type can be: "dynamic", "clarification"
    """
    messages = ""
    for msg in conversation_history:
        role = msg.role if hasattr(msg, 'role') else "user"
        content = msg.content if hasattr(msg, 'content') else ""
        messages += f"{role.capitalize()}: {content}\n"
    
    # Choose the appropriate prompt based on type
    if prompt_type == "clarification":
        base_prompt = CLARIFICATION_PROMPT
    else:
        base_prompt = DYNAMIC_ANALYSIS_PROMPT
    
    return f"""{base_prompt}

Conversation so far:
{messages}

Respond in valid JSON format only."""

def create_static_response(specialty, user_message, symptoms=None):
    """
    Create a response for static mapping matches
    """
    if symptoms is None:
        symptoms = []
    
    reply = f"Based on your symptoms, I recommend seeing a {specialty} specialist. "
    reply += f"These symptoms are commonly treated by {specialty} doctors. "
    reply += "Would you like me to help you find available doctors and book an appointment?"
    
    return {
        "reply": reply,
        "intent": "symptom_check",
        "entities": {
            "specialty": specialty,
            "symptoms": symptoms,
            "confidence": 1.0,
            "fallback_level": "static",
            "requires_clarification": False
        }
    }
