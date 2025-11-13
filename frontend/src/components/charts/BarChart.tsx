/**
 * Bar Chart Component
 * 
 * Displays data as a bar chart using Recharts.
 */

import {
  BarChart as RechartsBarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Box, Typography, Paper } from '@mui/material';

interface BarChartProps {
  data: any[];
  xAxisKey: string;
  yAxisKeys: string[];
  height?: number;
}

export default function BarChart({ data, xAxisKey, yAxisKeys, height = 400 }: BarChartProps) {
  if (data.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="body2" color="text.secondary">
          No data available for bar chart
        </Typography>
      </Paper>
    );
  }

  const colors = [
    '#1976d2', // Blue
    '#dc004e', // Red
    '#ed6c02', // Orange
    '#2e7d32', // Green
    '#9c27b0', // Purple
    '#0288d1', // Light Blue
    '#d32f2f', // Dark Red
    '#f57c00', // Dark Orange
  ];

  return (
    <Box sx={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <RechartsBarChart
          data={data}
          margin={{ top: 20, right: 30, left: 20, bottom: 60 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey={xAxisKey}
            angle={-45}
            textAnchor="end"
            height={100}
            interval={0}
            tick={{ fontSize: 12 }}
          />
          <YAxis />
          <Tooltip />
          <Legend />
          {yAxisKeys.map((key, index) => (
            <Bar
              key={key}
              dataKey={key}
              fill={colors[index % colors.length]}
              name={key}
            />
          ))}
        </RechartsBarChart>
      </ResponsiveContainer>
    </Box>
  );
}


