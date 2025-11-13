# Chunk 12: Frontend - Data Visualization & Insights - Testing Guide

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
   - At least one database should be registered
   - Database should have sample data (customers table with revenue data)
   - If needed, create sample data:
     ```sql
     CREATE TABLE customers (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100),
       revenue DECIMAL(10,2)
     );
     INSERT INTO customers (name, revenue) VALUES
       ('John Doe', 1500.00),
       ('Jane Smith', 2300.50),
       ('Bob Johnson', 850.25),
       ('Alice Williams', 3200.75),
       ('Charlie Brown', 1200.00);
     ```

4. **WebSocket Connection:**
   - Verify WebSocket is connected (check HomePage status indicator)
   - Connection should show "Connected" status

## ✅ Test Cases

### 1. Chart Auto-Detection - Bar Chart

**Test:** Bar chart appears automatically for categorical + numeric data

1. Navigate to "NL to SQL" page
2. Select a database
3. Enter query: "Show me top 5 customers by revenue"
4. Click "Convert to SQL"
5. Wait for SQL generation
6. Click "Execute Query"
7. Wait for results

**Expected:**
- Chart automatically appears (if view is "Chart" or "Both")
- Chart type is Bar Chart
- Customer names on x-axis
- Revenue values on y-axis
- Chart is interactive (hover shows tooltips)
- Chart is responsive (resizes with window)

**Verify:**
- Chart container shows "Visualization" heading
- Bars are color-coded
- Legend shows data series
- Tooltip appears on hover

### 2. Chart Auto-Detection - Line Chart

**Test:** Line chart appears for time series data

**Setup:** Create a table with date and numeric columns:
```sql
CREATE TABLE monthly_revenue (
  month DATE,
  revenue DECIMAL(10,2)
);
INSERT INTO monthly_revenue (month, revenue) VALUES
  ('2024-01-01', 10000),
  ('2024-02-01', 12000),
  ('2024-03-01', 15000),
  ('2024-04-01', 18000);
```

1. Enter query: "Show revenue by month"
2. Convert to SQL and execute
3. View results

**Expected:**
- Chart type is Line Chart
- Months on x-axis
- Revenue on y-axis
- Line connects data points
- Tooltip shows exact values

### 3. Chart Auto-Detection - Pie Chart

**Test:** Pie chart appears for proportional data

**Setup:** Create a table with category and count:
```sql
CREATE TABLE region_distribution (
  region VARCHAR(50),
  customer_count INTEGER
);
INSERT INTO region_distribution (region, customer_count) VALUES
  ('North', 25),
  ('South', 30),
  ('East', 20),
  ('West', 15);
```

1. Enter query: "Show customer distribution by region"
2. Convert to SQL and execute
3. View results

**Expected:**
- Chart type is Pie Chart
- Regions as segments
- Customer count as values
- Percentage labels on segments
- Legend shows region names

### 4. Chart Auto-Detection - Table Fallback

**Test:** Table view for complex/non-chartable data

1. Enter query: "Show all customer details" (query that returns many columns)
2. Convert to SQL and execute
3. View results

**Expected:**
- Chart container shows info message
- Message explains why charting isn't suitable
- Suggests what types of queries work for charts
- Table view still works normally

### 5. View Toggle Functionality

**Test:** Switch between Table, Chart, and Both views

1. Execute a query that returns chartable data
2. Verify default view is "Both"
3. Click "Table" button
4. Verify only table is shown
5. Click "Chart" button
6. Verify only chart is shown
7. Click "Both" button
8. Verify both table and chart are shown side-by-side

**Expected:**
- Active button is highlighted (contained variant)
- Inactive buttons are outlined (text variant)
- Layout changes immediately on click
- No page refresh needed
- Responsive: stacks on mobile, side-by-side on desktop

### 6. Insights Panel Display

**Test:** Insights panel shows statistics and patterns

1. Execute a query with numeric columns
2. Scroll to insights panel (above results)
3. Verify insights are displayed

