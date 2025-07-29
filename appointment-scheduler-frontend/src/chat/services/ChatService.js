/**
 * Chat Service - Handles all AI chat API communications
 * Connects React frontend to Java backend AI endpoints
 */

const API_BASE_URL = 'http://localhost:8080/api/ai';
const BOOKING_API_BASE_URL = 'http://localhost:8080/api/booking';

class ChatService {
    /**
     * Send a chat message to the AI agent
     * @param {string} message - User's message
     * @param {string} userId - User identifier (optional)
     * @returns {Promise<Object>} AI response with recommendations
     */
    static async sendMessage(message, userId = '1') {
        try {
            const response = await fetch(`${API_BASE_URL}/chat`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    message: message,
                    userId: userId
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return {
                success: true,
                data: data
            };

        } catch (error) {
            console.error('Error sending chat message:', error);
            return {
                success: false,
                error: error.message,
                data: {
                    reply: "I'm sorry, I'm having trouble connecting right now. Please try again later.",
                    intent: "error",
                    success: false
                }
            };
        }
    }

    /**
     * Send clarification response to AI agent
     * @param {string} userId - User identifier
     * @param {string} originalMessage - Original user message that needed clarification
     * @param {string} clarificationResponse - User's response to clarification questions
     * @returns {Promise<Object>} AI response with enhanced analysis
     */
    static async sendClarification(userId, originalMessage, clarificationResponse) {
        try {
            const response = await fetch(`${BOOKING_API_BASE_URL}/clarification`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: userId,
                    originalMessage: originalMessage,
                    clarificationResponse: clarificationResponse
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return {
                success: true,
                data: data
            };

        } catch (error) {
            console.error('Error sending clarification:', error);
            return {
                success: false,
                error: error.message,
                data: {
                    reply: "I'm sorry, I had trouble processing your clarification. Please try again.",
                    intent: "error",
                    success: false,
                    requiresFurtherClarification: false
                }
            };
        }
    }

    /**
     * Get doctor recommendations based on symptoms
     * @param {string} symptoms - Patient's symptoms description
     * @param {string} userId - User identifier (optional)
     * @returns {Promise<Object>} Doctor recommendations
     */
    static async getRecommendations(symptoms, userId = '1') {
        try {
            const response = await fetch(`${API_BASE_URL}/recommend`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    symptoms: symptoms,
                    userId: userId
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return {
                success: true,
                data: data
            };

        } catch (error) {
            console.error('Error getting recommendations:', error);
            return {
                success: false,
                error: error.message,
                data: {
                    message: "Unable to get doctor recommendations at this time.",
                    doctors: []
                }
            };
        }
    }

    /**
     * Get doctors by specialty
     * @param {string} specialty - Medical specialty
     * @returns {Promise<Object>} List of doctors
     */
    static async getDoctorsBySpecialty(specialty) {
        try {
            const response = await fetch(`${API_BASE_URL}/doctors/${encodeURIComponent(specialty)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const doctors = await response.json();
            return {
                success: true,
                data: doctors
            };

        } catch (error) {
            console.error('Error fetching doctors:', error);
            return {
                success: false,
                error: error.message,
                data: []
            };
        }
    }

    /**
     * Send booking conversation message
     * @param {string} message - User's booking response
     * @param {string} userId - User identifier (optional)
     * @returns {Promise<Object>} Booking conversation response
     */
    static async sendBookingMessage(message, userId = '1') {
        try {
            const response = await fetch('http://localhost:8080/api/booking/conversation', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    message: message,
                    userId: userId
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return {
                success: true,
                data: data
            };

        } catch (error) {
            console.error('Error sending booking message:', error);
            return {
                success: false,
                error: error.message,
                data: {
                    reply: "I'm sorry, I'm having trouble with the booking process right now. Please try again later.",
                    intent: "booking_error",
                    success: false
                }
            };
        }
    }

    /**
     * Check AI service health
     * @returns {Promise<Object>} Health status
     */
    static async checkHealth() {
        try {
            const response = await fetch(`${API_BASE_URL}/health`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return {
                success: true,
                data: data
            };

        } catch (error) {
            console.error('Error checking health:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Format chat message for display
     * @param {Object} apiResponse - Response from AI API
     * @returns {Object} Formatted message for chat UI
     */
    static formatChatMessage(apiResponse) {
        return {
            id: Date.now(),
            type: 'ai',
            text: apiResponse.reply,
            timestamp: new Date(),
            intent: apiResponse.intent,
            doctors: apiResponse.recommendedDoctors || [],
            success: apiResponse.success,
            // New clarification fields
            requiresClarification: apiResponse.requiresClarification || false,
            clarificationQuestions: apiResponse.clarificationQuestions || [],
            confidenceLevel: apiResponse.confidenceLevel || 'medium',
            fallbackLevel: apiResponse.fallbackLevel || 'unknown'
        };
    }

    /**
     * Format clarification response for display
     * @param {Object} clarificationResponse - Response from clarification API
     * @returns {Object} Formatted message for chat UI
     */
    static formatClarificationMessage(clarificationResponse) {
        return {
            id: Date.now(),
            type: 'ai',
            text: clarificationResponse.reply,
            timestamp: new Date(),
            intent: clarificationResponse.intent,
            doctors: clarificationResponse.recommendedDoctors || [],
            success: clarificationResponse.success,
            requiresFurtherClarification: clarificationResponse.requiresFurtherClarification || false,
            additionalQuestions: clarificationResponse.additionalQuestions || [],
            confidenceLevel: clarificationResponse.confidenceLevel || 'medium',
            fallbackLevel: clarificationResponse.fallbackLevel || 'clarification',
            processedMessage: clarificationResponse.processedMessage
        };
    }

    /**
     * Create user message object
     * @param {string} text - User's message text
     * @returns {Object} Formatted user message
     */
    static createUserMessage(text) {
        return {
            id: Date.now(),
            type: 'user',
            text: text,
            timestamp: new Date()
        };
    }
}

export default ChatService;
