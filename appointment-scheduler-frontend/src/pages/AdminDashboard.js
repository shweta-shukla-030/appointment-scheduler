import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Admin.css';

const AdminDashboard = ({ user, onLogout }) => {
    const [doctors, setDoctors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showAddForm, setShowAddForm] = useState(false);
    const [editingDoctor, setEditingDoctor] = useState(null);
    const [newDoctor, setNewDoctor] = useState({
        name: '',
        speciality: '',
        location: '',
        feesPerHour: '',
        experience: '',
        qualifications: '',
        about: '',
        contactNumber: '',
        email: '',
        rating: 4.0
    });
    const navigate = useNavigate();

    useEffect(() => {
        fetchDoctors();
    }, []);

    const fetchDoctors = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/doctors');
            const data = await response.json();
            
            if (data.status === 'success') {
                setDoctors(Array.isArray(data.data) ? data.data : []);
            } else {
                setError('Failed to fetch doctors');
            }
        } catch (error) {
            console.error('Error fetching doctors:', error);
            setError('Network error while fetching doctors');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        setNewDoctor({
            ...newDoctor,
            [e.target.name]: e.target.value
        });
    };

    const handleAddDoctor = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/api/admin/doctors', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ...newDoctor,
                    feesPerHour: parseFloat(newDoctor.feesPerHour),
                    rating: parseFloat(newDoctor.rating)
                }),
            });

            const data = await response.json();

            if (data.status === 'success') {
                setNewDoctor({
                    name: '',
                    speciality: '',
                    location: '',
                    feesPerHour: '',
                    experience: '',
                    qualifications: '',
                    about: '',
                    contactNumber: '',
                    email: '',
                    rating: 4.0
                });
                setShowAddForm(false);
                fetchDoctors(); // Refresh the list
            } else {
                setError(data.message || 'Failed to add doctor');
            }
        } catch (error) {
            console.error('Error adding doctor:', error);
            setError('Network error while adding doctor');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteDoctor = async (doctorId) => {
        if (!window.confirm('Are you sure you want to delete this doctor?')) return;

        try {
            const response = await fetch(`http://localhost:8080/api/admin/doctors/${doctorId}`, {
                method: 'DELETE',
            });

            const data = await response.json();

            if (data.status === 'success') {
                fetchDoctors(); // Refresh the list
            } else {
                setError(data.message || 'Failed to delete doctor');
            }
        } catch (error) {
            console.error('Error deleting doctor:', error);
            setError('Network error while deleting doctor');
        }
    };

    const handleToggleAvailability = async (doctorId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/admin/doctors/${doctorId}/availability`, {
                method: 'PUT',
            });

            const data = await response.json();

            if (data.status === 'success') {
                fetchDoctors(); // Refresh the list
            } else {
                setError(data.message || 'Failed to update availability');
            }
        } catch (error) {
            console.error('Error updating availability:', error);
            setError('Network error while updating availability');
        }
    };

    const handleLogout = () => {
        onLogout();
        navigate('/login');
    };

    if (loading) {
        return (
            <div className="admin-loading">
                <div className="spinner"></div>
                <p>Loading admin dashboard...</p>
            </div>
        );
    }

    return (
        <div className="admin-dashboard">
            <header className="admin-header">
                <div className="admin-title">
                    <h1>Admin Dashboard</h1>
                    <span className="welcome-text">Welcome, {user.firstName}!</span>
                </div>
                <div className="admin-actions">
                    <button 
                        className="add-doctor-btn"
                        onClick={() => setShowAddForm(true)}
                    >
                        + Add New Doctor
                    </button>
                    <button className="logout-btn" onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </header>

            {error && (
                <div className="error-banner">
                    <span>{error}</span>
                    <button onClick={() => setError('')}>×</button>
                </div>
            )}

            <div className="admin-stats">
                <div className="stat-card">
                    <h3>{doctors.length}</h3>
                    <p>Total Doctors</p>
                </div>
                <div className="stat-card">
                    <h3>{doctors.filter(d => d.available).length}</h3>
                    <p>Available Doctors</p>
                </div>
                <div className="stat-card">
                    <h3>{new Set(doctors.map(d => d.speciality)).size}</h3>
                    <p>Specialities</p>
                </div>
                <div className="stat-card">
                    <h3>{new Set(doctors.map(d => d.location)).size}</h3>
                    <p>Locations</p>
                </div>
            </div>

            <div className="doctors-table-container">
                <h2>Manage Doctors</h2>
                <div className="table-wrapper">
                    <table className="doctors-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Speciality</th>
                                <th>Location</th>
                                <th>Fees/Hour</th>
                                <th>Rating</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {doctors.map(doctor => (
                                <tr key={doctor.id}>
                                    <td>
                                        <div className="doctor-info">
                                            <strong>{doctor.name}</strong>
                                            {doctor.email && <small>{doctor.email}</small>}
                                        </div>
                                    </td>
                                    <td>{doctor.speciality}</td>
                                    <td>{doctor.location}</td>
                                    <td>${doctor.feesPerHour}</td>
                                    <td>
                                        <span className="rating">
                                            ⭐ {doctor.rating}
                                        </span>
                                    </td>
                                    <td>
                                        <span className={`status ${doctor.available ? 'available' : 'unavailable'}`}>
                                            {doctor.available ? 'Available' : 'Unavailable'}
                                        </span>
                                    </td>
                                    <td className="actions">
                                        <button 
                                            className="toggle-btn"
                                            onClick={() => handleToggleAvailability(doctor.id)}
                                        >
                                            {doctor.available ? 'Disable' : 'Enable'}
                                        </button>
                                        <button 
                                            className="edit-btn"
                                            onClick={() => setEditingDoctor(doctor)}
                                        >
                                            Edit
                                        </button>
                                        <button 
                                            className="delete-btn"
                                            onClick={() => handleDeleteDoctor(doctor.id)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    {doctors.length === 0 && (
                        <div className="empty-state">
                            <p>No doctors found. Add your first doctor to get started.</p>
                        </div>
                    )}
                </div>
            </div>

            {/* Add Doctor Modal */}
            {showAddForm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Add New Doctor</h2>
                        <form onSubmit={handleAddDoctor} className="doctor-form">
                            <div className="form-row">
                                <div className="form-group">
                                    <label>Full Name *</label>
                                    <input
                                        type="text"
                                        name="name"
                                        value={newDoctor.name}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Dr. John Smith"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Speciality *</label>
                                    <input
                                        type="text"
                                        name="speciality"
                                        value={newDoctor.speciality}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Cardiology"
                                    />
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Location *</label>
                                    <input
                                        type="text"
                                        name="location"
                                        value={newDoctor.location}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="New York"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Fees per Hour ($) *</label>
                                    <input
                                        type="number"
                                        name="feesPerHour"
                                        value={newDoctor.feesPerHour}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="150"
                                        min="1"
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Experience</label>
                                <input
                                    type="text"
                                    name="experience"
                                    value={newDoctor.experience}
                                    onChange={handleInputChange}
                                    placeholder="10 years of experience in Cardiology"
                                />
                            </div>

                            <div className="form-group">
                                <label>Qualifications</label>
                                <textarea
                                    name="qualifications"
                                    value={newDoctor.qualifications}
                                    onChange={handleInputChange}
                                    placeholder="MBBS, MD (Cardiology), Fellowship in Interventional Cardiology"
                                    rows={3}
                                />
                            </div>

                            <div className="form-group">
                                <label>About Doctor</label>
                                <textarea
                                    name="about"
                                    value={newDoctor.about}
                                    onChange={handleInputChange}
                                    placeholder="Brief description about the doctor's expertise and approach"
                                    rows={3}
                                />
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Contact Number</label>
                                    <input
                                        type="tel"
                                        name="contactNumber"
                                        value={newDoctor.contactNumber}
                                        onChange={handleInputChange}
                                        placeholder="+1 (555) 123-4567"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Email</label>
                                    <input
                                        type="email"
                                        name="email"
                                        value={newDoctor.email}
                                        onChange={handleInputChange}
                                        placeholder="doctor@hospital.com"
                                    />
                                </div>
                            </div>

                            <div className="form-actions">
                                <button 
                                    type="button" 
                                    className="cancel-btn"
                                    onClick={() => setShowAddForm(false)}
                                >
                                    Cancel
                                </button>
                                <button type="submit" className="save-btn">
                                    Add Doctor
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;
