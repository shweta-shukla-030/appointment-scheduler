import React from 'react';
import { useNavigate } from 'react-router-dom';

const DoctorCard = ({ doctor, selectedDate, selectedTimeSlot }) => {
  const navigate = useNavigate();

  const handleBookAppointment = () => {
    if (!doctor.available) return;
    
    // Navigate to booking page with doctor and appointment details
    navigate('/booking', {
      state: {
        doctor,
        selectedDate,
        selectedTimeSlot
      }
    });
  };

  return (
    <div className="doctor-card">
      <div className="doctor-header">
        <h3>{doctor.name}</h3>
        <span className="rating">★ {doctor.rating}</span>
      </div>
      
      <div className="doctor-info">
        <p><strong>Speciality:</strong> {doctor.speciality}</p>
        <p><strong>Category:</strong> {doctor.category}</p>
        <p><strong>Location:</strong> {doctor.location}</p>
        <p><strong>Experience:</strong> {doctor.yearsOfExperience} years</p>
        <p><strong>Fees:</strong> ${doctor.feesPerHour}/hour</p>
        
        {doctor.available ? (
          <span className="availability available">Available</span>
        ) : (
          <span className="availability unavailable">Not Available</span>
        )}
      </div>
      
      {doctor.reviews && doctor.reviews.length > 0 && (
        <div className="reviews">
          <h4>Recent Reviews:</h4>
          <ul>
            {doctor.reviews.slice(0, 2).map((review, index) => (
              <li key={index}>{review}</li>
            ))}
          </ul>
        </div>
      )}
      
      <button 
        className="book-appointment-btn" 
        disabled={!doctor.available}
        onClick={handleBookAppointment}
      >
        {doctor.available ? 'Book Appointment' : 'Not Available'}
      </button>
    </div>
  );
};

export default DoctorCard;