import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/App.css';

const BookingPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { doctor, selectedDate, selectedTimeSlot } = location.state || {};

  const [appointmentDetails, setAppointmentDetails] = useState({
    reasonForVisit: '',
    additionalNotes: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [bookingStatus, setBookingStatus] = useState(null); // 'success', 'error', null

  // Sample patient data (hardcoded as requested)
  const samplePatient = {
    id: 1,
    name: 'John Doe',
    email: 'john.doe@email.com',
    phone: '+1-555-0123',
    age: 32
  };

  useEffect(() => {
    // If no doctor data, redirect back to home
    if (!doctor) {
      navigate('/');
    }
  }, [doctor, navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setAppointmentDetails(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleBookAppointment = async () => {
    if (!appointmentDetails.reasonForVisit.trim()) {
      alert('Please enter a reason for visit');
      return;
    }

    setIsLoading(true);
    try {
      // Parse the time slot to get start and end times
      // Expected format: "10:00 AM - 11:00 AM"
      let startTimeStr, endTimeStr;
      
      if (selectedTimeSlot && selectedTimeSlot.includes(' - ')) {
        [startTimeStr, endTimeStr] = selectedTimeSlot.split(' - ');
      } else {
        // Handle case where timeSlot might not be in expected format
        throw new Error('Invalid time slot format: ' + selectedTimeSlot);
      }
      
      const bookingData = {
        doctorId: doctor.id,
        patientId: samplePatient.id,
        appointmentDate: selectedDate,
        startTime: startTimeStr.trim(),
        endTime: endTimeStr.trim(),
        reasonForVisit: appointmentDetails.reasonForVisit,
        additionalNotes: appointmentDetails.additionalNotes,
        status: 'CONFIRMED'
      };

      console.log('Booking Data:', bookingData);

      // Call backend API to create booking
      const response = await fetch('http://localhost:8080/api/appointments/book', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookingData)
      });

      const result = await response.json();

      if (response.ok && result.status === 'success') {
        setBookingStatus('success');
      } else {
        throw new Error(result.message || 'Booking failed');
      }
      
    } catch (error) {
      console.error('Booking failed:', error);
      setBookingStatus('error');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackToHome = () => {
    navigate('/');
  };

  if (!doctor) {
    return <div>Loading...</div>;
  }

  if (bookingStatus === 'success') {
    return (
      <div className="booking-page">
        <div className="booking-container">
          <div className="booking-success">
            <h2>✅ Appointment Booked Successfully!</h2>
            <div className="success-details">
              <h3>Booking Confirmation</h3>
              <div className="confirmation-info">
                <p><strong>Doctor:</strong> {doctor.name}</p>
                <p><strong>Speciality:</strong> {doctor.speciality}</p>
                <p><strong>Date:</strong> {selectedDate}</p>
                <p><strong>Time:</strong> {selectedTimeSlot}</p>
                <p><strong>Patient:</strong> {samplePatient.name}</p>
                <p><strong>Reason:</strong> {appointmentDetails.reasonForVisit}</p>
                <p><strong>Fees:</strong> ${doctor.feesPerHour}</p>
              </div>
            </div>
            <button 
              onClick={handleBackToHome}
              className="back-button"
            >
              Back to Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (bookingStatus === 'error') {
    return (
      <div className="booking-page">
        <div className="booking-container">
          <div className="booking-error">
            <h2>❌ Booking Failed</h2>
            <p>Sorry, we couldn't complete your booking. Please try again.</p>
            <button 
              onClick={() => setBookingStatus(null)}
              className="retry-button"
            >
              Try Again
            </button>
            <button 
              onClick={handleBackToHome}
              className="back-button"
            >
              Back to Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="booking-page">
      <div className="booking-container">
        <div className="booking-header">
          <button 
            onClick={handleBackToHome}
            className="back-link"
          >
            ← Back to Search
          </button>
          <h1>Book Appointment</h1>
        </div>

        <div className="booking-content">
          {/* Doctor Information */}
          <div className="doctor-summary">
            <h2>Doctor Details</h2>
            <div className="doctor-info">
              <h3>{doctor.name}</h3>
              <p><strong>Speciality:</strong> {doctor.speciality}</p>
              <p><strong>Experience:</strong> {doctor.yearsOfExperience} years</p>
              <p><strong>Rating:</strong> ⭐ {doctor.rating}</p>
              <p><strong>Fees:</strong> ${doctor.feesPerHour}/hour</p>
              <p><strong>Location:</strong> {doctor.location}</p>
            </div>
          </div>

          {/* Appointment Details */}
          <div className="appointment-summary">
            <h2>Appointment Details</h2>
            <div className="appointment-info">
              <p><strong>Date:</strong> {selectedDate}</p>
              <p><strong>Time:</strong> {selectedTimeSlot}</p>
              <p><strong>Duration:</strong> 1 hour</p>
            </div>
          </div>

          {/* Patient Information */}
          <div className="patient-summary">
            <h2>Patient Information</h2>
            <div className="patient-info">
              <p><strong>Name:</strong> {samplePatient.name}</p>
              <p><strong>Email:</strong> {samplePatient.email}</p>
              <p><strong>Phone:</strong> {samplePatient.phone}</p>
              <p><strong>Age:</strong> {samplePatient.age}</p>
            </div>
          </div>

          {/* Appointment Form */}
          <div className="appointment-form">
            <h2>Appointment Information</h2>
            <div className="form-group">
              <label htmlFor="reasonForVisit">
                Reason for Visit <span className="required">*</span>
              </label>
              <input
                type="text"
                id="reasonForVisit"
                name="reasonForVisit"
                value={appointmentDetails.reasonForVisit}
                onChange={handleInputChange}
                placeholder="e.g., Regular checkup, Consultation, etc."
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="additionalNotes">
                Additional Notes (Optional)
              </label>
              <textarea
                id="additionalNotes"
                name="additionalNotes"
                value={appointmentDetails.additionalNotes}
                onChange={handleInputChange}
                placeholder="Any additional information for the doctor..."
                rows="3"
              />
            </div>
          </div>

          {/* Booking Summary */}
          <div className="booking-summary">
            <h2>Booking Summary</h2>
            <div className="summary-details">
              <div className="summary-row">
                <span>Consultation Fee:</span>
                <span>${doctor.feesPerHour}</span>
              </div>
              <div className="summary-row total">
                <span><strong>Total Amount:</strong></span>
                <span><strong>${doctor.feesPerHour}</strong></span>
              </div>
            </div>
          </div>

          {/* Book Button */}
          <div className="booking-actions">
            <button 
              onClick={handleBookAppointment}
              disabled={isLoading}
              className="book-button"
            >
              {isLoading ? 'Booking...' : 'Confirm Booking'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookingPage;
