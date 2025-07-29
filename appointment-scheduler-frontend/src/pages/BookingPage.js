import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/App.css';

const BookingPage = ({ user, onLogout }) => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const [doctor, setDoctor] = useState(null);
  const [cameFromChat, setCameFromChat] = useState(false);
  const [isInitialized, setIsInitialized] = useState(false);
  
  // Get and store doctor data once on component mount
  useEffect(() => {
    // Prevent duplicate initialization
    if (isInitialized) {
      console.log('BookingPage already initialized, skipping...');
      return;
    }

    console.log('BookingPage mounted, checking for doctor data...');
    
    // Get doctor data from either navigation state or sessionStorage (from chat)
    const getSelectedDoctorData = () => {
      // First check navigation state (original booking flow)
      if (location.state?.doctor) {
        console.log('Found doctor in location.state');
        return location.state.doctor;
      }
      
      // Then check sessionStorage (from AI chat booking)
      const storedDoctor = sessionStorage.getItem('selectedDoctor');
      if (storedDoctor) {
        try {
          console.log('Found doctor in sessionStorage');
          return JSON.parse(storedDoctor);
        } catch (error) {
          console.error('Error parsing stored doctor:', error);
        }
      }
      
      console.log('No doctor found in either location.state or sessionStorage');
      return null;
    };
    
    const selectedDoctor = getSelectedDoctorData();
    console.log('Initial doctor load:', selectedDoctor);
    
    if (selectedDoctor) {
      setDoctor(selectedDoctor);
      
      // Determine if came from chat BEFORE clearing sessionStorage
      const fromChat = !location.state?.doctor && sessionStorage.getItem('selectedDoctor') !== null;
      setCameFromChat(fromChat);
      console.log('Came from chat:', fromChat);
      
      // Clear sessionStorage after getting the data to prevent conflicts
      if (sessionStorage.getItem('selectedDoctor')) {
        sessionStorage.removeItem('selectedDoctor');
        console.log('Cleared sessionStorage');
      }
      
      setIsInitialized(true);
    } else {
      console.log('No doctor data found, will redirect to home');
      setDoctor(false); // Set to false to trigger redirect
      setIsInitialized(true);
    }
  }, []); // Empty dependency array - run only once on mount

  const [appointmentDetails, setAppointmentDetails] = useState({
    reasonForVisit: '',
    additionalNotes: ''
  });
  const [selectedDate, setSelectedDate] = useState(location.state?.selectedDate || '');
  const [selectedTimeSlot, setSelectedTimeSlot] = useState(location.state?.selectedTimeSlot || '');
  const [isLoading, setIsLoading] = useState(false);
  const [bookingStatus, setBookingStatus] = useState(null); // 'success', 'error', null

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  // Use actual user data instead of sample patient
  const currentPatient = {
    id: user?.id || 1,
    name: user?.firstName && user?.lastName ? `${user.firstName} ${user.lastName}` : 'John Doe',
    email: user?.email || 'john.doe@email.com',
    phone: user?.phoneNumber || '+1-555-0123',
    age: 32 // You might want to add age field to User model later
  };

  useEffect(() => {
    console.log('Doctor check useEffect running, doctor:', doctor);
    // If no doctor data, redirect back to home
    if (doctor === null) {
      console.log('Doctor is null, waiting for initial load...');
    } else if (!doctor) {
      console.log('No doctor found, redirecting to home');
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

  const handleDateChange = (e) => {
    console.log('Date changed:', e.target.value);
    setSelectedDate(e.target.value);
  };

  const handleTimeChange = (e) => {
    console.log('Time changed:', e.target.value);
    setSelectedTimeSlot(e.target.value);
  };

  const handleBookAppointment = async (e) => {
    if (e) e.preventDefault();
    console.log('handleBookAppointment called');
    
    if (!appointmentDetails.reasonForVisit.trim()) {
      alert('Please enter a reason for visit');
      return;
    }

    if (!selectedDate) {
      alert('Please select an appointment date');
      return;
    }

    if (!selectedTimeSlot) {
      alert('Please select an appointment time');
      return;
    }

    console.log('Starting booking process...');
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
        patientId: currentPatient.id,
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

  if (!isInitialized || doctor === null) {
    return <div>Loading...</div>;
  }

  if (!doctor) {
    return <div>No doctor selected. Redirecting...</div>;
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
                <p><strong>Patient:</strong> {currentPatient.name}</p>
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
      <div className="user-header">
        <span>Welcome, {user.firstName}!</span>
        <button onClick={handleLogout} className="logout-button">Logout</button>
      </div>
      
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

        {/* Show notification if came from AI chat */}
        {cameFromChat && (
          <div className="chat-booking-notification">
            <div className="notification-content">
              <i className="fas fa-robot"></i>
              <span>Great choice! Your AI assistant recommended this doctor based on your symptoms.</span>
              <button 
                onClick={() => navigate('/chat')}
                className="back-to-chat-btn"
              >
                <i className="fas fa-comments"></i>
                Back to Chat
              </button>
            </div>
          </div>
        )}

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
            {selectedDate && selectedTimeSlot ? (
              <div className="appointment-info">
                <p><strong>Date:</strong> {selectedDate}</p>
                <p><strong>Time:</strong> {selectedTimeSlot}</p>
                <p><strong>Duration:</strong> 1 hour</p>
                {cameFromChat && (
                  <button 
                    onClick={() => {setSelectedDate(''); setSelectedTimeSlot('');}}
                    className="change-appointment-btn"
                  >
                    Change Date/Time
                  </button>
                )}
              </div>
            ) : (
              <div className="appointment-selection">
                <div className="form-group">
                  <label htmlFor="appointmentDate">
                    Select Date <span className="required">*</span>
                  </label>
                  <input
                    type="date"
                    id="appointmentDate"
                    value={selectedDate}
                    onChange={handleDateChange}
                    min={new Date().toISOString().split('T')[0]}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="appointmentTime">
                    Select Time <span className="required">*</span>
                  </label>
                  <select
                    id="appointmentTime"
                    value={selectedTimeSlot}
                    onChange={handleTimeChange}
                    required
                  >
                    <option value="">Choose a time slot</option>
                    <option value="09:00 AM - 10:00 AM">09:00 AM - 10:00 AM</option>
                    <option value="10:00 AM - 11:00 AM">10:00 AM - 11:00 AM</option>
                    <option value="11:00 AM - 12:00 PM">11:00 AM - 12:00 PM</option>
                    <option value="02:00 PM - 03:00 PM">02:00 PM - 03:00 PM</option>
                    <option value="03:00 PM - 04:00 PM">03:00 PM - 04:00 PM</option>
                    <option value="04:00 PM - 05:00 PM">04:00 PM - 05:00 PM</option>
                  </select>
                </div>
              </div>
            )}
          </div>

          {/* Patient Information */}
          <div className="patient-summary">
            <h2>Patient Information</h2>
            <div className="patient-info">
              <p><strong>Name:</strong> {currentPatient.name}</p>
              <p><strong>Email:</strong> {currentPatient.email}</p>
              <p><strong>Phone:</strong> {currentPatient.phone}</p>
            </div>
          </div>

          {/* Appointment Form */}
          <form onSubmit={handleBookAppointment}>
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
                type="submit"
                disabled={isLoading || !selectedDate || !selectedTimeSlot || !appointmentDetails.reasonForVisit.trim()}
                className="book-button"
              >
                {isLoading ? 'Booking...' : 'Confirm Booking'}
              </button>
              <p className="booking-note">
                {!selectedDate || !selectedTimeSlot ? 
                  'Please select date and time to continue' : 
                  'Review your booking details and confirm'}
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default BookingPage;
