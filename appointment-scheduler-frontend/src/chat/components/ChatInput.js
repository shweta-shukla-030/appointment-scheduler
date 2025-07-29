import React, { useState, useRef } from 'react';
import '../styles/Chat.css';

/**
 * ChatInput Component - Message input field with send functionality
 * Handles user input, validation, and message sending
 */
const ChatInput = ({ onSendMessage, disabled = false }) => {
    const [message, setMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const inputRef = useRef(null);

    const handleInputChange = (e) => {
        setMessage(e.target.value);
        
        // Auto-resize textarea
        const textarea = e.target;
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage();
        }
    };

    const handleSendMessage = async () => {
        const trimmedMessage = message.trim();
        
        if (!trimmedMessage || isLoading || disabled) return;

        setIsLoading(true);
        
        try {
            await onSendMessage(trimmedMessage);
            setMessage('');
            
            // Reset textarea height
            if (inputRef.current) {
                inputRef.current.style.height = 'auto';
            }
        } catch (error) {
            console.error('Error sending message:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const getSuggestions = () => {
        return [
            "I have a headache and fever",
            "My knee hurts when I walk",
            "I need a dermatologist",
            "Chest pain and shortness of breath",
            "Book appointment with cardiologist"
        ];
    };

    const handleSuggestionClick = (suggestion) => {
        setMessage(suggestion);
        inputRef.current?.focus();
    };

    return (
        <div className="chat-input-container">
            {/* Quick suggestions */}
            {message === '' && (
                <div className="chat-suggestions">
                    <div className="suggestions-label">
                        <i className="fas fa-lightbulb"></i>
                        Try asking about:
                    </div>
                    <div className="suggestions-list">
                        {getSuggestions().map((suggestion, index) => (
                            <button
                                key={index}
                                className="suggestion-chip"
                                onClick={() => handleSuggestionClick(suggestion)}
                                disabled={disabled}
                            >
                                {suggestion}
                            </button>
                        ))}
                    </div>
                </div>
            )}

            <div className="input-wrapper">
                <textarea
                    ref={inputRef}
                    value={message}
                    onChange={handleInputChange}
                    onKeyPress={handleKeyPress}
                    placeholder={disabled ? "AI is responding..." : "Describe your symptoms or ask about doctors..."}
                    disabled={disabled || isLoading}
                    className={`message-input ${disabled ? 'disabled' : ''}`}
                    rows={1}
                />
                
                <div className="input-actions">
                    {/* Character counter */}
                    <div className="char-counter">
                        {message.length}/500
                    </div>
                    
                    {/* Send button */}
                    <button
                        onClick={handleSendMessage}
                        disabled={!message.trim() || isLoading || disabled}
                        className={`send-button ${message.trim() ? 'active' : ''}`}
                        title="Send message (Enter)"
                    >
                        {isLoading ? (
                            <i className="fas fa-spinner fa-spin"></i>
                        ) : (
                            <i className="fas fa-paper-plane"></i>
                        )}
                    </button>
                </div>
            </div>

            {/* Input hints */}
            <div className="input-hints">
                <div className="hint">
                    <i className="fas fa-info-circle"></i>
                    <span>Press Enter to send, Shift+Enter for new line</span>
                </div>
                <div className="hint">
                    <i className="fas fa-shield-alt"></i>
                    <span>Your health information is kept private</span>
                </div>
            </div>
        </div>
    );
};

export default ChatInput;
