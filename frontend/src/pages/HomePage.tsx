/**
 * Home Page
 * 
 * Dashboard/home page with overview and quick actions.
 */

import {
  Container,
  Typography,
  Box,
  Paper,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { healthApi } from '../services/api';
import { useEffect, useState } from 'react';
import type { HealthResponse } from '../types';
import { useWebSocket } from '../hooks/useWebSocket';
import { Chip } from '@mui/material';

export default function HomePage() {
  const navigate = useNavigate();
  const [healthStatus, setHealthStatus] = useState<HealthResponse | null>(null);
  const [loading, setLoading] = useState(true);
  
  // WebSocket connection status
  const { isConnected } = useWebSocket({
    // Callbacks are optional - connection status is shown in UI
  });

  useEffect(() => {
    checkBackendHealth();
  }, []);

  const checkBackendHealth = async () => {
    try {
      const health = await healthApi.check();
      setHealthStatus(health);
    } catch (error) {
      console.error('Backend health check failed:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Welcome to AI Database Intelligence Platform
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Convert natural language queries to SQL and execute them with real-time progress updates.
        </Typography>

        {/* Connection Status Cards */}
        <Grid container spacing={2} sx={{ mb: 4 }}>
          {/* Backend Status Card */}
          <Grid item xs={12} md={6}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" sx={{ flexGrow: 1 }}>
                  Backend Status
                </Typography>
                {healthStatus && (
                  <Chip
                    label={healthStatus.status}
                    color={healthStatus.status === 'UP' ? 'success' : 'error'}
                    size="small"
                  />
                )}
              </Box>
              {loading ? (
                <Typography>Checking...</Typography>
              ) : healthStatus ? (
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Message:</strong> {healthStatus.message}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    <strong>Timestamp:</strong> {healthStatus.timestamp}
                  </Typography>
                </Box>
              ) : (
                <Typography color="error">❌ Backend not connected</Typography>
              )}
              <Button
                variant="outlined"
                onClick={checkBackendHealth}
                sx={{ mt: 2 }}
                size="small"
              >
                Refresh Status
              </Button>
            </Paper>
          </Grid>

          {/* WebSocket Status Card */}
          <Grid item xs={12} md={6}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" sx={{ flexGrow: 1 }}>
                  WebSocket Status
                </Typography>
                <Chip
                  label={isConnected ? 'Connected' : 'Disconnected'}
                  color={isConnected ? 'success' : 'default'}
                  size="small"
                />
              </Box>
              <Typography variant="body2" color="text.secondary">
                {isConnected
                  ? '✅ Real-time updates are active. You will receive progress updates here.'
                  : '⏳ Connecting to WebSocket server...'}
              </Typography>
              <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                {isConnected
                  ? 'Listening to: /topic/nl-to-sql, /topic/query-execution'
                  : 'Make sure backend is running on port 8080'}
              </Typography>
            </Paper>
          </Grid>
        </Grid>

        {/* Quick Actions */}
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6} md={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  NL to SQL
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Convert natural language queries to SQL using AI
                </Typography>
              </CardContent>
              <CardActions>
                <Button size="small" onClick={() => navigate('/nl-to-sql')}>
                  Get Started
                </Button>
              </CardActions>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Query History
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  View your past queries and results
                </Typography>
              </CardContent>
              <CardActions>
                <Button size="small" onClick={() => navigate('/history')}>
                  View History
                </Button>
              </CardActions>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Settings
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Configure your database connections
                </Typography>
              </CardContent>
              <CardActions>
                <Button size="small" onClick={() => navigate('/settings')}>
                  Open Settings
                </Button>
              </CardActions>
            </Card>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
}

