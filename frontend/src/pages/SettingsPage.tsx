/**
 * Settings Page
 * 
 * Application settings and database connection management.
 */

import {
  Container,
  Typography,
  Box,
  Paper,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  CardActions,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
  MenuItem,
  Chip,
} from '@mui/material';
import { Add as AddIcon, Delete as DeleteIcon, Edit as EditIcon } from '@mui/icons-material';
import { useState, useEffect } from 'react';
import { databaseApi } from '../services/api';
import type { DatabaseInfo } from '../types';

export default function SettingsPage() {
  const [databases, setDatabases] = useState<DatabaseInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingDatabase, setEditingDatabase] = useState<DatabaseInfo | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    databaseType: 'PostgreSQL',
    host: '',
    port: 5432,
    databaseName: '',
    username: '',
    password: '',
  });

  // Load databases on mount
  useEffect(() => {
    loadDatabases();
  }, []);

  const loadDatabases = async () => {
    try {
      setLoading(true);
      setError(null);
      const dbList = await databaseApi.getAll();
      setDatabases(dbList);
    } catch (err: any) {
      console.error('Failed to load databases:', err);
      setError(err.response?.data?.message || err.message || 'Failed to load databases');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDialog = (database?: DatabaseInfo) => {
    if (database) {
      setEditingDatabase(database);
      setFormData({
        name: database.name,
        databaseType: (database as any).databaseType || 'PostgreSQL',
        host: database.host,
        port: database.port,
        databaseName: database.databaseName,
        username: database.username,
        password: '', // Don't show existing password for security
      });
    } else {
      setEditingDatabase(null);
      setFormData({
        name: '',
        databaseType: 'PostgreSQL',
        host: '',
        port: 5432,
        databaseName: '',
        username: '',
        password: '',
      });
    }
    setDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
    setEditingDatabase(null);
    setFormData({
      name: '',
      databaseType: 'PostgreSQL',
      host: '',
      port: 5432,
      databaseName: '',
      username: '',
      password: '',
    });
  };

  const handleSubmit = async () => {
    try {
      setError(null);
      setSuccess(null);

      // Prepare data - include password if provided
      // For updates: if password is empty but database has password, don't send it (keep existing)
      // For updates: if password is empty and database has NO password, send empty string to clear
      // For creates: send password if provided
      const dataToSend: any = {
        name: formData.name,
        databaseType: formData.databaseType,
        host: formData.host,
        port: formData.port,
        databaseName: formData.databaseName,
        username: formData.username,
      };
      
      // Always include password field if it has a value
      // For updates without password: don't include it (keeps existing)
      // For updates with password: include it (updates password)
      // For creates: include it if provided
      if (formData.password && formData.password.trim() !== '') {
        dataToSend.password = formData.password.trim();
      } else if (!editingDatabase) {
        // For new databases, send empty string if no password
        dataToSend.password = '';
      }
      // For updates: if password is empty, don't include it (keeps existing password)
      
      console.log('Sending database data:', { ...dataToSend, password: dataToSend.password ? '***' : 'NOT INCLUDED' });

      if (editingDatabase) {
        // Update existing database
        await databaseApi.update(editingDatabase.id, dataToSend);
        setSuccess('Database updated successfully!');
      } else {
        // Create new database
        await databaseApi.create(dataToSend);
        setSuccess('Database added successfully!');
      }

      handleCloseDialog();
      await loadDatabases();
    } catch (err: any) {
      console.error('Failed to save database:', err);
      setError(err.response?.data?.message || err.message || 'Failed to save database');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this database?')) {
      return;
    }

    try {
      setError(null);
      setSuccess(null);
      await databaseApi.delete(id);
      setSuccess('Database deleted successfully!');
      await loadDatabases();
    } catch (err: any) {
      console.error('Failed to delete database:', err);
      setError(err.response?.data?.message || err.message || 'Failed to delete database');
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">
            Settings
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => handleOpenDialog()}
          >
            Add Database
          </Button>
        </Box>

        <Typography variant="body1" color="text.secondary" paragraph>
          Manage your database connections. Add databases to query them using natural language.
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
            {success}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
            <CircularProgress />
          </Box>
        ) : databases.length === 0 ? (
          <Paper elevation={3} sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h6" gutterBottom>
              No databases registered
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Click "Add Database" to register your first database connection.
            </Typography>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => handleOpenDialog()}
              sx={{ mt: 2 }}
            >
              Add Database
            </Button>
          </Paper>
        ) : (
          <Grid container spacing={3}>
            {databases.map((database) => (
              <Grid item xs={12} sm={6} md={4} key={database.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                      <Typography variant="h6" component="h2">
                        {database.name}
                      </Typography>
                      <Chip
                        label={(database as any).databaseType || 'PostgreSQL'}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    </Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      <strong>Host:</strong> {database.host}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      <strong>Port:</strong> {database.port}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      <strong>Database:</strong> {database.databaseName}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      <strong>User:</strong> {database.username}
                    </Typography>
                  </CardContent>
                  <CardActions>
                    <IconButton
                      size="small"
                      onClick={() => handleOpenDialog(database)}
                      color="primary"
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDelete(database.id)}
                      color="error"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}

        {/* Add/Edit Database Dialog */}
        <Dialog open={dialogOpen} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
          <DialogTitle>
            {editingDatabase ? 'Edit Database' : 'Add Database'}
          </DialogTitle>
          <DialogContent>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
              <TextField
                label="Database Name"
                fullWidth
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="e.g., Production Database"
              />
              <TextField
                label="Database Type"
                select
                fullWidth
                required
                value={formData.databaseType}
                onChange={(e) => setFormData({ ...formData, databaseType: e.target.value })}
              >
                <MenuItem value="PostgreSQL">PostgreSQL</MenuItem>
                <MenuItem value="MySQL">MySQL</MenuItem>
                <MenuItem value="Oracle">Oracle</MenuItem>
                <MenuItem value="SQL Server">SQL Server</MenuItem>
              </TextField>
              <TextField
                label="Host"
                fullWidth
                required
                value={formData.host}
                onChange={(e) => setFormData({ ...formData, host: e.target.value })}
                placeholder="e.g., localhost or db.example.com"
              />
              <TextField
                label="Port"
                type="number"
                fullWidth
                required
                value={formData.port}
                onChange={(e) => setFormData({ ...formData, port: parseInt(e.target.value) || 5432 })}
              />
              <TextField
                label="Database Name"
                fullWidth
                required
                value={formData.databaseName}
                onChange={(e) => setFormData({ ...formData, databaseName: e.target.value })}
                placeholder="e.g., mydb"
              />
              <TextField
                label="Username"
                fullWidth
                required
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                placeholder="e.g., postgres"
              />
              <TextField
                label={editingDatabase && !(editingDatabase as any).hasPassword 
                  ? "Password (REQUIRED for Render)" 
                  : "Password (Optional)"}
                type="password"
                fullWidth
                required={!!(editingDatabase && !(editingDatabase as any).hasPassword)}
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder={editingDatabase && (editingDatabase as any).hasPassword 
                  ? "Enter new password to update, or leave empty to keep existing" 
                  : editingDatabase && !(editingDatabase as any).hasPassword
                  ? "⚠️ Password is REQUIRED - Enter password from Render connection string"
                  : "Leave empty if database doesn't require password"}
                helperText={
                  editingDatabase && (editingDatabase as any).hasPassword
                    ? "Password is already set. Enter a new password to update it, or leave empty to keep existing."
                    : editingDatabase && !(editingDatabase as any).hasPassword
                    ? "⚠️ No password set! This will cause authentication errors. Get password from Render → Database → Connect → Internal Database URL"
                    : "Optional - Only needed if your database requires authentication. For Render databases, get it from Render → Database → Connect → Internal Database URL"
                }
              />
              {editingDatabase && (editingDatabase as any).hasPassword && (
                <Alert severity="success" sx={{ mt: 1 }}>
                  ✓ Password is configured for this database.
                </Alert>
              )}
              {editingDatabase && !(editingDatabase as any).hasPassword && (
                <Alert severity="error" sx={{ mt: 1 }}>
                  ⚠️ <strong>Password is NOT set!</strong> You must enter a password for Render databases. Get it from Render → Database → Connect → Internal Database URL. Extract the password from the connection string (between username: and @).
                </Alert>
              )}
              {!editingDatabase && (
                <Alert severity="info" sx={{ mt: 1 }}>
                  Password is optional. Only required if your database needs authentication (e.g., Render, cloud databases).
                </Alert>
              )}
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Cancel</Button>
            <Button
              onClick={handleSubmit}
              variant="contained"
              disabled={
                !formData.name || 
                !formData.host || 
                !formData.databaseName || 
                !formData.username ||
                !!(editingDatabase && !(editingDatabase as any).hasPassword && !formData.password)
              }
            >
              {editingDatabase ? 'Update' : 'Add'}
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Container>
  );
}
