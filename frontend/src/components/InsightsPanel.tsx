/**
 * Insights Panel Component
 * 
 * Displays AI-generated insights and statistics from query results.
 */

import { useMemo } from 'react';
import {
  Paper,
  Typography,
  Box,
  Grid,
  Chip,
  Divider,
  Alert,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Assessment as AssessmentIcon,
} from '@mui/icons-material';

interface InsightsPanelProps {
  columns: string[];
  rows: Record<string, any>[];
}

/**
 * Check if a value is numeric
 */
function isNumeric(value: any): boolean {
  if (value === null || value === undefined) return false;
  if (typeof value === 'number') return true;
  if (typeof value === 'string') {
    const trimmed = value.trim();
    if (trimmed === '') return false;
    return !isNaN(Number(trimmed)) && isFinite(Number(trimmed));
  }
  return false;
}

/**
 * Calculate statistics for a numeric column
 */
function calculateStats(rows: Record<string, any>[], column: string) {
  const values = rows
    .map(row => row[column])
    .filter(v => isNumeric(v))
    .map(v => Number(v));

  if (values.length === 0) {
    return null;
  }

  const sum = values.reduce((a, b) => a + b, 0);
  const avg = sum / values.length;
  const min = Math.min(...values);
  const max = Math.max(...values);
  const sorted = [...values].sort((a, b) => a - b);
  const median = sorted.length % 2 === 0
    ? (sorted[sorted.length / 2 - 1] + sorted[sorted.length / 2]) / 2
    : sorted[Math.floor(sorted.length / 2)];

  return { sum, avg, min, max, median, count: values.length };
}

