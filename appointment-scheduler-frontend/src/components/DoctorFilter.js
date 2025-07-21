import React from 'react';

const DoctorFilter = ({ 
  location, 
  speciality,
  selectedDate,
  selectedTimeSlot, 
  onLocationChange, 
  onSpecialityChange,
  onDateChange,
  onTimeSlotChange, 
  onFilterSubmit,
  locations = [],
  specialities = []
}) => {
  
  const handleSubmit = (e) => {
    e.preventDefault();
    onFilterSubmit();
  };

  // Generate time slots for the dropdown (full hour ranges)
  const timeSlots = [
    '09:00 AM - 10:00 AM',
    '10:00 AM - 11:00 AM', 
    '11:00 AM - 12:00 PM',
    '12:00 PM - 01:00 PM',
    '01:00 PM - 02:00 PM',
    '02:00 PM - 03:00 PM',
    '03:00 PM - 04:00 PM',
    '04:00 PM - 05:00 PM'
  ];

  // Get today's date in YYYY-MM-DD format for min date
  const today = new Date().toISOString().split('T')[0];

  return (
    <div className="filter-container">
      <h3>Find Doctors</h3>
      <form onSubmit={handleSubmit} className="filter-form">
        <div className="filter-group">
          <label htmlFor="location">Location:</label>
          <select 
            id="location"
            value={location} 
            onChange={(e) => onLocationChange(e.target.value)}
            className="filter-select"
          >
            <option value="">All Locations</option>
            {locations.map((loc, index) => (
              <option key={index} value={loc}>{loc}</option>
            ))}
          </select>
        </div>
        
        <div className="filter-group">
          <label htmlFor="speciality">Speciality:</label>
          <select 
            id="speciality"
            value={speciality} 
            onChange={(e) => onSpecialityChange(e.target.value)}
            className="filter-select"
          >
            <option value="">All Specialities</option>
            {specialities.map((spec, index) => (
              <option key={index} value={spec}>{spec}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="date">Date:</label>
          <input 
            type="date"
            id="date"
            value={selectedDate} 
            onChange={(e) => onDateChange(e.target.value)}
            className="filter-select"
            min={today}
          />
        </div>

        <div className="filter-group">
          <label htmlFor="timeSlot">Time Slot:</label>
          <select 
            id="timeSlot"
            value={selectedTimeSlot} 
            onChange={(e) => onTimeSlotChange(e.target.value)}
            className="filter-select"
          >
            <option value="">Any Time</option>
            {timeSlots.map((slot, index) => (
              <option key={index} value={slot}>
                {slot}
              </option>
            ))}
          </select>
        </div>
        
        <button type="submit" className="filter-button">
          Search Doctors
        </button>
      </form>
    </div>
  );
};

export default DoctorFilter;
