import React, { useState } from 'react';
import '../styles/Chat.css';

/**
 * MessageBubble Component - Individual chat message display
 * Supports both user and AI messages with different styling and interactive booking elements
 */
const MessageBubble = ({ message, showTyping = false, onSendMessage }) => {
    const isUser = message.type === 'user';
    const isAI = message.type === 'ai';
    const [selectedDate, setSelectedDate] = useState(null);

    const formatTime = (timestamp) => {
        return new Date(timestamp).toLocaleTimeString([], { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };

    const renderDoctorRecommendations = () => {
        if (!message.doctors || message.doctors.length === 0) return null;

        return (
            <div className="doctor-recommendations">
                <div className="recommendations-header">
                    <i className="fas fa-user-md"></i>
                    <span>Recommended Doctors:</span>
                </div>
                <div className="doctors-list">
                    {message.doctors.map((doctor, index) => (
                        <div key={index} className="doctor-card-mini">
                            <div className="doctor-info">
                                <div className="doctor-name">{doctor.name}</div>
                                <div className="doctor-specialty">{doctor.specialty || doctor.speciality}</div>
                                {doctor.experience && (
                                    <div className="doctor-experience">
                                        {doctor.experience} years experience
                                    </div>
                                )}
                                {doctor.yearsOfExperience && (
                                    <div className="doctor-experience">
                                        {doctor.yearsOfExperience} years experience
                                    </div>
                                )}
                                {doctor.feesPerHour && (
                                    <div className="doctor-fee">
                                        ${doctor.feesPerHour}/hour
                                    </div>
                                )}
                                {doctor.location && (
                                    <div className="doctor-location">
                                        <i className="fas fa-map-marker-alt"></i>
                                        {doctor.location}
                                    </div>
                                )}
                                {doctor.rating && (
                                    <div className="doctor-rating">
                                        <i className="fas fa-star"></i>
                                        {doctor.rating}
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        );
    };

    const renderLocationSelection = () => {
        console.log('[MessageBubble] renderLocationSelection called, intent:', message.intent);
        if (message.intent !== 'booking_location_selection') return null;
        
        // Extract locations from the message text
        const locations = extractLocationsFromMessage(message.text);
        console.log('[MessageBubble] Locations extracted:', locations);
        if (locations.length === 0) return null;

        console.log('[MessageBubble] Rendering location selection with', locations.length, 'locations');
        return (
            <div className="booking-interactions">
                <div className="interaction-header">
                    <i className="fas fa-map-marker-alt"></i>
                    <span>Select Location:</span>
                </div>
                <div className="location-buttons">
                    {locations.map((location, index) => (
                        <button
                            key={index}
                            className="booking-option-btn location-btn"
                            onClick={() => handleLocationSelect(location)}
                        >
                            <i className="fas fa-map-marker-alt"></i>
                            {location}
                        </button>
                    ))}
                </div>
            </div>
        );
    };

    const renderTimeSlotSelection = () => {
        if (message.intent !== 'booking_time_selection') return null;
        
        // Extract time slots from the message text
        const timeSlots = extractTimeSlotsFromMessage(message.text);
        if (timeSlots.length === 0) return null;

        return (
            <div className="booking-interactions">
                <div className="interaction-header">
                    <i className="fas fa-clock"></i>
                    <span>Select Time Slot:</span>
                </div>
                <div className="time-slot-buttons">
                    {timeSlots.map((slot, index) => (
                        <button
                            key={index}
                            className="booking-option-btn time-slot-btn"
                            onClick={() => handleTimeSlotSelect(slot)}
                        >
                            <i className="fas fa-clock"></i>
                            {slot}
                        </button>
                    ))}
                </div>
            </div>
        );
    };

    const renderDatePicker = () => {
        if (message.intent !== 'booking_date_selection') return null;

        return (
            <div className="booking-interactions">
                <div className="interaction-header">
                    <i className="fas fa-calendar-alt"></i>
                    <span>Select Date:</span>
                </div>
                <div className="date-picker-container">
                    <input
                        type="date"
                        className="booking-date-picker"
                        min={new Date().toISOString().split('T')[0]}
                        max={new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]}
                        onChange={(e) => handleDateSelect(e.target.value)}
                        value={selectedDate || ''}
                    />
                    <div className="quick-date-buttons">
                        <button
                            className="booking-option-btn quick-date-btn"
                            onClick={() => handleQuickDateSelect(1)}
                        >
                            Tomorrow
                        </button>
                        <button
                            className="booking-option-btn quick-date-btn"
                            onClick={() => handleQuickDateSelect(2)}
                        >
                            Day After
                        </button>
                        <button
                            className="booking-option-btn quick-date-btn"
                            onClick={() => handleQuickDateSelect(7)}
                        >
                            Next Week
                        </button>
                    </div>
                </div>
            </div>
        );
    };

    // Helper functions
    const extractLocationsFromMessage = (text) => {
        const locations = [];
        // Look for patterns like "• Location" or "1. Location" or numbered locations
        const locationMatches = text.match(/[•\d+\.]\s*([A-Za-z\s]+)(?=\n|$)/g);
        console.log('[MessageBubble] Extracting locations from text:', text);
        console.log('[MessageBubble] Location matches found:', locationMatches);
        
        if (locationMatches) {
            locationMatches.forEach(match => {
                const location = match.replace(/[•\d+\.\s]/g, '').trim();
                if (location && location.length > 2) {
                    locations.push(location);
                }
            });
        }
        
        console.log('[MessageBubble] Extracted locations:', locations);
        return locations;
    };

    const extractTimeSlotsFromMessage = (text) => {
        const timeSlots = [];
        // Look for time patterns like "1. 09:00 AM - 10:00 AM"
        const timeMatches = text.match(/\d+\.\s*(\d{1,2}:\d{2}\s*[AP]M\s*-\s*\d{1,2}:\d{2}\s*[AP]M)/g);
        if (timeMatches) {
            timeMatches.forEach(match => {
                const timeSlot = match.replace(/\d+\.\s*/, '').trim();
                timeSlots.push(timeSlot);
            });
        }
        return timeSlots;
    };

    const handleLocationSelect = (location) => {
        console.log('[MessageBubble] Location button clicked:', location);
        console.log('[MessageBubble] onSendMessage function:', onSendMessage);
        if (onSendMessage) {
            console.log('[MessageBubble] Sending location message:', location);
            onSendMessage(location);
        } else {
            console.error('[MessageBubble] onSendMessage is not defined!');
        }
    };

    const handleTimeSlotSelect = (timeSlot) => {
        console.log('[MessageBubble] Time slot button clicked:', timeSlot);
        console.log('[MessageBubble] onSendMessage function:', onSendMessage);
        if (onSendMessage) {
            console.log('[MessageBubble] Sending time slot message:', timeSlot);
            onSendMessage(timeSlot);
        } else {
            console.error('[MessageBubble] onSendMessage is not defined!');
        }
    };

    const handleDateSelect = (date) => {
        console.log('[MessageBubble] Date picker changed:', date);
        console.log('[MessageBubble] onSendMessage function:', onSendMessage);
        setSelectedDate(date);
        if (onSendMessage) {
            console.log('[MessageBubble] Sending date message:', date);
            onSendMessage(date);
        } else {
            console.error('[MessageBubble] onSendMessage is not defined!');
        }
    };

    const handleQuickDateSelect = (daysFromNow) => {
        const selectedDate = new Date();
        selectedDate.setDate(selectedDate.getDate() + daysFromNow);
        const dateString = selectedDate.toISOString().split('T')[0];
        console.log('[MessageBubble] Quick date button clicked:', daysFromNow, 'days, date:', dateString);
        console.log('[MessageBubble] onSendMessage function:', onSendMessage);
        setSelectedDate(dateString);
        if (onSendMessage) {
            console.log('[MessageBubble] Sending quick date message:', dateString);
            onSendMessage(dateString);
        } else {
            console.error('[MessageBubble] onSendMessage is not defined!');
        }
    };

    const renderTypingIndicator = () => {
        if (!showTyping) return null;
        
        return (
            <div className="message-bubble ai typing">
                <div className="message-content">
                    <div className="typing-indicator">
                        <div className="typing-dots">
                            <span></span>
                            <span></span>
                            <span></span>
                        </div>
                        <span className="typing-text">AI is typing...</span>
                    </div>
                </div>
            </div>
        );
    };

    if (showTyping) {
        return renderTypingIndicator();
    }

    return (
        <div className={`message-bubble ${isUser ? 'user' : 'ai'}`}>
            <div className="message-content">
                {isAI && (
                    <div className="ai-avatar">
                        <i className="fas fa-robot"></i>
                    </div>
                )}
                
                <div className="message-text">
                    {message.text}
                    
                    {/* Show intent badge for AI messages */}
                    {isAI && message.intent && (
                        <div className={`intent-badge intent-${message.intent}`}>
                            {message.intent}
                        </div>
                    )}
                    
                    {/* Show doctor recommendations */}
                    {renderDoctorRecommendations()}
                    
                    {/* Show interactive booking components */}
                    {renderLocationSelection()}
                    {renderDatePicker()}
                    {renderTimeSlotSelection()}
                </div>
                
                {isUser && (
                    <div className="user-avatar">
                        <i className="fas fa-user"></i>
                    </div>
                )}
            </div>
            
            <div className="message-timestamp">
                {formatTime(message.timestamp)}
                {isUser && (
                    <i className="fas fa-check message-status"></i>
                )}
            </div>
        </div>
    );
};

export default MessageBubble;