export default function InsightsPanel({ columns, rows }: InsightsPanelProps) {
  const insights = useMemo(() => {
    if (rows.length === 0) {
      return {
        rowCount: 0,
        columnCount: columns.length,
        numericColumns: [],
        insights: [],
      };
    }

    // Find numeric columns
    const numericColumns = columns.filter(col => {
      const sampleValues = rows.slice(0, Math.min(10, rows.length)).map(row => row[col]);
      return sampleValues.some(v => isNumeric(v));
    });

    // Calculate statistics for each numeric column
    const columnStats = numericColumns.map(col => ({
      column: col,
      stats: calculateStats(rows, col),
    })).filter(item => item.stats !== null);

    // Generate insights
    const generatedInsights: string[] = [];

    // Row count insight
    if (rows.length === 1) {
      generatedInsights.push('Query returned a single row.');
    } else {
      generatedInsights.push(`Query returned ${rows.length} row${rows.length > 1 ? 's' : ''}.`);
    }

    // Column statistics insights
    columnStats.forEach(({ column, stats }) => {
      if (!stats) return;

      const formattedAvg = stats.avg.toFixed(2);
      const formattedMin = stats.min.toFixed(2);
      const formattedMax = stats.max.toFixed(2);
      const formattedSum = stats.sum.toLocaleString();

      generatedInsights.push(
        `**${column}**: Average = ${formattedAvg}, Min = ${formattedMin}, Max = ${formattedMax}, Total = ${formattedSum}`
      );

      // Detect outliers (values more than 2 standard deviations from mean)
      if (stats.count > 2) {
        const variance = rows
          .map(row => row[column])
          .filter(v => isNumeric(v))
          .map(v => Math.pow(Number(v) - stats.avg, 2))
          .reduce((a, b) => a + b, 0) / stats.count;
        const stdDev = Math.sqrt(variance);
        const outliers = rows.filter(row => {
          const val = Number(row[column]);
          return isNumeric(val) && Math.abs(val - stats.avg) > 2 * stdDev;
        });

        if (outliers.length > 0) {
          generatedInsights.push(
            `**${column}**: Found ${outliers.length} potential outlier${outliers.length > 1 ? 's' : ''} (values significantly different from average).`
          );
        }
      }

      // Trend detection (if sorted data)
      if (stats.count > 3) {
        const firstHalf = rows.slice(0, Math.floor(rows.length / 2))
          .map(row => Number(row[column]))
          .filter(v => isNumeric(v));
        const secondHalf = rows.slice(Math.floor(rows.length / 2))
          .map(row => Number(row[column]))
          .filter(v => isNumeric(v));

        if (firstHalf.length > 0 && secondHalf.length > 0) {
          const firstAvg = firstHalf.reduce((a, b) => a + b, 0) / firstHalf.length;
          const secondAvg = secondHalf.reduce((a, b) => a + b, 0) / secondHalf.length;
          const change = ((secondAvg - firstAvg) / firstAvg) * 100;

          if (Math.abs(change) > 5) {
            const direction = change > 0 ? 'increasing' : 'decreasing';
            generatedInsights.push(
              `**${column}**: Shows ${direction} trend (${Math.abs(change).toFixed(1)}% change).`
            );
          }
        }
      }
    });

    return {
      rowCount: rows.length,
      columnCount: columns.length,
      numericColumns: columnStats,
      insights: generatedInsights,
    };
  }, [columns, rows]);

  if (rows.length === 0) {
    return (
      <Paper sx={{ p: 2 }}>
        <Typography variant="h6" gutterBottom>
          <AssessmentIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
          Insights
        </Typography>
        <Alert severity="info">No data available for insights.</Alert>
      </Paper>
    );
  }

  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h6" gutterBottom>
        <AssessmentIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
        Insights
      </Typography>
      <Divider sx={{ my: 2 }} />

      <Box sx={{ mb: 2 }}>
        <Grid container spacing={2}>
          <Grid item xs={6} sm={4}>
            <Chip
              label={`${insights.rowCount} Row${insights.rowCount !== 1 ? 's' : ''}`}
              color="primary"
              variant="outlined"
            />
          </Grid>
          <Grid item xs={6} sm={4}>
            <Chip
              label={`${insights.columnCount} Column${insights.columnCount !== 1 ? 's' : ''}`}
              color="secondary"
              variant="outlined"
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <Chip
              label={`${insights.numericColumns.length} Numeric Column${insights.numericColumns.length !== 1 ? 's' : ''}`}
              color="success"
              variant="outlined"
            />
          </Grid>
        </Grid>
      </Box>

      {insights.numericColumns.length > 0 && (
        <>
          <Typography variant="subtitle2" gutterBottom sx={{ mt: 2, mb: 1 }}>
            Column Statistics
          </Typography>
          <Grid container spacing={2} sx={{ mb: 2 }}>
            {insights.numericColumns.map(({ column, stats }) => {
              if (!stats) return null;
              return (
                <Grid item xs={12} sm={6} key={column}>
                  <Paper variant="outlined" sx={{ p: 1.5 }}>
                    <Typography variant="body2" fontWeight="bold" gutterBottom>
                      {column}
                    </Typography>
                    <Typography variant="caption" display="block">
                      Avg: {stats.avg.toFixed(2)} | Min: {stats.min.toFixed(2)} | Max: {stats.max.toFixed(2)}
                    </Typography>
                    <Typography variant="caption" display="block" color="text.secondary">
                      Total: {stats.sum.toLocaleString()} | Median: {stats.median.toFixed(2)}
                    </Typography>
                  </Paper>
                </Grid>
              );
            })}
          </Grid>
        </>
      )}

      {insights.insights.length > 0 && (
        <>
          <Divider sx={{ my: 2 }} />
          <Typography variant="subtitle2" gutterBottom>
            Key Insights
          </Typography>
          <Box component="ul" sx={{ pl: 2, m: 0 }}>
            {insights.insights.map((insight, index) => {
              // Parse markdown-style bold
              const parts = insight.split(/\*\*(.*?)\*\*/);
              return (
                <Typography
                  key={index}
                  variant="body2"
                  component="li"
                  sx={{ mb: 1 }}
                >
                  {parts.map((part, i) => {
                    if (i % 2 === 1) {
                      return <strong key={i}>{part}</strong>;
                    }
                    return <span key={i}>{part}</span>;
                  })}
                </Typography>
              );
            })}
          </Box>
        </>
      )}
    </Paper>
  );
}


