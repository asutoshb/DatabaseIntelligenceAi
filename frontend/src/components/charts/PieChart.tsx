/**
 * Pie Chart Component
 * 
 * Displays data as a pie chart using Recharts.
 */

import {
  PieChart as RechartsPieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Box, Typography, Paper } from '@mui/material';

interface PieChartProps {
  data: any[];
  categoryKey: string;
  valueKey: string;
  height?: number;
}

const COLORS = [
  '#1976d2', // Blue
  '#dc004e', // Red
  '#ed6c02', // Orange
  '#2e7d32', // Green
  '#9c27b0', // Purple
  '#0288d1', // Light Blue
  '#d32f2f', // Dark Red
  '#f57c00', // Dark Orange
  '#7b1fa2', // Deep Purple
  '#c2185b', // Pink
];

export default function PieChart({ data, categoryKey, valueKey, height = 400 }: PieChartProps) {
  if (data.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="body2" color="text.secondary">
          No data available for pie chart
        </Typography>
      </Paper>
    );
  }

  // Transform data for pie chart
  const pieData = data.map((item, index) => ({
    name: String(item.name || item[categoryKey] || `Item ${index + 1}`),
    value: Number(item.value || item[valueKey] || 0),
  }));

  return (
    <Box sx={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <RechartsPieChart>
          <Pie
            data={pieData}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percent }) => `${name}: ${((percent || 0) * 100).toFixed(0)}%`}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
          >
            {pieData.map((_entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </RechartsPieChart>
      </ResponsiveContainer>
    </Box>
  );
}


