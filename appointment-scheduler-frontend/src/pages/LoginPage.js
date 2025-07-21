import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Auth.css';

const LoginPage = ({ onLogin }) => {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError(''); // Clear error when user types
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            });

            const data = await response.json();

            if (data.status === 'success') {
                // Store user data in localStorage
                localStorage.setItem('currentUser', JSON.stringify(data.user));
                
                // Call parent component's onLogin function
                onLogin(data.user);
                
                // Redirect based on user role
                if (data.user.role === 'ADMIN') {
                    navigate('/admin/dashboard');
                } else {
                    navigate('/');
                }
            } else {
                setError(data.message || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Login to Your Account</h2>
                <p className="auth-subtitle">Welcome back! Please enter your credentials.</p>
                
                {error && (
                    <div className="error-message">
                        <i className="error-icon">⚠️</i>
                        {error}
                    </div>
                )}

                <form onSubmit={handleLogin} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="email">Email Address</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            required
                            placeholder="Enter your email"
                            className="form-input"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                            required
                            placeholder="Enter your password"
                            className="form-input"
                            minLength={6}
                        />
                    </div>

                    <button 
                        type="submit" 
                        className={`auth-button ${loading ? 'loading' : ''}`}
                        disabled={loading}
                    >
                        {loading ? 'Signing In...' : 'Sign In'}
                    </button>
                </form>

                <div className="auth-links">
                    <p>
                        Don't have an account? 
                        <button 
                            className="link-button"
                            onClick={() => navigate('/register')}
                        >
                            Sign Up
                        </button>
                    </p>
                    <p>
                        Are you an admin?
                        <button 
                            className="link-button"
                            onClick={() => navigate('/admin/register')}
                        >
                            Admin Registration
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;
