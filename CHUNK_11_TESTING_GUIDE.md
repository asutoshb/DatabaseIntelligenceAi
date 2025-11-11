# Chunk 11: Frontend - Real-time Status & Results - Testing Guide

## 🧪 Testing Checklist

### Prerequisites

1. **Backend Running:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   Backend should be running on `http://localhost:8080`

2. **Frontend Running:**
   ```bash
   cd frontend
   npm run dev
   ```
   Frontend should be running on `http://localhost:3000`

3. **Database Setup:**
   - At least one database should be registered in the system
   - Database should have some data/tables for querying
   - If no databases exist, register one via Settings page or API

4. **WebSocket Connection:**
   - Verify WebSocket is connected (check HomePage status indicator)
   - Connection should show "Connected" status

## ✅ Test Cases

### 1. Execute Query Button Visibility

**Test:** Button appears only when SQL is generated
1. Navigate to "NL to SQL" page
2. Verify "Execute Query" button is NOT visible initially
3. Enter a query and click "Convert to SQL"
4. Wait for SQL to be generated
5. Verify "Execute Query" button appears after SQL generation

**Expected:**
- Button only appears when `response.isValid === true`
- Button is green (success color)
- Button shows play arrow icon
- Button text: "Execute Query"

**Edge Cases:**
- If SQL is invalid: Button should NOT appear
- If no SQL generated: Button should NOT appear

### 2. Query Execution - Basic Flow

**Test:** Execute a simple query
1. Convert a natural language query to SQL
2. Click "Execute Query" button
3. Observe the execution process

**Expected:**
- Button changes to "Executing..." with spinner
- Button is disabled during execution
- Progress indicator appears
- Real-time progress updates show stages
- Results table appears after completion

**Test Queries:**
```
"Show me top 5 customers"
"List all orders"
"Count total products"
"Find customers with revenue > 1000"
```

### 3. Real-time Progress Updates

**Test:** WebSocket progress updates during execution
1. Execute a query
2. Watch the progress indicator
3. Verify stages update in real-time

**Expected Stages:**
- "REQUEST_RECEIVED" → "Validating query..."
- "VALIDATING" → "Validating SQL query"
- "EXECUTING" → "Executing SQL query"
- "PROCESSING_RESULTS" → "Processing results"
- "COMPLETED" → "Query execution completed"

**Verify:**
- Progress bar shows during execution
- Status messages update dynamically
- Updates appear without page refresh
- WebSocket connection remains active

**How to Verify:**
- Open browser DevTools → Console
- Look for WebSocket connection logs
- Check Network tab → WS filter for WebSocket messages

### 4. Results Table Display

**Test:** View query results in table
1. Execute a query that returns results
2. Verify results table appears
3. Check table structure

**Expected:**
- Table header shows column names
- Rows display data correctly
- Data is formatted appropriately:
  - Numbers: Displayed as numbers
  - Dates: Formatted as readable dates
  - Booleans: Show "true" or "false"
  - NULL: Show "NULL"
  - JSON: Stringified

**Verify:**
- All columns from query are displayed
- Row count matches expected results
- Data types are formatted correctly
- Table is scrollable if many rows

### 5. Table Sorting

**Test:** Sort columns by clicking headers
1. Execute a query with multiple columns
2. Click on a column header
3. Verify sorting behavior

**Expected:**
- First click: Sort ascending (↑)
- Second click: Sort descending (↓)
- Third click: Remove sort (optional, or cycle back)
- Sort indicator (arrow) appears on active column

**Test Cases:**
- Sort by numeric column (e.g., "id", "revenue")
- Sort by text column (e.g., "name", "email")
- Sort by date column (if available)
- Sort with NULL values

**Verify:**
- Sorting works correctly for all data types
- NULL values are handled properly (usually sorted last)
- Multiple clicks toggle sort direction
- Only one column sorted at a time

### 6. Table Pagination

**Test:** Pagination for large result sets
1. Execute a query that returns > 50 rows
2. Verify pagination appears
3. Navigate between pages

**Expected:**
- First 50 rows displayed initially
- Pagination controls appear at bottom
- Page numbers are clickable
- Current page is highlighted
- "Showing X-Y of Z" indicator appears

**Test Cases:**
- Query returning 100+ rows
- Query returning exactly 50 rows
- Query returning < 50 rows (no pagination)

**Verify:**
- Pagination only appears when > 50 rows
- Page navigation works correctly
- Data updates when changing pages
- Sort order is maintained across pages

### 7. Execution Statistics

**Test:** View execution statistics
1. Execute a query
2. Check statistics display

**Expected:**
- Row count chip shows correct number
- Execution time chip shows time in milliseconds
- Execution timestamp shows when query ran
- Statistics appear above results table

**Verify:**
- Row count matches actual result count
- Execution time is reasonable (< 30 seconds typically)
- Timestamp is in readable format
- Statistics update for each new execution

### 8. Error Handling - API Errors

**Test:** Handle API errors during execution
1. Stop backend server
2. Try to execute a query
3. Verify error handling

