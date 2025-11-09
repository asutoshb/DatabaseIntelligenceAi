/**
 * NL to SQL Page
 * 
 * Complete interface for converting natural language to SQL with:
 * - Database selection
 * - Query input
 * - Real-time progress updates
 * - Generated SQL preview
 * - Query history
 */

import { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  TextField,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Alert,
  CircularProgress,
  Chip,
  Grid,
  Card,
  CardContent,
  IconButton,
  Divider,
  LinearProgress,
} from '@mui/material';
import {
  Send as SendIcon,
  ContentCopy as CopyIcon,
  History as HistoryIcon,
} from '@mui/icons-material';
import { useWebSocket } from '../hooks/useWebSocket';
import { nlToSqlApi, databaseApi } from '../services/api';
import type {
  NLToSQLRequest,
  NLToSQLResponse,
  DatabaseInfo,
  RealTimeStatusMessage,
} from '../types';

interface QueryHistoryItem {
  id: string;
  query: string;
  sql: string;
  databaseId: number;
  databaseName: string;
  timestamp: string;
  success: boolean;
}

const QUERY_HISTORY_KEY = 'nl-to-sql-query-history';
const MAX_HISTORY_ITEMS = 20;

export default function NLToSQLPage() {
  // Form state
  const [selectedDatabaseId, setSelectedDatabaseId] = useState<number | ''>('');
  const [query, setQuery] = useState('');
  const [databases, setDatabases] = useState<DatabaseInfo[]>([]);
  const [loadingDatabases, setLoadingDatabases] = useState(true);

  // Query execution state
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [response, setResponse] = useState<NLToSQLResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [currentRequestId, setCurrentRequestId] = useState<string | null>(null);

  // Real-time progress state
  const [progressStage, setProgressStage] = useState<string>('');
  const [progressStatus, setProgressStatus] = useState<'IN_PROGRESS' | 'SUCCESS' | 'ERROR' | null>(null);
  const [progressMessage, setProgressMessage] = useState<string>('');

  // Query history
  const [queryHistory, setQueryHistory] = useState<QueryHistoryItem[]>([]);
  const [showHistory, setShowHistory] = useState(false);

  // WebSocket callback for NL to SQL updates
  const handleNlToSqlUpdate = useCallback((message: RealTimeStatusMessage) => {
    // Only process updates for the current request
    if (currentRequestId && message.requestId === currentRequestId) {
      setProgressStage(message.stage);
      setProgressStatus(message.status);
      setProgressMessage(message.message);

      // If successful, the response should already be set from the API call
      if (message.status === 'SUCCESS' && message.data && 'sqlQuery' in message.data) {
        // Update response if we got SQL from WebSocket
        setResponse((prev) => {
          if (prev) {
            return { ...prev, sqlQuery: message.data?.sqlQuery as string };
          }
          return null;
        });
      }

      // If error, show error message
      if (message.status === 'ERROR') {
        setError(message.message);
        setIsSubmitting(false);
      }
    }
  }, [currentRequestId]);

  // Initialize WebSocket connection
  useWebSocket({
    onNlToSqlUpdate: handleNlToSqlUpdate,
  });

  // Load databases on mount
  useEffect(() => {
    const loadDatabases = async () => {
      try {
        setLoadingDatabases(true);
        setError(null); // Clear any previous errors
        const dbList = await databaseApi.getAll();
        setDatabases(dbList);
        if (dbList.length > 0 && !selectedDatabaseId) {
          setSelectedDatabaseId(dbList[0].id);
        } else if (dbList.length === 0) {
          setError('No databases available. Please register a database in Settings first.');
        }
      } catch (err: any) {
        console.error('Failed to load databases:', err);
        const errorMessage = err.response?.status === 401 
          ? 'Authentication required. Please login first.'
          : err.response?.status === 404
          ? 'Databases endpoint not found. Please check backend configuration.'
          : err.response?.data?.message || err.message || 'Failed to load databases. Please try again.';
        setError(errorMessage);
      } finally {
        setLoadingDatabases(false);
      }
    };

    loadDatabases();
  }, []);

  // Load query history from localStorage
  useEffect(() => {
    try {
      const stored = localStorage.getItem(QUERY_HISTORY_KEY);
      if (stored) {
        setQueryHistory(JSON.parse(stored));
      }
    } catch (err) {
      console.error('Failed to load query history:', err);
    }
  }, []);

  // Save query to history
  const saveToHistory = useCallback((item: QueryHistoryItem) => {
    setQueryHistory((prev) => {
      const updated = [item, ...prev].slice(0, MAX_HISTORY_ITEMS);
      localStorage.setItem(QUERY_HISTORY_KEY, JSON.stringify(updated));
      return updated;
    });
  }, []);

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!selectedDatabaseId || !query.trim()) {
      setError('Please select a database and enter a query');
      return;
    }

    // Reset state
    setIsSubmitting(true);
    setError(null);
    setResponse(null);
    setProgressStage('');
    setProgressStatus(null);
    setProgressMessage('');

    // Generate request ID
    const requestId = `nl-to-sql-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    setCurrentRequestId(requestId);

    try {
      const request: NLToSQLRequest = {
        databaseInfoId: selectedDatabaseId as number,
        naturalLanguageQuery: query.trim(),
        topK: 5,
        clientRequestId: requestId,
      };

      // Make API call
      const result = await nlToSqlApi.convert(request);
      setResponse(result);
      setIsSubmitting(false);
      setProgressStatus('SUCCESS');
      setProgressMessage('SQL generated successfully!');

      // Save to history
      const selectedDb = databases.find((db) => db.id === selectedDatabaseId);
      saveToHistory({
        id: requestId,
        query: query.trim(),
        sql: result.sqlQuery,
        databaseId: selectedDatabaseId as number,
        databaseName: selectedDb?.name || 'Unknown',
        timestamp: new Date().toISOString(),
        success: result.isValid,
      });
    } catch (err: any) {
      console.error('Failed to convert query:', err);
      setError(err.response?.data?.message || err.message || 'Failed to convert query. Please try again.');
      setIsSubmitting(false);
      setProgressStatus('ERROR');
      setProgressMessage('Failed to generate SQL');
    }
  };

  // Copy SQL to clipboard
  const handleCopySQL = () => {
    if (response?.sqlQuery) {
      navigator.clipboard.writeText(response.sqlQuery);
      // You could add a toast notification here
    }
  };

  // Load query from history
  const loadFromHistory = (item: QueryHistoryItem) => {
    setQuery(item.query);
    setSelectedDatabaseId(item.databaseId);
    setShowHistory(false);
  };


  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Natural Language to SQL
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Convert your natural language questions into SQL queries using AI.
        </Typography>

        <Grid container spacing={3}>
          {/* Main Content */}
          <Grid item xs={12} md={showHistory ? 8 : 12}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <form onSubmit={handleSubmit}>
                <Grid container spacing={3}>
                  {/* Database Selection */}
                  <Grid item xs={12}>
                    <FormControl fullWidth>
                      <InputLabel id="database-select-label">Select Database</InputLabel>
                      <Select
                        labelId="database-select-label"
                        id="database-select"
                        value={selectedDatabaseId}
                        label="Select Database"
                        onChange={(e) => setSelectedDatabaseId(e.target.value as number)}
                        disabled={isSubmitting || loadingDatabases}
                      >
                        {loadingDatabases ? (
                          <MenuItem disabled>
                            <CircularProgress size={20} sx={{ mr: 1 }} />
                            Loading databases...
                          </MenuItem>
                        ) : databases.length === 0 ? (
                          <MenuItem disabled>No databases available</MenuItem>
                        ) : (
                          databases.map((db) => (
                            <MenuItem key={db.id} value={db.id}>
                              {db.name}
                            </MenuItem>
                          ))
                        )}
                      </Select>
                    </FormControl>
                  </Grid>

                  {/* Query Input */}
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      multiline
                      rows={6}
                      label="Enter your question in natural language"
                      placeholder="Example: Show me the top 5 customers by revenue"
                      value={query}
                      onChange={(e) => setQuery(e.target.value)}
                      disabled={isSubmitting}
                      helperText={`${query.length} characters`}
                      required
                    />
                  </Grid>

                  {/* Progress Indicator */}
                  {isSubmitting && (
                    <Grid item xs={12}>
                      <Box sx={{ mb: 2 }}>
                        {progressStatus === 'IN_PROGRESS' && (
                          <LinearProgress sx={{ mb: 1 }} />
                        )}
                        {progressStage && (
                          <Typography variant="body2" color="text.secondary">
                            {progressStage}: {progressMessage || 'Processing...'}
                          </Typography>
                        )}
                      </Box>
                    </Grid>
                  )}

                  {/* Error Display */}
                  {error && (
                    <Grid item xs={12}>
                      <Alert severity="error" onClose={() => setError(null)}>
                        {error}
                      </Alert>
                    </Grid>
                  )}

                  {/* Submit Button */}
                  <Grid item xs={12}>
                    <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                      <Button
                        type="submit"
                        variant="contained"
                        size="large"
                        startIcon={isSubmitting ? <CircularProgress size={20} /> : <SendIcon />}
                        disabled={isSubmitting || !selectedDatabaseId || !query.trim()}
                      >
                        {isSubmitting ? 'Generating SQL...' : 'Convert to SQL'}
                      </Button>
                      <Button
                        variant="outlined"
                        startIcon={<HistoryIcon />}
                        onClick={() => setShowHistory(!showHistory)}
                      >
                        {showHistory ? 'Hide' : 'Show'} History
                      </Button>
                    </Box>
                  </Grid>
                </Grid>
              </form>

              {/* Generated SQL Preview */}
              {response && (
                <Box sx={{ mt: 4 }}>
                  <Divider sx={{ mb: 2 }} />
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6">Generated SQL</Typography>
                    <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                      <Chip
                        label={response.isValid ? 'Valid SQL' : 'Invalid SQL'}
                        color={response.isValid ? 'success' : 'error'}
                        size="small"
                      />
                      <IconButton size="small" onClick={handleCopySQL} title="Copy SQL">
                        <CopyIcon />
                      </IconButton>
                    </Box>
                  </Box>

                  <Paper
                    variant="outlined"
                    sx={{
                      p: 2,
                      bgcolor: 'grey.50',
                      fontFamily: 'monospace',
                      fontSize: '0.875rem',
                      whiteSpace: 'pre-wrap',
                      wordBreak: 'break-word',
                      maxHeight: '300px',
                      overflow: 'auto',
                    }}
                  >
                    {response.sqlQuery || 'No SQL generated'}
                  </Paper>

                  {/* Validation Errors */}
                  {response.validationErrors && response.validationErrors.length > 0 && (
                    <Alert severity="warning" sx={{ mt: 2 }}>
                      <Typography variant="subtitle2" gutterBottom>
                        Validation Errors:
                      </Typography>
                      <ul style={{ margin: 0, paddingLeft: 20 }}>
                        {response.validationErrors.map((err, idx) => (
                          <li key={idx}>{err}</li>
                        ))}
                      </ul>
                    </Alert>
                  )}

                  {/* Explanation */}
                  {response.explanation && (
                    <Alert severity="info" sx={{ mt: 2 }}>
                      <Typography variant="subtitle2" gutterBottom>
                        Explanation:
                      </Typography>
                      {response.explanation}
                    </Alert>
                  )}

                  {/* Relevant Schemas */}
                  {response.relevantSchemas && response.relevantSchemas.length > 0 && (
                    <Box sx={{ mt: 2 }}>
                      <Typography variant="subtitle2" gutterBottom>
                        Relevant Schemas Used:
                      </Typography>
                      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                        {response.relevantSchemas.map((schema, idx) => (
                          <Chip
                            key={idx}
                            label={schema.schemaName || 'Unknown Schema'}
                            size="small"
                            variant="outlined"
                            title={schema.schemaDescription || ''}
                          />
                        ))}
                      </Box>
                    </Box>
                  )}
                </Box>
              )}
            </Paper>
          </Grid>

          {/* Query History Sidebar */}
          {showHistory && (
            <Grid item xs={12} md={4}>
              <Paper elevation={3} sx={{ p: 2, maxHeight: '600px', overflow: 'auto' }}>
                <Typography variant="h6" gutterBottom>
                  Query History
                </Typography>
                <Divider sx={{ mb: 2 }} />
                {queryHistory.length === 0 ? (
                  <Typography variant="body2" color="text.secondary">
                    No query history yet. Your queries will appear here.
                  </Typography>
                ) : (
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                    {queryHistory.map((item) => (
                      <Card
                        key={item.id}
                        variant="outlined"
                        sx={{
                          cursor: 'pointer',
                          '&:hover': { bgcolor: 'action.hover' },
                        }}
                        onClick={() => loadFromHistory(item)}
                      >
                        <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
                          <Typography variant="body2" color="text.secondary" gutterBottom>
                            {new Date(item.timestamp).toLocaleString()}
                          </Typography>
                          <Typography variant="body2" sx={{ mb: 1, fontWeight: 'medium' }}>
                            {item.query}
                          </Typography>
                          <Typography
                            variant="caption"
                            sx={{
                              fontFamily: 'monospace',
                              fontSize: '0.75rem',
                              color: 'text.secondary',
                              display: 'block',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              whiteSpace: 'nowrap',
                            }}
                          >
                            {item.sql}
                          </Typography>
                          <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
                            <Chip
                              label={item.databaseName}
                              size="small"
                              variant="outlined"
                            />
                            <Chip
                              label={item.success ? 'Valid' : 'Invalid'}
                              size="small"
                              color={item.success ? 'success' : 'error'}
                            />
                          </Box>
                        </CardContent>
                      </Card>
                    ))}
                  </Box>
                )}
              </Paper>
            </Grid>
          )}
        </Grid>
      </Box>
    </Container>
  );
}
