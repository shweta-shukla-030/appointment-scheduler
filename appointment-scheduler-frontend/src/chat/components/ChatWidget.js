import React, { useState, useRef, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import MessageBubble from './MessageBubble';
import ChatInput from './ChatInput';
import ClarificationModal from './ClarificationModal';
import ChatService from '../services/ChatService';
import '../styles/Chat.css';

/**
 * ChatWidget Component - Main chat interface
 * Handles message flow, state management, and AI interactions
 */
const ChatWidget = ({ isOpen, onToggle }) => {
    const navigate = useNavigate();
    const [messages, setMessages] = useState([]);
    const [isTyping, setIsTyping] = useState(false);
    const [isConnected, setIsConnected] = useState(false);
    const [connectionError, setConnectionError] = useState(null);
    
    // Clarification modal state
    const [showClarificationModal, setShowClarificationModal] = useState(false);
    const [clarificationData, setClarificationData] = useState({
        questions: [],
        originalMessage: '',
        userId: 'user123' // Default user ID
    });
    
    const messagesEndRef = useRef(null);
    const chatContainerRef = useRef(null);

    // Initialize chat and check connection
    useEffect(() => {
        checkConnection();
        
        // Add welcome message
        if (messages.length === 0) {
            addWelcomeMessage();
        }
    }, []);

    // Auto-scroll to bottom when new messages arrive
    useEffect(() => {
        scrollToBottom();
    }, [messages, isTyping]);

    const checkConnection = async () => {
        try {
            const result = await ChatService.checkHealth();
            setIsConnected(result.success);
            setConnectionError(result.success ? null : result.error);
        } catch (error) {
            setIsConnected(false);
            setConnectionError('Unable to connect to AI service');
        }
    };

    const addWelcomeMessage = () => {
        const welcomeMessage = {
            id: 'welcome',
            type: 'ai',
            text: 'Hi! I\'m your AI health assistant. I can help you find the right doctor based on your symptoms or medical needs. How can I help you today?',
            timestamp: new Date(),
            intent: 'greeting'
        };
        setMessages([welcomeMessage]);
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const handleSendMessage = useCallback(async (messageText) => {
        console.log('[ChatWidget] handleSendMessage called with:', messageText);
        
        // Add user message immediately
        const userMessage = ChatService.createUserMessage(messageText);
        console.log('[ChatWidget] Adding user message:', userMessage);
        setMessages(prev => [...prev, userMessage]);
        
        // Show typing indicator
        setIsTyping(true);

        try {
            // Check if we're in a booking conversation
            const lastAIMessage = messages.filter(m => m.type === 'ai').slice(-1)[0];
            const isInBookingFlow = lastAIMessage && 
                lastAIMessage.intent && 
                lastAIMessage.intent.startsWith('booking_');

            console.log('[ChatWidget] Last AI message:', lastAIMessage);
            console.log('[ChatWidget] Is in booking flow:', isInBookingFlow);

            let response;
            if (isInBookingFlow) {
                // Use booking conversation API
                console.log('[ChatWidget] Using booking API');
                response = await ChatService.sendBookingMessage(messageText);
            } else {
                // Use regular AI chat API
                console.log('[ChatWidget] Using regular chat API');
                response = await ChatService.sendMessage(messageText);
            }
            
            console.log('[ChatWidget] API response:', response);
            
            // Simulate typing delay for better UX
            setTimeout(() => {
                setIsTyping(false);
                
                if (response.success) {
                    const aiMessage = ChatService.formatChatMessage(response.data);
                    console.log('[ChatWidget] Adding AI message:', aiMessage);
                    
                    // Check if clarification is required
                    if (aiMessage.requiresClarification && aiMessage.clarificationQuestions.length > 0) {
                        console.log('[ChatWidget] Clarification required, showing modal');
                        setClarificationData({
                            questions: aiMessage.clarificationQuestions,
                            originalMessage: messageText,
                            userId: clarificationData.userId
                        });
                        setShowClarificationModal(true);
                    }
                    
                    setMessages(prev => [...prev, aiMessage]);
                } else {
                    // Add error message
                    const errorMessage = ChatService.formatChatMessage(response.data);
                    console.log('[ChatWidget] Adding error message:', errorMessage);
                    setMessages(prev => [...prev, errorMessage]);
                }
            }, 1000);
            
        } catch (error) {
            console.error('[ChatWidget] Error in handleSendMessage:', error);
            setIsTyping(false);
            const errorMessage = {
                id: Date.now(),
                type: 'ai',
                text: 'I\'m sorry, something went wrong. Please try again.',
                timestamp: new Date(),
                intent: 'error'
            };
            setMessages(prev => [...prev, errorMessage]);
        }
    }, [messages]);

    const handleRetryConnection = () => {
        checkConnection();
    };

    const handleClearChat = () => {
        setMessages([]);
        addWelcomeMessage();
    };

    const handleBookAppointment = (doctor) => {
        // Store doctor info for booking page
        sessionStorage.setItem('selectedDoctor', JSON.stringify(doctor));
        
        // Add confirmation message
        const confirmationMessage = {
            id: Date.now(),
            type: 'ai',
            text: `Perfect! I'm taking you to book an appointment with ${doctor.name}. You can come back to chat anytime!`,
            timestamp: new Date(),
            intent: 'appointment_booking'
        };
        setMessages(prev => [...prev, confirmationMessage]);
        
        // Close chat widget and navigate
        setTimeout(() => {
            onToggle(); // Close chat widget
            navigate('/booking');
        }, 2000);
    };

    // Clarification modal handlers
    const handleClarificationComplete = (clarificationMessage) => {
        console.log('[ChatWidget] Clarification completed:', clarificationMessage);
        // Add the clarification response to chat
        setMessages(prev => [...prev, clarificationMessage]);
        setShowClarificationModal(false);
    };

    const handleClarificationClose = () => {
        console.log('[ChatWidget] Clarification modal closed');
        setShowClarificationModal(false);
    };

    const renderChatHeader = () => (
        <div className="chat-header">
            <div className="chat-title">
                <div className="ai-status">
                    <i className="fas fa-robot"></i>
                    <span>AI Health Assistant</span>
                    <div className={`status-indicator ${isConnected ? 'online' : 'offline'}`}>
                        {isConnected ? 'Online' : 'Offline'}
                    </div>
                </div>
            </div>
            
            <div className="chat-controls">
                {!isConnected && (
                    <button 
                        className="btn-retry"
                        onClick={handleRetryConnection}
                        title="Retry connection"
                    >
                        <i className="fas fa-redo"></i>
                    </button>
                )}
                
                <button 
                    className="btn-clear"
                    onClick={handleClearChat}
                    title="Clear chat"
                >
                    <i className="fas fa-trash"></i>
                </button>
                
                <button 
                    className="btn-close"
                    onClick={onToggle}
                    title="Close chat"
                >
                    <i className="fas fa-times"></i>
                </button>
            </div>
        </div>
    );

    const renderConnectionError = () => {
        if (!connectionError) return null;
        
        return (
            <div className="connection-error">
                <div className="error-content">
                    <i className="fas fa-exclamation-triangle"></i>
                    <span>Connection Error: {connectionError}</span>
                    <button onClick={handleRetryConnection} className="btn-retry-small">
                        Retry
                    </button>
                </div>
            </div>
        );
    };

    if (!isOpen) {
        return (
            <div className="chat-widget-minimized">
                <button className="chat-toggle-button" onClick={onToggle}>
                    <i className="fas fa-comments"></i>
                    <div className="chat-badge">AI</div>
                </button>
            </div>
        );
    }

    return (
        <div className="chat-widget-container">
            <div className="chat-widget">
                {renderChatHeader()}
                {renderConnectionError()}
                
                <div className="chat-messages" ref={chatContainerRef}>
                    {messages.map((message) => {
                        console.log('[ChatWidget] Rendering MessageBubble for message:', message.id, 'handleSendMessage defined:', typeof handleSendMessage);
                        const sendMessageProp = handleSendMessage || ((msg) => {
                            console.error('[ChatWidget] Fallback onSendMessage called with:', msg);
                            return Promise.resolve();
                        });
                        
                        return (
                            <MessageBubble
                                key={message.id}
                                message={message}
                                onBookAppointment={handleBookAppointment}
                                onSendMessage={sendMessageProp}
                            />
                        );
                    })}
                    
                    {isTyping && (
                        <MessageBubble
                            message={{}}
                            showTyping={true}
                        />
                    )}
                    
                    <div ref={messagesEndRef} />
                </div>
                
                <ChatInput
                    onSendMessage={handleSendMessage}
                    disabled={!isConnected || isTyping}
                />
            </div>
            
            {/* Clarification Modal */}
            <ClarificationModal
                isOpen={showClarificationModal}
                onClose={handleClarificationClose}
                clarificationQuestions={clarificationData.questions}
                originalMessage={clarificationData.originalMessage}
                userId={clarificationData.userId}
                onClarificationComplete={handleClarificationComplete}
            />
        </div>
    );
};

export default ChatWidget;
