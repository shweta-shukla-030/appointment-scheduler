import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import DoctorCard from '../components/DoctorCard';
import DoctorFilter from '../components/DoctorFilter';
import DoctorAPI from '../services/DoctorAPI';

const HomePage = ({ user, onLogout }) => {
  const navigate = useNavigate();
  
  // State for doctors and filters
  const [allDoctors, setAllDoctors] = useState([]);
  const [filteredDoctors, setFilteredDoctors] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState('');
  const [selectedSpeciality, setSelectedSpeciality] = useState('');
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTimeSlot, setSelectedTimeSlot] = useState('');
  const [locations, setLocations] = useState([]);
  const [specialities, setSpecialities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  // Mock data as fallback - In real application, this would come from API
  const mockDoctors = [
    {
      id: 1,
      name: 'Dr. John Smith',
      speciality: 'Cardiology',
      category: 'Specialist',
      yearsOfExperience: 15,
      feesPerHour: 200,
      rating: 4.8,
      reviews: ['Excellent doctor', 'Very professional'],
      location: 'New York',
      available: true
    },
    {
      id: 2,
      name: 'Dr. Sarah Johnson',
      speciality: 'Family Medicine',
      category: 'General Practitioner',
      yearsOfExperience: 8,
      feesPerHour: 120,
      rating: 4.5,
      reviews: ['Great bedside manner', 'Thorough examination'],
      location: 'Los Angeles',
      available: true
    },
    {
      id: 3,
      name: 'Dr. Michael Brown',
      speciality: 'Neurology',
      category: 'Specialist',
      yearsOfExperience: 12,
      feesPerHour: 250,
      rating: 4.9,
      reviews: ['Outstanding expertise', 'Saved my life'],
      location: 'Chicago',
      available: true
    },
    {
      id: 4,
      name: 'Dr. Emily Davis',
      speciality: 'Dermatology',
      category: 'Specialist',
      yearsOfExperience: 6,
      feesPerHour: 180,
      rating: 4.6,
      reviews: ['Very knowledgeable', 'Quick diagnosis'],
      location: 'New York',
      available: true
    },
    {
      id: 5,
      name: 'Dr. Robert Wilson',
      speciality: 'Orthopedics',
      category: 'Specialist',
      yearsOfExperience: 20,
      feesPerHour: 300,
      rating: 4.7,
      reviews: ['Experienced surgeon', 'Excellent results'],
      location: 'Miami',
      available: true
    },
    {
      id: 6,
      name: 'Dr. Lisa Anderson',
      speciality: 'Internal Medicine',
      category: 'General Practitioner',
      yearsOfExperience: 10,
      feesPerHour: 150,
      rating: 4.4,
      reviews: ['Patient and caring', 'Listens well'],
      location: 'Los Angeles',
      available: true
    },
    {
      id: 7,
      name: 'Dr. David Martinez',
      speciality: 'Cardiology',
      category: 'Specialist',
      yearsOfExperience: 18,
      feesPerHour: 280,
      rating: 4.9,
      reviews: ['Top cardiologist', 'Highly recommended'],
      location: 'Chicago',
      available: true
    },
    {
      id: 8,
      name: 'Dr. Jennifer Taylor',
      speciality: 'Pediatrics',
      category: 'Specialist',
      yearsOfExperience: 9,
      feesPerHour: 160,
      rating: 4.8,
      reviews: ['Great with children', 'Very gentle'],
      location: 'Miami',
      available: true
    }
  ];

  // Initialize data on component mount
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Try to load data from API
        const [doctorsData, locationsData, specialitiesData] = await Promise.all([
          DoctorAPI.getAllDoctors(),
          DoctorAPI.getLocations(),
          DoctorAPI.getSpecialities()
        ]);
        
        setAllDoctors(doctorsData);
        setFilteredDoctors(doctorsData);
        setLocations(locationsData);
        setSpecialities(specialitiesData);
        
      } catch (error) {
        console.warn('API not available, using mock data:', error);
        setError('API not available, using sample data');
        
        // Use mock data as fallback
        setAllDoctors(mockDoctors);
        setFilteredDoctors(mockDoctors);
        
        // Extract unique locations and specialities from mock data
        const uniqueLocations = [...new Set(mockDoctors.map(doctor => doctor.location))].sort();
        const uniqueSpecialities = [...new Set(mockDoctors.map(doctor => doctor.speciality))].sort();
        
        setLocations(uniqueLocations);
        setSpecialities(uniqueSpecialities);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // Filter doctors based on selected criteria
  const filterDoctors = async () => {
    try {
      setLoading(true);
      
      // Try to use API search with availability checking
      const filtered = await DoctorAPI.searchDoctors(selectedLocation, selectedSpeciality, selectedDate, selectedTimeSlot);
      setFilteredDoctors(filtered);
      
    } catch (error) {
      console.warn('API search not available, using local filtering:', error);
      
      // Fallback to local filtering
      let filtered = allDoctors.filter(doctor => doctor.available);

      if (selectedLocation && selectedLocation.trim() !== '') {
        filtered = filtered.filter(doctor => 
          doctor.location.toLowerCase().includes(selectedLocation.toLowerCase())
        );
      }

      if (selectedSpeciality && selectedSpeciality.trim() !== '') {
        filtered = filtered.filter(doctor => 
          doctor.speciality.toLowerCase().includes(selectedSpeciality.toLowerCase())
        );
      }

      setFilteredDoctors(filtered);
    } finally {
      setLoading(false);
    }
  };

  // Handle filter changes
  const handleLocationChange = (location) => {
    setSelectedLocation(location);
  };

  const handleSpecialityChange = (speciality) => {
    setSelectedSpeciality(speciality);
  };

  const handleDateChange = (date) => {
    setSelectedDate(date);
  };

  const handleTimeSlotChange = (timeSlot) => {
    setSelectedTimeSlot(timeSlot);
  };

  const handleFilterSubmit = () => {
    filterDoctors();
  };

  // Clear filters
  const clearFilters = () => {
    setSelectedLocation('');
    setSelectedSpeciality('');
    setSelectedDate('');
    setSelectedTimeSlot('');
    setFilteredDoctors(allDoctors);
  };

  if (loading) {
    return <div className="loading">Loading doctors...</div>;
  }

  return (
    <div className="home-page">
      {/* User Header with Logout */}
      <div className="user-header">
        <div className="user-info">
          <span className="welcome-text">
            Welcome, <strong>{user?.firstName} {user?.lastName}</strong>
          </span>
          <span className="user-role">{user?.role === 'PATIENT' ? '👤 Patient' : '🛡️ Admin'}</span>
        </div>
        <div className="header-actions">
          <button className="ai-assistant-btn" onClick={() => navigate('/chat')}>
            <i className="fas fa-robot"></i>
            AI Assistant
          </button>
          <button className="profile-btn" onClick={() => navigate('/profile')}>
            My Profile
          </button>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>

      <header className="page-header">
        <h1>Doctor Appointment Scheduler</h1>
        <p>Find and book appointments with qualified healthcare professionals</p>
        {error && <div className="error-message">⚠️ {error}</div>}
      </header>

      <div className="content-container">
        <div className="filters-section">
          <DoctorFilter
            location={selectedLocation}
            speciality={selectedSpeciality}
            selectedDate={selectedDate}
            selectedTimeSlot={selectedTimeSlot}
            onLocationChange={handleLocationChange}
            onSpecialityChange={handleSpecialityChange}
            onDateChange={handleDateChange}
            onTimeSlotChange={handleTimeSlotChange}
            onFilterSubmit={handleFilterSubmit}
            locations={locations}
            specialities={specialities}
          />
          
          {(selectedLocation || selectedSpeciality || selectedDate || selectedTimeSlot) && (
            <div className="active-filters">
              <h4>Active Filters:</h4>
              {selectedLocation && <span className="filter-tag">Location: {selectedLocation}</span>}
              {selectedSpeciality && <span className="filter-tag">Speciality: {selectedSpeciality}</span>}
              {selectedDate && <span className="filter-tag">Date: {selectedDate}</span>}
              {selectedTimeSlot && <span className="filter-tag">Time: {new Date(`1970-01-01T${selectedTimeSlot}:00`).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>}
              <button onClick={clearFilters} className="clear-filters-btn">Clear All Filters</button>
            </div>
          )}
        </div>

        <div className="results-section">
          <div className="results-header">
            <h2>Available Doctors ({filteredDoctors.length})</h2>
          </div>
          
          {filteredDoctors.length === 0 ? (
            <div className="no-results">
              <p>No doctors found matching your criteria.</p>
              <button onClick={clearFilters} className="clear-filters-btn">Show All Doctors</button>
            </div>
          ) : (
            <div className="doctors-grid">
              {filteredDoctors.map((doctor) => (
                <DoctorCard 
                  key={doctor.id} 
                  doctor={doctor}
                  selectedDate={selectedDate}
                  selectedTimeSlot={selectedTimeSlot}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default HomePage;