**Expected:**
- Panel shows row count chip
- Panel shows column count chip
- Panel shows numeric column count chip
- Statistics cards for each numeric column:
  - Average, Min, Max values
  - Total and Median values
- Key insights section with:
  - Row count message
  - Statistics for each numeric column
  - Pattern detection (if applicable)

**Test Queries:**
- "Show me top 5 customers by revenue" → Should show revenue statistics
- "List all orders with amounts" → Should show amount statistics

### 7. Insights - Pattern Detection

**Test:** Insights detect outliers and trends

**Setup:** Create data with outliers:
```sql
INSERT INTO customers (name, revenue) VALUES
  ('Normal Customer', 1000),
  ('Normal Customer 2', 1200),
  ('Normal Customer 3', 1100),
  ('Outlier Customer', 50000); -- Outlier
```

1. Execute query: "Show all customers"
2. Check insights panel

**Expected:**
- Insights mention potential outliers
- Outlier count is shown
- Statistics are calculated correctly

### 8. CSV Export

**Test:** Export results as CSV file

1. Execute a query
2. Wait for results to appear
3. Click "CSV" export button
4. Check Downloads folder

**Expected:**
- File downloads automatically
- Filename: `query-results-YYYY-MM-DD.csv`
- File opens correctly in Excel/Google Sheets
- All columns are included
- All rows are included
- Special characters are properly escaped
- Headers are in first row

**Verify CSV Content:**
- Open file in text editor
- Check that commas in data are quoted
- Check that quotes in data are escaped
- Check that newlines in data are handled

### 9. JSON Export

**Test:** Export results as JSON file

1. Execute a query
2. Wait for results to appear
3. Click "JSON" export button
4. Check Downloads folder

**Expected:**
- File downloads automatically
- Filename: `query-results-YYYY-MM-DD.json`
- File is valid JSON
- File is pretty-printed (indented)
- All columns are included
- All rows are included
- Column order is preserved

**Verify JSON Content:**
- Open file in text editor
- Validate JSON syntax (use JSON validator)
- Check that all data is present
- Check that data types are preserved

### 10. Responsive Design

**Test:** Charts and layout adapt to screen size

1. Execute a query with results
2. Set view to "Both"
3. Resize browser window
4. Test on mobile device (or browser dev tools mobile view)

**Expected:**
- Charts resize automatically
- On desktop (>960px): Table and chart side-by-side
- On mobile (<960px): Table and chart stacked vertically
- Charts remain readable at all sizes
- Export buttons wrap on small screens
- View toggle buttons remain accessible

### 11. Edge Cases - No Data

**Test:** Handle empty result sets

1. Execute query that returns 0 rows: "Find customers with revenue > 1000000"
2. View results

**Expected:**
- Insights panel shows "No data available for insights"
- Chart container shows appropriate message
- Table shows empty state
- No errors in console

### 12. Edge Cases - Single Row

**Test:** Handle single row results

1. Execute query that returns 1 row: "Show customer with highest revenue"
2. View results

**Expected:**
- Table displays single row correctly
- Chart may show message or display single data point
- Insights show "Query returned a single row"
- No errors in console

### 13. Edge Cases - Large Dataset

**Test:** Handle large result sets (>100 rows)

1. Execute query that returns >100 rows
2. View results

**Expected:**
- Chart detection defaults to table view
- Message explains why charting isn't suitable
- Table pagination works correctly
- Export works for all rows (not just visible ones)

### 14. Chart Interactivity

**Test:** Chart tooltips and legends

1. Execute query with chartable data
2. Hover over chart elements (bars, lines, pie segments)
3. Click legend items

**Expected:**
- Tooltip appears on hover
- Tooltip shows exact values
- Tooltip is positioned correctly
- Legend is clickable (hides/shows series)
- Chart updates smoothly

### 15. Multiple Series Charts

**Test:** Charts with multiple data series

