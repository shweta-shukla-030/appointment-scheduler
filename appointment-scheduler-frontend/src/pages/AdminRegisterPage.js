import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Auth.css';

const AdminRegisterPage = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        adminCode: '' // Special code for admin registration
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError('');
        setSuccess('');
    };

    const validateForm = () => {
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return false;
        }
        if (formData.password.length < 6) {
            setError('Password must be at least 6 characters long');
            return false;
        }
        if (!formData.email.includes('@')) {
            setError('Please enter a valid email address');
            return false;
        }
        // Simple admin code validation (in real app, this would be more secure)
        if (formData.adminCode !== 'ADMIN2025') {
            setError('Invalid admin access code');
            return false;
        }
        return true;
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) return;
        
        setLoading(true);
        setError('');

        try {
            const response = await fetch('http://localhost:8080/api/auth/register/admin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    firstName: formData.firstName,
                    lastName: formData.lastName,
                    email: formData.email,
                    password: formData.password
                }),
            });

            const data = await response.json();

            if (data.status === 'success') {
                setSuccess('Admin registration successful! Please login with your credentials.');
                setTimeout(() => {
                    navigate('/login');
                }, 2000);
            } else {
                setError(data.message || 'Registration failed');
            }
        } catch (error) {
            console.error('Admin registration error:', error);
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card admin-register-card">
                <h2>Admin Registration</h2>
                <p className="auth-subtitle">Create administrator account for doctor management</p>
                
                {error && (
                    <div className="error-message">
                        <i className="error-icon">⚠️</i>
                        {error}
                    </div>
                )}

                {success && (
                    <div className="success-message">
                        <i className="success-icon">✅</i>
                        {success}
                    </div>
                )}

                <form onSubmit={handleRegister} className="auth-form">
                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="firstName">First Name *</label>
                            <input
                                type="text"
                                id="firstName"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                required
                                placeholder="First name"
                                className="form-input"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="lastName">Last Name *</label>
                            <input
                                type="text"
                                id="lastName"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                required
                                placeholder="Last name"
                                className="form-input"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Admin Email Address *</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            required
                            placeholder="admin.email@hospital.com"
                            className="form-input"
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="password">Password *</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                required
                                placeholder="At least 6 characters"
                                className="form-input"
                                minLength={6}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="confirmPassword">Confirm Password *</label>
                            <input
                                type="password"
                                id="confirmPassword"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleInputChange}
                                required
                                placeholder="Confirm password"
                                className="form-input"
                                minLength={6}
                            />
                        </div>
                    </div>

                    <div className="form-group admin-code-group">
                        <label htmlFor="adminCode">Admin Access Code *</label>
                        <input
                            type="password"
                            id="adminCode"
                            name="adminCode"
                            value={formData.adminCode}
                            onChange={handleInputChange}
                            required
                            placeholder="Enter admin access code"
                            className="form-input"
                        />
                        <small className="form-hint">Contact system administrator for access code</small>
                    </div>

                    <button 
                        type="submit" 
                        className={`auth-button admin-button ${loading ? 'loading' : ''}`}
                        disabled={loading}
                    >
                        {loading ? 'Creating Admin Account...' : 'Create Admin Account'}
                    </button>
                </form>

                <div className="auth-links">
                    <p>
                        Already have an account? 
                        <button 
                            className="link-button"
                            onClick={() => navigate('/login')}
                        >
                            Sign In
                        </button>
                    </p>
                    <p>
                        Need a patient account?
                        <button 
                            className="link-button"
                            onClick={() => navigate('/register')}
                        >
                            Patient Registration
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default AdminRegisterPage;
