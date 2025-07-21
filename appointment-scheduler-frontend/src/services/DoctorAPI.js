// API configuration
const API_BASE_URL = 'http://localhost:8080/api/doctors';

class DoctorAPI {
  // Get all doctors
  static async getAllDoctors() {
    try {
      const response = await fetch(`${API_BASE_URL}/all`);
      if (!response.ok) throw new Error('Failed to fetch doctors');
      return await response.json();
    } catch (error) {
      console.error('Error fetching doctors:', error);
      throw error;
    }
  }

  // Search doctors by filters with availability checking
  static async searchDoctors(location, speciality, date, timeSlot) {
    try {
      const params = new URLSearchParams();
      if (location && location.trim()) params.append('location', location);
      if (speciality && speciality.trim()) params.append('speciality', speciality);
      if (date && date.trim()) params.append('date', date);
      if (timeSlot && timeSlot.trim()) params.append('timeSlot', timeSlot);
      
      const response = await fetch(`${API_BASE_URL}/search?${params}`);
      if (!response.ok) throw new Error('Failed to search doctors');
      return await response.json();
    } catch (error) {
      console.error('Error searching doctors:', error);
      throw error;
    }
  }

  // Get unique locations
  static async getLocations() {
    try {
      const response = await fetch(`${API_BASE_URL}/locations`);
      if (!response.ok) throw new Error('Failed to fetch locations');
      return await response.json();
    } catch (error) {
      console.error('Error fetching locations:', error);
      throw error;
    }
  }

  // Get unique specialities
  static async getSpecialities() {
    try {
      const response = await fetch(`${API_BASE_URL}/specialities`);
      if (!response.ok) throw new Error('Failed to fetch specialities');
      return await response.json();
    } catch (error) {
      console.error('Error fetching specialities:', error);
      throw error;
    }
  }
}

export default DoctorAPI;
