/**
 * Chart Container Component
 * 
 * Auto-detects chart type and renders the appropriate chart component.
 */

import { useMemo } from 'react';
import { Paper, Box, Typography, Alert } from '@mui/material';
import { detectChartType, transformDataForChart, type ChartConfig } from '../../utils/chartDetection';
import BarChart from './BarChart';
import LineChart from './LineChart';
import PieChart from './PieChart';

interface ChartContainerProps {
  columns: string[];
  rows: Record<string, any>[];
  height?: number;
}

export default function ChartContainer({ columns, rows, height = 400 }: ChartContainerProps) {
  const chartConfig: ChartConfig = useMemo(() => {
    return detectChartType(columns, rows);
  }, [columns, rows]);

  const chartData = useMemo(() => {
    return transformDataForChart(rows, chartConfig);
  }, [rows, chartConfig]);

  if (chartConfig.type === 'table') {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Alert severity="info" sx={{ mb: 2 }}>
          <Typography variant="body2">
            This data is best viewed in table format. Charts are automatically generated for
            data with clear numeric and categorical relationships.
          </Typography>
        </Alert>
        <Typography variant="body2" color="text.secondary">
          To see a chart, try queries that return:
        </Typography>
        <Typography variant="body2" color="text.secondary" component="ul" sx={{ mt: 1, textAlign: 'left', display: 'inline-block' }}>
          <li>Categories with numeric values (bar chart)</li>
          <li>Time series data (line chart)</li>
          <li>Proportional data (pie chart)</li>
        </Typography>
      </Paper>
    );
  }

  return (
    <Box sx={{ width: '100%' }}>
      {chartConfig.type === 'bar' && chartConfig.xAxisKey && chartConfig.yAxisKeys && (
        <BarChart
          data={chartData}
          xAxisKey={chartConfig.xAxisKey}
          yAxisKeys={chartConfig.yAxisKeys}
          height={height}
        />
      )}
      {chartConfig.type === 'line' && chartConfig.xAxisKey && chartConfig.yAxisKeys && (
        <LineChart
          data={chartData}
          xAxisKey={chartConfig.xAxisKey}
          yAxisKeys={chartConfig.yAxisKeys}
          height={height}
        />
      )}
      {chartConfig.type === 'pie' && chartConfig.categoryKey && chartConfig.valueKey && (
        <PieChart
          data={chartData}
          categoryKey={chartConfig.categoryKey}
          valueKey={chartConfig.valueKey}
          height={height}
        />
      )}
    </Box>
  );
}


