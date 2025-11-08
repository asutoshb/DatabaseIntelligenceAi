/**
 * NL to SQL Page
 * 
 * Interface for converting natural language to SQL.
 * This will be fully implemented in Chunk 10.
 */

import {
  Container,
  Typography,
  Box,
  Paper,
  Alert,
} from '@mui/material';

export default function NLToSQLPage() {
  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Natural Language to SQL
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Convert your natural language questions into SQL queries using AI.
        </Typography>

        <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
          <Alert severity="info">
            This feature will be fully implemented in Chunk 10. The interface will include:
            <ul>
              <li>Natural language input field</li>
              <li>Database selection</li>
              <li>Real-time progress updates via WebSocket</li>
              <li>Generated SQL preview</li>
              <li>Query execution</li>
            </ul>
          </Alert>
        </Paper>
      </Box>
    </Container>
  );
}

