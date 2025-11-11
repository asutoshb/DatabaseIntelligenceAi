/**
 * Results Table Component
 * 
 * Displays query execution results in a sortable, paginated table.
 * Handles different data types and large result sets efficiently.
 */

import { useState, useMemo } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  Paper,
  Box,
  Typography,
  Chip,
  Pagination,
  CircularProgress,
} from '@mui/material';

interface ResultsTableProps {
  columns: string[];
  rows: Record<string, any>[];
  loading?: boolean;
  rowCount?: number;
  executionTimeMs?: number;
}

type SortDirection = 'asc' | 'desc' | false;

/**
 * Format a value for display based on its type
 */
function formatValue(value: any): string {
  if (value === null || value === undefined) {
    return 'NULL';
  }
  
  if (typeof value === 'boolean') {
    return value ? 'true' : 'false';
  }
  
  if (value instanceof Date) {
    return value.toLocaleString();
  }
  
  if (typeof value === 'object') {
    return JSON.stringify(value);
  }
  
  return String(value);
}

export default function ResultsTable({
  columns,
  rows,
  loading = false,
  rowCount,
  executionTimeMs,
}: ResultsTableProps) {
  const [sortColumn, setSortColumn] = useState<string | null>(null);
  const [sortDirection, setSortDirection] = useState<SortDirection>('asc');
  const [page, setPage] = useState(1);
  const rowsPerPage = 50;

  // Handle column sorting
  const handleSort = (column: string) => {
    if (sortColumn === column) {
      // Toggle direction if same column
      setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      // New column, default to ascending
      setSortColumn(column);
      setSortDirection('asc');
    }
  };

  // Sort and paginate rows
  const sortedAndPaginatedRows = useMemo(() => {
    let sorted = [...rows];

    // Apply sorting
    if (sortColumn) {
      sorted.sort((a, b) => {
        const aVal = a[sortColumn];
        const bVal = b[sortColumn];

        // Handle null/undefined
        if (aVal === null || aVal === undefined) return 1;
        if (bVal === null || bVal === undefined) return -1;

        // Compare values
        let comparison = 0;
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          comparison = aVal - bVal;
        } else {
          comparison = String(aVal).localeCompare(String(bVal));
        }

        return sortDirection === 'asc' ? comparison : -comparison;
      });
    }

    // Apply pagination
    const startIndex = (page - 1) * rowsPerPage;
    const endIndex = startIndex + rowsPerPage;
    return sorted.slice(startIndex, endIndex);
  }, [rows, sortColumn, sortDirection, page]);

  const totalPages = Math.ceil(rows.length / rowsPerPage);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 200 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (rows.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="body1" color="text.secondary">
          No results returned
        </Typography>
      </Paper>
    );
  }

  return (
    <Box>
      {/* Statistics */}
      <Box sx={{ mb: 2, display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
        <Chip
          label={`${rowCount ?? rows.length} row${(rowCount ?? rows.length) !== 1 ? 's' : ''}`}
          size="small"
          color="primary"
          variant="outlined"
        />
        {executionTimeMs !== undefined && (
          <Chip
            label={`${executionTimeMs}ms`}
            size="small"
            color="secondary"
            variant="outlined"
          />
        )}
        {rows.length > rowsPerPage && (
          <Chip
            label={`Showing ${(page - 1) * rowsPerPage + 1}-${Math.min(page * rowsPerPage, rows.length)} of ${rows.length}`}
            size="small"
            variant="outlined"
          />
        )}
      </Box>

      {/* Table */}
      <TableContainer component={Paper} variant="outlined">
        <Table size="small" stickyHeader>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={column}
                  sortDirection={sortColumn === column ? (sortDirection || undefined) : undefined}
                  sx={{ fontWeight: 'bold', bgcolor: 'grey.100' }}
                >
                  <TableSortLabel
                    active={sortColumn === column}
                    direction={sortColumn === column ? (sortDirection || 'asc') : 'asc'}
                    onClick={() => handleSort(column)}
                  >
                    {column}
                  </TableSortLabel>
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {sortedAndPaginatedRows.map((row, rowIndex) => (
              <TableRow
                key={rowIndex}
                hover
                sx={{ '&:nth-of-type(odd)': { bgcolor: 'action.hover' } }}
              >
                {columns.map((column) => (
                  <TableCell key={column} sx={{ fontFamily: 'monospace', fontSize: '0.875rem' }}>
                    {formatValue(row[column])}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Pagination */}
      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
          <Pagination
            count={totalPages}
            page={page}
            onChange={(_event, newPage) => setPage(newPage)}
            color="primary"
            size="small"
          />
        </Box>
      )}
    </Box>
  );
}

