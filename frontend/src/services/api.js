import axios from 'axios';

// Base URL for the backend API
const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with default config
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Response interceptor to handle errors
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

// User API endpoints
export const userAPI = {
    getAllUsers: () => apiClient.get('/users'),
    
    validateUser: (username) => apiClient.get(`/users/validate/${encodeURIComponent(username)}`),
    
    checkUserExists: (username) => apiClient.get(`/users/exists/${encodeURIComponent(username)}`),
};

// Session API endpoints
export const sessionAPI = {
    createSession: (username) => apiClient.post('/sessions', { username }),
    
    getSession: (sessionCode) => apiClient.get(`/sessions/${encodeURIComponent(sessionCode)}`),
    
    lockSession: (sessionCode) => apiClient.put(`/sessions/${encodeURIComponent(sessionCode)}/lock`),
};

// Restaurant API endpoints
export const restaurantAPI = {
    submitRestaurant: (sessionCode, restaurantName, submittedBy) => 
        apiClient.post(`/sessions/${encodeURIComponent(sessionCode)}/restaurants`, {
            restaurantName,
            submittedBy
        }),
    
    getRestaurants: (sessionCode) => 
        apiClient.get(`/sessions/${encodeURIComponent(sessionCode)}/restaurants`),
    
    getRandomRestaurant: (sessionCode) => 
        apiClient.get(`/sessions/${encodeURIComponent(sessionCode)}/restaurants/random`),
    
    getRestaurantCount: (sessionCode) => 
        apiClient.get(`/sessions/${encodeURIComponent(sessionCode)}/restaurants/count`),
    
    canRequestRandom: (sessionCode, username) => 
        apiClient.get(`/sessions/${encodeURIComponent(sessionCode)}/restaurants/can-request-random/${encodeURIComponent(username)}`),
};

// Utility function to handle API errors
export const handleApiError = (error) => {
    if (error.response) {
        // Server responded with error status
        const status = error.response.status;
        const message = error.response.data;
        
        switch (status) {
            case 400:
                return typeof message === 'string' ? message : 'Bad request. Please check your input.';
            case 404:
                return 'Resource not found.';
            case 409:
                return typeof message === 'string' ? message : 'Conflict occurred.';
            case 500:
                return 'Server error. Please try again later.';
            default:
                return typeof message === 'string' ? message : `Error (${status}): ${error.message}`;
        }
    } else if (error.request) {
        // Network error
        return 'Network error. Please check your connection and try again.';
    } else {
        // Other error
        return error.message || 'An unexpected error occurred.';
    }
};

export default apiClient;