**Expected:**
- Error alert appears
- Error message is clear and helpful
- Retry button appears
- Execution state is reset

**Error Messages to Test:**
- Network error (backend down)
- Timeout error (query takes too long)
- Invalid SQL error
- Database connection error

**Verify:**
- Error messages are user-friendly
- Retry button is functional
- Error can be dismissed
- Can retry after fixing issue

### 9. Error Handling - Execution Errors

**Test:** Handle SQL execution errors
1. Execute an invalid SQL query (if possible)
2. Or execute query that causes database error
3. Verify error handling

**Expected:**
- Error alert appears
- Error message explains the issue
- Retry button appears
- Results table does NOT appear

**Test Cases:**
- SQL syntax error
- Table doesn't exist error
- Column doesn't exist error
- Permission denied error
- Query timeout

**Verify:**
- Error messages are clear
- Error includes relevant details
- Retry functionality works
- Can continue with new query

### 10. Retry Functionality

**Test:** Retry failed query execution
1. Cause an execution error (or use network error)
2. Click "Retry" button
3. Verify retry works

**Expected:**
- Retry button appears on error
- Clicking retry re-executes the query
- Previous error is cleared
- New execution starts

**Verify:**
- Retry uses same SQL query
- Retry uses same database
- Error state is cleared
- Progress indicators reset

### 11. WebSocket Disconnection Handling

**Test:** Handle WebSocket disconnection during execution
1. Start query execution
2. Disconnect network (or stop WebSocket)
3. Verify graceful handling

**Expected:**
- Execution continues via HTTP (fallback)
- Error shown if WebSocket critical
- UI doesn't freeze
- Can retry after reconnection

**Note:** Current implementation uses HTTP for actual execution, WebSocket is for progress only, so disconnection shouldn't break execution.

### 12. Multiple Executions

**Test:** Execute multiple queries in sequence
1. Execute first query
2. Wait for results
3. Execute second query
4. Verify both work correctly

**Expected:**
- Each execution is independent
- Results from previous query are replaced
- Progress indicators reset
- No state contamination

**Verify:**
- Can execute multiple queries without issues
- Previous results don't interfere
- State is properly reset between executions

### 13. Empty Results Handling

**Test:** Handle queries that return no results
1. Execute query that returns 0 rows
2. Verify empty state handling

**Expected:**
- Table shows "No results returned" message
- Statistics show 0 rows
- Execution time still shown
- No errors displayed

**Test Queries:**
```
"Find customers that don't exist"
"Show orders from year 3000"
"List products with negative price"
```

### 14. Large Result Sets

**Test:** Handle queries with many results
1. Execute query returning 100+ rows
2. Verify performance and display

**Expected:**
- Pagination works correctly
- Table renders efficiently
- Sorting works on all pages
- No performance issues

**Verify:**
- UI remains responsive
- Pagination controls work
- Can navigate all pages
- Sort works across pages

### 15. Data Type Formatting

**Test:** Verify different data types format correctly
1. Execute query with various data types
2. Check formatting in table

**Data Types to Test:**
- **Integers**: `123`, `-456`
- **Decimals**: `123.45`, `0.001`
- **Strings**: `"text"`, `"with spaces"`
- **Booleans**: `true`, `false`
- **Dates**: `2024-01-15`, `2024-01-15T10:30:00`
- **NULL**: `null`, `undefined`
- **JSON**: `{"key": "value"}`

**Expected:**
- Numbers display as numbers (not strings)
- Dates display in readable format
- Booleans show "true"/"false"
- NULL shows "NULL"
- JSON is stringified

### 16. Execution Timeout

**Test:** Handle query timeout
1. Execute a very slow query (if possible)
2. Or set timeout to very low value
3. Verify timeout handling

**Expected:**
- Timeout error appears after timeout period
- Error message mentions timeout
- Retry button appears
- Can retry with longer timeout (if configurable)

**Note:** Default timeout is 30 seconds. Very slow queries may timeout.

### 17. Concurrent Executions

**Test:** Handle multiple simultaneous executions
1. Open multiple browser tabs
2. Execute queries in different tabs
3. Verify each works independently

**Expected:**
- Each tab has independent state
- WebSocket updates are filtered by request ID
- No cross-contamination between tabs
- All executions complete successfully

### 18. Copy Results (Future Enhancement)

**Test:** Copy results to clipboard
1. Execute query
2. Try to copy results (if feature exists)

**Note:** This is a future enhancement. Current implementation doesn't have copy functionality for results, only for SQL.

### 19. Export Results (Future Enhancement)

**Test:** Export results to CSV/Excel
1. Execute query
2. Try to export results (if feature exists)

**Note:** This is a future enhancement. Current implementation doesn't have export functionality.

### 20. Integration with Query History

**Test:** Verify execution results don't interfere with history
1. Execute multiple queries
2. Check query history
3. Verify history still works

**Expected:**
- Query history shows previous queries
- Execution results don't affect history
- Can load queries from history
- History items show SQL, not results

## 🐛 Common Issues & Solutions

### Issue 1: Execute Button Not Appearing

