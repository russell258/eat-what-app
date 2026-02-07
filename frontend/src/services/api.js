import axios from 'axios';

// Base URL for the backend API
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Create axios instance with default config
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor for logging
apiClient.interceptors.request.use(
    (config) => {
        console.log(`[API] ${config.method.toUpperCase()} ${config.url}`, {
            data: config.data,
            params: config.params
        });
        return config;
    },
    (error) => {
        console.error('[API] Request error:', error);
        return Promise.reject(error);
    }
);

// Response interceptor to handle standardized responses and errors
apiClient.interceptors.response.use(
    (response) => {
        console.log(`[API] ${response.config.method.toUpperCase()} ${response.config.url} - Success`, {
            status: response.status,
            data: response.data
        });
        
        // Handle standardized API responses
        if (response.data && typeof response.data === 'object' && 'success' in response.data) {
            if (response.data.success) {
                return response;
            } else {
                const error = new Error(response.data.message || 'Request failed');
                error.response = {
                    ...response,
                    data: response.data
                };
                console.error(`[API] Business error: ${response.data.message}`);
                return Promise.reject(error);
            }
        }
        
        return response;
    },
    (error) => {
        console.error(`[API] ${error.config?.method?.toUpperCase()} ${error.config?.url} - Error:`, {
            status: error.response?.status,
            message: error.response?.data?.message || error.message,
            details: error.response?.data
        });
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
    console.log('[handleApiError] Processing error:', error);
    
    if (error.response) {
        // Server responded with error status
        const status = error.response.status;
        const data = error.response.data;
        
        // Handle standardized API responses
        if (data && typeof data === 'object' && 'message' in data) {
            return data.message;
        }
        
        // Handle traditional error responses
        switch (status) {
            case 400:
                return typeof data === 'string' ? data : 'Bad request. Please check your input.';
            case 404:
                return 'Resource not found.';
            case 409:
                return typeof data === 'string' ? data : 'Conflict occurred.';
            case 500:
                return 'Server error. Please try again later.';
            default:
                return typeof data === 'string' ? data : `Error (${status}): ${error.message}`;
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