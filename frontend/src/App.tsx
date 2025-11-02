import { useState, useEffect } from 'react'
import { Container, Typography, Box, Paper, Button } from '@mui/material'
import axios from 'axios'

/**
 * Main App Component
 * 
 * This is the root component of our React application.
 * It displays a welcome message and checks if the backend is running.
 * 
 * Concepts:
 * - useState: React hook to manage component state
 * - useEffect: React hook to run code when component loads
 * - axios: Library to make HTTP requests
 */
function App() {
  const [backendStatus, setBackendStatus] = useState<string>('Checking...')
  const [backendData, setBackendData] = useState<any>(null)

  /**
   * useEffect runs when the component first loads
   * We use it to check if the backend is running
   */
  useEffect(() => {
    checkBackendHealth()
  }, [])

  /**
   * Function to call our backend health check endpoint
   */
  const checkBackendHealth = async () => {
    try {
      // Make GET request to backend
      const response = await axios.get('http://localhost:8080/api/health')
      setBackendStatus('✅ Connected!')
      setBackendData(response.data)
    } catch (error) {
      setBackendStatus('❌ Backend not running')
      console.error('Backend connection failed:', error)
    }
  }

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4, textAlign: 'center' }}>
        {/* Typography: Material-UI component for text */}
        <Typography variant="h2" component="h1" gutterBottom>
          🚀 AI Database Intelligence Platform
        </Typography>
        
        <Typography variant="h5" component="h2" gutterBottom sx={{ mb: 4 }}>
          Convert Natural Language to SQL Queries
        </Typography>

        {/* Paper: Material-UI component for card-like container */}
        <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            Backend Status
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            {backendStatus}
          </Typography>
          
          {backendData && (
            <Box sx={{ mt: 2, textAlign: 'left' }}>
              <Typography variant="body2" color="text.secondary">
                <strong>Status:</strong> {backendData.status}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                <strong>Message:</strong> {backendData.message}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                <strong>Timestamp:</strong> {backendData.timestamp}
              </Typography>
            </Box>
          )}
          
          <Button 
            variant="contained" 
            onClick={checkBackendHealth}
            sx={{ mt: 2 }}
          >
            Refresh Status
          </Button>
        </Paper>

        <Typography variant="body1" color="text.secondary">
          Frontend is running! ✅
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Start the backend server to see the full connection
        </Typography>
      </Box>
    </Container>
  )
}

export default App