**Symptoms:** SQL generated but button doesn't show

**Solutions:**
- Check `response.isValid === true`
- Verify SQL was actually generated
- Check browser console for errors
- Refresh page and try again

### Issue 2: Results Not Displaying

**Symptoms:** Query executes but no table appears

**Solutions:**
- Check `executionResult.success === true`
- Verify `executionResult.rows` is not empty
- Check browser console for errors
- Verify ResultsTable component is imported correctly

### Issue 3: WebSocket Updates Not Received

**Symptoms:** No progress updates during execution

**Solutions:**
- Check WebSocket connection status (HomePage)
- Verify `executionRequestId` matches backend `requestId`
- Check browser console for WebSocket errors
- Verify backend is publishing to `/topic/query-execution`
- Check Network tab → WS filter

### Issue 4: Sorting Not Working

**Symptoms:** Clicking column headers doesn't sort

**Solutions:**
- Check browser console for errors
- Verify `handleSort` function is called
- Check data types (mixing types can cause issues)
- Verify `useMemo` dependencies are correct
- Try refreshing page

### Issue 5: Pagination Not Working

**Symptoms:** Pagination controls don't appear or don't work

**Solutions:**
- Verify result set has > 50 rows
- Check `totalPages` calculation
- Verify pagination state updates
- Check browser console for errors

### Issue 6: Performance Issues

**Symptoms:** UI freezes with large result sets

**Solutions:**
- Reduce `rowsPerPage` value in ResultsTable
- Check for infinite loops in rendering
- Verify `useMemo` is working correctly
- Consider implementing virtual scrolling (future)

### Issue 7: Error Messages Not Clear

**Symptoms:** Error messages are technical or unclear

**Solutions:**
- Check error message formatting
- Verify user-friendly error messages
- Check if error includes helpful context
- Consider adding error code explanations

## 📊 Test Data Setup

### Recommended Test Queries

**Simple Queries:**
```sql
SELECT * FROM customers LIMIT 5;
SELECT COUNT(*) FROM orders;
SELECT name, email FROM users;
```

**Queries with Sorting:**
```sql
SELECT * FROM customers ORDER BY revenue DESC LIMIT 10;
SELECT * FROM products ORDER BY price ASC;
```

**Queries with Multiple Columns:**
```sql
SELECT id, name, email, created_at FROM users;
SELECT order_id, customer_id, total, status FROM orders;
```

**Queries Returning Many Rows:**
```sql
SELECT * FROM orders;  -- If you have 100+ orders
SELECT * FROM products;
```

### Database Setup for Testing

1. **Create Test Tables:**
   ```sql
   CREATE TABLE customers (
     id SERIAL PRIMARY KEY,
     name VARCHAR(100),
     email VARCHAR(100),
     revenue DECIMAL(10,2)
   );
   
   CREATE TABLE orders (
     id SERIAL PRIMARY KEY,
     customer_id INTEGER,
     total DECIMAL(10,2),
     created_at TIMESTAMP
   );
   ```

2. **Insert Test Data:**
   ```sql
   INSERT INTO customers (name, email, revenue) VALUES
     ('John Doe', 'john@example.com', 1500.00),
     ('Jane Smith', 'jane@example.com', 2300.50),
     ('Bob Johnson', 'bob@example.com', 850.25);
   
   INSERT INTO orders (customer_id, total, created_at) VALUES
     (1, 100.00, '2024-01-15 10:30:00'),
     (2, 250.50, '2024-01-16 14:20:00'),
     (3, 75.25, '2024-01-17 09:15:00');
   ```

## ✅ Acceptance Criteria

All tests should pass for Chunk 11 to be considered complete:

- ✅ Execute Query button appears after SQL generation
- ✅ Query execution works correctly
- ✅ Real-time progress updates via WebSocket
- ✅ Results displayed in sortable, paginated table
- ✅ Execution statistics shown (row count, time, timestamp)
- ✅ Error handling works correctly
- ✅ Retry functionality works
- ✅ Data formatting is correct for all types
- ✅ Performance is acceptable for large result sets
- ✅ UI/UX is professional and intuitive

## 🎯 Testing Checklist Summary

- [ ] Execute Query button visibility
- [ ] Basic query execution flow
- [ ] Real-time progress updates
- [ ] Results table display
- [ ] Table sorting functionality
- [ ] Table pagination
- [ ] Execution statistics
- [ ] API error handling
- [ ] Execution error handling
- [ ] Retry functionality
- [ ] WebSocket disconnection handling
- [ ] Multiple executions
- [ ] Empty results handling
- [ ] Large result sets
- [ ] Data type formatting
- [ ] Execution timeout
- [ ] Concurrent executions
- [ ] Integration with query history

## 🚀 Next Steps

After completing all tests:
1. Document any issues found
2. Fix any bugs discovered
3. Update documentation if needed
4. Proceed to Chunk 12 (Data Visualization & Insights)

---

**Happy Testing!** 🧪

If you encounter any issues not covered in this guide, please document them and we'll address them in the next iteration.

