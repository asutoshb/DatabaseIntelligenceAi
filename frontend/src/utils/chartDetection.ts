/**
 * Chart Detection Utilities
 * 
 * Analyzes query results to determine the most appropriate chart type.
 */

export type ChartType = 'bar' | 'line' | 'pie' | 'table';

export interface ChartConfig {
  type: ChartType;
  xAxisKey?: string;
  yAxisKeys?: string[];
  dataKey?: string;
  nameKey?: string;
  valueKey?: string;
  categoryKey?: string;
}

/**
 * Check if a value looks like a date
 */
function isDateLike(value: any): boolean {
  if (typeof value === 'string') {
    // Check common date patterns
    const datePatterns = [
      /^\d{4}-\d{2}-\d{2}/, // YYYY-MM-DD
      /^\d{2}\/\d{2}\/\d{4}/, // MM/DD/YYYY
      /^\d{4}\/\d{2}\/\d{2}/, // YYYY/MM/DD
      /^\w{3}\s+\d{1,2},?\s+\d{4}/, // Mon DD, YYYY
    ];
    return datePatterns.some(pattern => pattern.test(value));
  }
  return value instanceof Date;
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
 * Analyze columns to determine their types
 */
function analyzeColumns(columns: string[], rows: Record<string, any>[]): {
  numericColumns: string[];
  dateColumns: string[];
  categoricalColumns: string[];
} {
  const numericColumns: string[] = [];
  const dateColumns: string[] = [];
  const categoricalColumns: string[] = [];

  if (rows.length === 0) {
    return { numericColumns, dateColumns, categoricalColumns };
  }

  for (const column of columns) {
    const sampleValues = rows.slice(0, Math.min(10, rows.length)).map(row => row[column]);
    const hasNumeric = sampleValues.some(v => isNumeric(v));
    const hasDate = sampleValues.some(v => isDateLike(v));
    const hasNonNumeric = sampleValues.some(v => !isNumeric(v) && v !== null && v !== undefined);

    if (hasDate) {
      dateColumns.push(column);
    } else if (hasNumeric && !hasNonNumeric) {
      numericColumns.push(column);
    } else {
      categoricalColumns.push(column);
    }
  }

  return { numericColumns, dateColumns, categoricalColumns };
}

/**
 * Detect the most appropriate chart type for the given data
 */
export function detectChartType(
  columns: string[],
  rows: Record<string, any>[]
): ChartConfig {
  // If no data, default to table
  if (rows.length === 0 || columns.length === 0) {
    return { type: 'table' };
  }

  // If too many rows, table is better
  if (rows.length > 100) {
    return { type: 'table' };
  }

  const { numericColumns, dateColumns, categoricalColumns } = analyzeColumns(columns, rows);

  // Line chart: date column + numeric columns
  if (dateColumns.length > 0 && numericColumns.length > 0) {
    return {
      type: 'line',
      xAxisKey: dateColumns[0],
      yAxisKeys: numericColumns,
    };
  }

  // Pie chart: 1 categorical + 1 numeric (best for small datasets)
  if (categoricalColumns.length === 1 && numericColumns.length === 1 && rows.length <= 20) {
    return {
      type: 'pie',
      categoryKey: categoricalColumns[0],
      valueKey: numericColumns[0],
    };
  }

  // Bar chart: categorical + numeric columns
  if (categoricalColumns.length > 0 && numericColumns.length > 0) {
    // Use first categorical as x-axis, first numeric as y-axis
    return {
      type: 'bar',
      xAxisKey: categoricalColumns[0],
      yAxisKeys: numericColumns,
    };
  }

  // If we have numeric columns but no clear categorical/date, try bar chart with index
  if (numericColumns.length > 0 && categoricalColumns.length === 0 && dateColumns.length === 0) {
    return {
      type: 'bar',
      xAxisKey: columns[0], // Use first column as x-axis
      yAxisKeys: numericColumns,
    };
  }

  // Default to table for complex or non-chartable data
  return { type: 'table' };
}

/**
 * Transform data for chart display
 */
export function transformDataForChart(
  rows: Record<string, any>[],
  config: ChartConfig
): any[] {
  if (config.type === 'table' || rows.length === 0) {
    return rows;
  }

  if (config.type === 'pie' && config.categoryKey && config.valueKey) {
    return rows.map(row => ({
      name: String(row[config.categoryKey!] || 'Unknown'),
      value: isNumeric(row[config.valueKey!]) ? Number(row[config.valueKey!]) : 0,
    }));
  }

  if (config.type === 'line' && config.xAxisKey && config.yAxisKeys) {
    return rows.map(row => {
      const transformed: any = {
        [config.xAxisKey!]: row[config.xAxisKey!],
      };
      config.yAxisKeys!.forEach(key => {
        transformed[key] = isNumeric(row[key]) ? Number(row[key]) : 0;
      });
      return transformed;
    });
  }

  if (config.type === 'bar' && config.xAxisKey && config.yAxisKeys) {
    return rows.map(row => {
      const transformed: any = {
        [config.xAxisKey!]: String(row[config.xAxisKey!] || 'Unknown'),
      };
      config.yAxisKeys!.forEach(key => {
        transformed[key] = isNumeric(row[key]) ? Number(row[key]) : 0;
      });
      return transformed;
    });
  }

  return rows;
}