**Setup:** Create data with multiple numeric columns:
```sql
CREATE TABLE sales_data (
  month VARCHAR(20),
  revenue DECIMAL(10,2),
  profit DECIMAL(10,2),
  expenses DECIMAL(10,2)
);
```

1. Execute query: "Show revenue, profit, and expenses by month"
2. View chart

**Expected:**
- Chart shows multiple series
- Each series has different color
- Legend shows all series
- Tooltip shows all series values
- All series are visible and distinguishable

## 🐛 Common Issues & Troubleshooting

### Issue 1: Charts Not Appearing

**Symptoms:**
- Results show but no chart appears
- Chart container shows "No data available"

**Possible Causes:**
1. Data not suitable for charting (too many rows, no numeric columns)
2. Chart detection failed
3. Recharts not installed

**Solutions:**
1. Check browser console for errors
2. Verify `recharts` is installed: `npm list recharts`
3. Try a query with clear categorical + numeric data
4. Check that view is set to "Chart" or "Both"

### Issue 2: Export Not Working

**Symptoms:**
- Click export button but no file downloads
- File downloads but is empty/corrupted

**Possible Causes:**
1. Browser blocking downloads
2. Data transformation failed
3. Blob API not supported

**Solutions:**
1. Check browser download settings
2. Check browser console for errors
3. Try different browser
4. Verify data is present before exporting

### Issue 3: Insights Not Showing

**Symptoms:**
- Insights panel is empty
- Statistics not calculated

**Possible Causes:**
1. No numeric columns in results
2. All values are null
3. Component error

**Solutions:**
1. Check browser console for errors
2. Verify query returns numeric data
3. Try query with known numeric columns

### Issue 4: Chart Type Detection Wrong

**Symptoms:**
- Wrong chart type selected
- Chart doesn't make sense for data

**Possible Causes:**
1. Detection algorithm misclassified data
2. Column types not detected correctly

**Solutions:**
1. This is expected for edge cases
2. Chart will show message if not suitable
3. Table view is always available as fallback

## ✅ Acceptance Criteria

All of the following must pass:

- [ ] Bar charts appear for categorical + numeric data
- [ ] Line charts appear for time series data
- [ ] Pie charts appear for proportional data
- [ ] Table view is default for complex data
- [ ] View toggle works (Table/Chart/Both)
- [ ] Insights panel shows statistics
- [ ] CSV export works correctly
- [ ] JSON export works correctly
- [ ] Charts are responsive
- [ ] Charts are interactive (tooltips, legends)
- [ ] Edge cases handled gracefully
- [ ] No console errors
- [ ] No TypeScript errors
- [ ] Responsive design works on mobile

## 🎯 Performance Testing

### Large Dataset Performance

1. Execute query returning 1000+ rows
2. Measure time to:
   - Render table
   - Calculate insights
   - Detect chart type
   - Export to CSV/JSON

**Expected:**
- Table renders in <2 seconds
- Insights calculate in <1 second
- Chart detection in <500ms
- Export completes in <3 seconds

### Memory Usage

1. Execute multiple queries in sequence
2. Monitor browser memory usage
3. Check for memory leaks

**Expected:**
- Memory usage stays reasonable
- No memory leaks
- Old charts/components are cleaned up

## 📝 Test Data Recommendations

### For Bar Charts
```sql
-- Top customers by revenue
SELECT name, revenue FROM customers ORDER BY revenue DESC LIMIT 5;
```

### For Line Charts
```sql
-- Revenue over time
SELECT month, revenue FROM monthly_revenue ORDER BY month;
```

### For Pie Charts
```sql
-- Distribution by category
SELECT region, COUNT(*) as count FROM customers GROUP BY region;
```

### For Table View
```sql
-- Complex data
SELECT * FROM customers; -- Many columns, mixed types
```

---

**Testing Status**: Ready for testing
**Estimated Testing Time**: 1-2 hours
**Priority**: High (Core feature)


