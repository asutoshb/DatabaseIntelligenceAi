/**
 * API Service Layer
 * 
 * Centralized API client using Axios for all backend communication.
 */

import axios from 'axios';
import type {
  NLToSQLRequest,
  NLToSQLResponse,
  QueryExecutionRequest,
  QueryExecutionResponse,
  HealthResponse,
} from '../types';

// Base URL for backend API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor (for adding auth tokens in the future)
apiClient.interceptors.request.use(
  (config) => {
    // Add JWT token if available (for Chunk 7+)
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor (for error handling)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle common errors
    if (error.response) {
      // Server responded with error status
      console.error('API Error:', error.response.data);
    } else if (error.request) {
      // Request made but no response
      console.error('Network Error:', error.request);
    } else {
      // Something else happened
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

/**
 * Health Check API
 */
export const healthApi = {
  check: async (): Promise<HealthResponse> => {
    const response = await apiClient.get<HealthResponse>('/health');
    return response.data;
  },
};

/**
 * NL to SQL API
 */
export const nlToSqlApi = {
  convert: async (request: NLToSQLRequest): Promise<NLToSQLResponse> => {
    const response = await apiClient.post<NLToSQLResponse>('/nl-to-sql/convert', request);
    return response.data;
  },
};

/**
 * Query Execution API
 */
export const queryExecutionApi = {
  execute: async (request: QueryExecutionRequest): Promise<QueryExecutionResponse> => {
    const response = await apiClient.post<QueryExecutionResponse>(
      '/query-execution/execute',
      request
    );
    return response.data;
  },
};

// Export the axios instance for custom requests
export default apiClient;

