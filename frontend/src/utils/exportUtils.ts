/**
 * Export Utilities
 * 
 * Functions to export query results as CSV or JSON files.
 */

/**
 * Convert a value to a CSV-safe string
 */
function escapeCSVValue(value: any): string {
  if (value === null || value === undefined) {
    return '';
  }
  
  const stringValue = String(value);
  
  // If contains comma, quote, or newline, wrap in quotes and escape quotes
  if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
    return `"${stringValue.replace(/"/g, '""')}"`;
  }
  
  return stringValue;
}

/**
 * Convert array of objects to CSV string
 */
function convertToCSV(columns: string[], rows: Record<string, any>[]): string {
  // Header row
  const header = columns.map(escapeCSVValue).join(',');
  
  // Data rows
  const dataRows = rows.map(row => {
    return columns.map(column => escapeCSVValue(row[column])).join(',');
  });
  
  return [header, ...dataRows].join('\n');
}

/**
 * Download a file with the given content and filename
 */
function downloadFile(content: string, filename: string, mimeType: string): void {
  const blob = new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

/**
 * Export query results as CSV file
 */
export function exportToCSV(
  columns: string[],
  rows: Record<string, any>[],
  filename?: string
): void {
  const csvContent = convertToCSV(columns, rows);
  const defaultFilename = `query-results-${new Date().toISOString().split('T')[0]}.csv`;
  downloadFile(csvContent, filename || defaultFilename, 'text/csv;charset=utf-8;');
}

/**
 * Export query results as JSON file
 */
export function exportToJSON(
  columns: string[],
  rows: Record<string, any>[],
  filename?: string
): void {
  // Convert to array of objects with consistent column order
  const jsonData = rows.map(row => {
    const orderedRow: Record<string, any> = {};
    columns.forEach(column => {
      orderedRow[column] = row[column];
    });
    return orderedRow;
  });
  
  const jsonContent = JSON.stringify(jsonData, null, 2);
  const defaultFilename = `query-results-${new Date().toISOString().split('T')[0]}.json`;
  downloadFile(jsonContent, filename || defaultFilename, 'application/json');
}


