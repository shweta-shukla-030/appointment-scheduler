import React, { useState } from 'react';
import ChatService from '../services/ChatService';
import './ClarificationModal.css';

/**
 * Modal component for handling clarification questions from AI
 * Displays questions and collects user responses for better symptom analysis
 */
const ClarificationModal = ({ 
    isOpen, 
    onClose, 
    clarificationQuestions = [], 
    originalMessage,
    userId,
    onClarificationComplete 
}) => {
    const [response, setResponse] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    // Don't render if modal is not open
    if (!isOpen) return null;

    /**
     * Handle form submission
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Validate response
        if (!response.trim()) {
            setError('Please provide more details about your symptoms.');
            return;
        }

        if (response.trim().length < 5) {
            setError('Please provide more detailed information (at least 5 characters).');
            return;
        }

        setIsSubmitting(true);
        setError('');

        try {
            // Send clarification to backend
            const result = await ChatService.sendClarification(
                userId, 
                originalMessage, 
                response.trim()
            );

            if (result.success) {
                // Format the response for display
                const formattedMessage = ChatService.formatClarificationMessage(result.data);
                
                // Call the completion handler
                onClarificationComplete(formattedMessage);
                
                // Close modal and reset state
                handleClose();
            } else {
                setError(result.error || 'Failed to process clarification. Please try again.');
            }
        } catch (err) {
            console.error('Error processing clarification:', err);
            setError('An unexpected error occurred. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    /**
     * Handle modal close
     */
    const handleClose = () => {
        setResponse('');
        setError('');
        setIsSubmitting(false);
        onClose();
    };

    /**
     * Handle backdrop click
     */
    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) {
            handleClose();
        }
    };

    return (
        <div className="clarification-modal-backdrop" onClick={handleBackdropClick}>
            <div className="clarification-modal">
                <div className="clarification-modal-header">
                    <h3>Help us understand your symptoms better</h3>
                    <button 
                        className="close-button"
                        onClick={handleClose}
                        disabled={isSubmitting}
                    >
                        ×
                    </button>
                </div>

                <div className="clarification-modal-body">
                    <div className="original-message">
                        <strong>Your message:</strong> "{originalMessage}"
                    </div>

                    <div className="clarification-questions">
                        <p>To provide better recommendations, please help us with the following:</p>
                        <ul>
                            {clarificationQuestions.map((question, index) => (
                                <li key={index}>{question}</li>
                            ))}
                        </ul>
                    </div>

                    <form onSubmit={handleSubmit} className="clarification-form">
                        <div className="form-group">
                            <label htmlFor="clarification-response">
                                Please provide more specific details:
                            </label>
                            <textarea
                                id="clarification-response"
                                value={response}
                                onChange={(e) => setResponse(e.target.value)}
                                placeholder="For example: 'I feel sharp pain in my chest that gets worse when I breathe deeply. It started this morning.'"
                                rows="4"
                                disabled={isSubmitting}
                                className={error ? 'error' : ''}
                            />
                        </div>

                        {error && (
                            <div className="error-message">
                                {error}
                            </div>
                        )}

                        <div className="form-actions">
                            <button 
                                type="button" 
                                onClick={handleClose}
                                className="cancel-button"
                                disabled={isSubmitting}
                            >
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                className="submit-button"
                                disabled={isSubmitting || !response.trim()}
                            >
                                {isSubmitting ? 'Processing...' : 'Get Recommendations'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ClarificationModal;
