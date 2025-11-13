/**
 * Line Chart Component
 * 
 * Displays data as a line chart using Recharts.
 */

import {
  LineChart as RechartsLineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Box, Typography, Paper } from '@mui/material';

interface LineChartProps {
  data: any[];
  xAxisKey: string;
  yAxisKeys: string[];
  height?: number;
}

export default function LineChart({ data, xAxisKey, yAxisKeys, height = 400 }: LineChartProps) {
  if (data.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="body2" color="text.secondary">
          No data available for line chart
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
        <RechartsLineChart
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
            <Line
              key={key}
              type="monotone"
              dataKey={key}
              stroke={colors[index % colors.length]}
              strokeWidth={2}
              name={key}
              dot={{ r: 4 }}
              activeDot={{ r: 6 }}
            />
          ))}
        </RechartsLineChart>
      </ResponsiveContainer>
    </Box>
  );
}


