import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MessageBubble from '../components/MessageBubble';
import ChatInput from '../components/ChatInput';
import ChatService from '../services/ChatService';
import '../styles/Chat.css';

/**
 * ChatPage Component - Full-page chat interface
 * Dedicated page for AI health assistant conversations
 */
const ChatPage = ({ user, onLogout }) => {
    const navigate = useNavigate();
    console.log('ChatPage loaded with user:', user);
    const [messages, setMessages] = useState([]);
    const [isTyping, setIsTyping] = useState(false);
    const [isConnected, setIsConnected] = useState(false);
    const [connectionError, setConnectionError] = useState(null);
    const [sessionStats, setSessionStats] = useState({
        messagesCount: 0,
        doctorsRecommended: 0,
        sessionStart: null
    });

    useEffect(() => {
        const initializeChat = async () => {
            // Check connection
            await checkConnection();
            
            // Set session start time
            setSessionStats(prev => ({
                ...prev,
                sessionStart: new Date()
            }));
            
            // Add welcome message
            addWelcomeMessage();
        };

        initializeChat();
    }, []);

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
            text: 'Welcome to your AI Health Assistant! I\'m here to help you find the right healthcare providers based on your symptoms and medical needs.\n\nYou can ask me about:\n• Symptom analysis and doctor recommendations\n• Finding specialists by medical field\n• Booking appointments\n• General health questions\n\nHow can I assist you today?',
            timestamp: new Date(),
            intent: 'greeting'
        };
        setMessages([welcomeMessage]);
    };

    const handleSendMessage = async (messageText) => {
        // Add user message immediately
        const userMessage = ChatService.createUserMessage(messageText);
        setMessages(prev => [...prev, userMessage]);
        
        // Update stats
        setSessionStats(prev => ({
            ...prev,
            messagesCount: prev.messagesCount + 1
        }));
        
        // Show typing indicator
        setIsTyping(true);

        try {
            // Send message to AI
            const response = await ChatService.sendMessage(messageText);
            
            // Simulate realistic typing delay
            const typingDelay = Math.min(messageText.length * 50, 2000);
            
            setTimeout(() => {
                setIsTyping(false);
                
                if (response.success) {
                    const aiMessage = ChatService.formatChatMessage(response.data);
                    setMessages(prev => [...prev, aiMessage]);
                    
                    // Update doctor recommendations count
                    if (aiMessage.doctors && aiMessage.doctors.length > 0) {
                        setSessionStats(prev => ({
                            ...prev,
                            doctorsRecommended: prev.doctorsRecommended + aiMessage.doctors.length
                        }));
                    }
                } else {
                    // Add error message
                    const errorMessage = ChatService.formatChatMessage(response.data);
                    setMessages(prev => [...prev, errorMessage]);
                }
            }, typingDelay);
            
        } catch (error) {
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
    };

    const handleRetryConnection = () => {
        checkConnection();
    };

    const handleClearChat = () => {
        setMessages([]);
        setSessionStats({
            messagesCount: 0,
            doctorsRecommended: 0,
            sessionStart: new Date()
        });
        addWelcomeMessage();
    };

    const getSessionDuration = () => {
        if (!sessionStats.sessionStart) return '0m';
        
        const duration = Date.now() - sessionStats.sessionStart.getTime();
        const minutes = Math.floor(duration / 60000);
        
        if (minutes < 1) return '<1m';
        if (minutes < 60) return `${minutes}m`;
        
        const hours = Math.floor(minutes / 60);
        const remainingMinutes = minutes % 60;
        return `${hours}h ${remainingMinutes}m`;
    };

    const renderPageHeader = () => (
        <div className="chat-page-header">
            <div className="header-content">
                <div className="header-left">
                    <div className="page-title">
                        <i className="fas fa-robot"></i>
                        <h1>AI Health Assistant</h1>
                    </div>
                    <div className="page-subtitle">
                        Get personalized doctor recommendations based on your symptoms
                    </div>
                </div>
                
                <div className="header-right">
                    <div className="session-info">
                        <div className="stat-item">
                            <i className="fas fa-clock"></i>
                            <span>{getSessionDuration()}</span>
                        </div>
                        <div className="stat-item">
                            <i className="fas fa-comments"></i>
                            <span>{sessionStats.messagesCount} messages</span>
                        </div>
                        <div className="stat-item">
                            <i className="fas fa-user-md"></i>
                            <span>{sessionStats.doctorsRecommended} doctors recommended</span>
                        </div>
                    </div>
                    
                    <div className="header-actions">
                        <div className={`connection-status ${isConnected ? 'connected' : 'disconnected'}`}>
                            <i className={`fas ${isConnected ? 'fa-circle' : 'fa-circle'}`}></i>
                            <span>{isConnected ? 'Connected' : 'Disconnected'}</span>
                        </div>
                        
                        <button 
                            className="btn-clear-chat"
                            onClick={handleClearChat}
                            title="Clear conversation"
                        >
                            <i className="fas fa-trash"></i>
                            Clear
                        </button>
                    </div>
                </div>
            </div>
            
            {connectionError && (
                <div className="connection-error-banner">
                    <i className="fas fa-exclamation-triangle"></i>
                    <span>Connection Error: {connectionError}</span>
                    <button onClick={handleRetryConnection} className="btn-retry">
                        <i className="fas fa-redo"></i>
                        Retry
                    </button>
                </div>
            )}
        </div>
    );

    const renderQuickActions = () => (
        <div className="quick-actions">
            <div className="actions-title">Quick Actions:</div>
            <div className="actions-list">
                <button 
                    className="action-btn"
                    onClick={() => handleSendMessage("I have a headache and fever")}
                    disabled={!isConnected || isTyping}
                >
                    <i className="fas fa-thermometer-half"></i>
                    Fever & Headache
                </button>
                <button 
                    className="action-btn"
                    onClick={() => handleSendMessage("I need a cardiologist")}
                    disabled={!isConnected || isTyping}
                >
                    <i className="fas fa-heartbeat"></i>
                    Find Cardiologist
                </button>
                <button 
                    className="action-btn"
                    onClick={() => handleSendMessage("I want to book an appointment for my headache and fever")}
                    disabled={!isConnected || isTyping}
                >
                    <i className="fas fa-calendar"></i>
                    Book Appointment
                </button>
                <button 
                    className="action-btn"
                    onClick={() => handleSendMessage("What are the symptoms of diabetes?")}
                    disabled={!isConnected || isTyping}
                >
                    <i className="fas fa-question-circle"></i>
                    Health Question
                </button>
            </div>
        </div>
    );

    return (
        <div className="chat-page">
            {renderPageHeader()}
            
            <div className="chat-page-content">
                <div className="chat-container">
                    <div className="messages-container">
                        {messages.length === 1 && messages[0].id === 'welcome' && renderQuickActions()}
                        
                        {messages.map((message) => (
                            <MessageBubble
                                key={message.id}
                                message={message}
                            />
                        ))}
                        
                        {isTyping && (
                            <MessageBubble
                                message={{}}
                                showTyping={true}
                            />
                        )}
                    </div>
                    
                    <div className="chat-input-section">
                        <ChatInput
                            onSendMessage={handleSendMessage}
                            disabled={!isConnected || isTyping}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ChatPage;
