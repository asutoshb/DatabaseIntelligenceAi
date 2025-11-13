# Chunk 12: Frontend - Data Visualization & Insights

## Goal
Show charts and AI-generated insights from query results, with auto-detection of chart types and export functionality.

## What We'll Build

### 1. Chart Components
- **BarChart**: For categorical data comparisons
- **LineChart**: For time-series or sequential data
- **PieChart**: For proportional/percentage data
- **Auto-detection**: Automatically determine the best chart type based on data structure

### 2. Chart Type Detection Logic
- Analyze query results to determine appropriate chart type
- Consider column types (numeric, date, categorical)
- Consider number of columns and rows
- Default to table view if charting is not suitable

### 3. AI Insights Display
- Generate simple insights from query results
- Show key statistics (sum, average, min, max)
- Highlight interesting patterns or outliers
- Display insights in a readable format

### 4. Export Functionality
- **CSV Export**: Download results as CSV file
- **JSON Export**: Download results as JSON file
- **Chart Export**: Export chart as PNG image (future enhancement)

### 5. Enhanced Results View
- Toggle between table and chart views
- Show both table and chart simultaneously
- Responsive chart sizing
- Interactive chart tooltips

## Technical Implementation

### Files to Create/Modify

1. **`frontend/src/components/charts/ChartContainer.tsx`** (NEW)
   - Main container component that auto-detects chart type
   - Handles data transformation
   - Renders appropriate chart component

2. **`frontend/src/components/charts/BarChart.tsx`** (NEW)
   - Bar chart component using Recharts
   - Handles categorical data

3. **`frontend/src/components/charts/LineChart.tsx`** (NEW)
   - Line chart component using Recharts
   - Handles time-series data

4. **`frontend/src/components/charts/PieChart.tsx`** (NEW)
   - Pie chart component using Recharts
   - Handles proportional data

5. **`frontend/src/components/InsightsPanel.tsx`** (NEW)
   - Displays AI-generated insights
   - Shows statistics and patterns

6. **`frontend/src/utils/chartDetection.ts`** (NEW)
   - Logic to detect appropriate chart type
   - Data analysis utilities

7. **`frontend/src/utils/exportUtils.ts`** (NEW)
   - CSV export functionality
   - JSON export functionality

8. **`frontend/src/pages/NLToSQLPage.tsx`** (MODIFY)
   - Add chart view toggle
   - Integrate chart components
   - Add export buttons
   - Add insights panel

## Implementation Steps

### Step 1: Install Dependencies
- Install `recharts` library
- Install `@types/recharts` if needed

### Step 2: Create Chart Detection Logic
- Analyze data structure (columns, types, row count)
- Determine best chart type
- Return chart configuration

### Step 3: Create Chart Components
- BarChart component
- LineChart component
- PieChart component
- Shared styling and theming

### Step 4: Create Chart Container
- Auto-detect chart type
- Transform data for charts
- Render appropriate chart component
- Handle edge cases (no data, single row, etc.)

### Step 5: Create Insights Panel
- Calculate statistics (sum, avg, min, max)
- Detect patterns (trends, outliers)
- Format insights for display

### Step 6: Create Export Utilities
- CSV export function
- JSON export function
- Download file functionality

### Step 7: Integrate into NLToSQLPage
- Add view toggle (Table/Chart/Both)
- Add export buttons
- Add insights panel
- Update layout to accommodate new components

## User Flow

1. User executes a query
2. Results are displayed in table format
3. System auto-detects if data is suitable for charting
4. If suitable, chart is automatically displayed
5. User can toggle between table and chart views
6. Insights panel shows key statistics
7. User can export results as CSV or JSON
8. User can view generated SQL

## Chart Type Detection Rules

### Bar Chart
- 1-2 categorical columns + 1+ numeric columns
- Best for: Comparisons, rankings, categories

### Line Chart
- 1 date/time column + 1+ numeric columns
- Best for: Trends over time, sequences

### Pie Chart
- 1 categorical column + 1 numeric column
- Best for: Proportions, percentages, parts of whole

### Table (Default)
- Complex data structures
- Non-numeric data
- Too many columns/rows for effective charting

## Success Criteria

✅ Charts auto-generated from query results
✅ Chart type automatically detected
✅ Can toggle between table and chart views
✅ Insights panel displays key statistics
✅ Can export results as CSV
✅ Can export results as JSON
✅ Charts are responsive and interactive
✅ Handles edge cases gracefully (no data, single row, etc.)

## Tech Stack to Learn

- **Recharts**: React charting library built on D3.js
- **Data Transformation**: Converting tabular data to chart formats
- **File Downloads**: Browser download API
- **Conditional Rendering**: Showing different views based on data
- **Data Analysis**: Calculating statistics and patterns

## Dependencies

```json
{
  "recharts": "^2.10.0"
}
```

## UI/UX Features

1. **Auto-Detection**
   - Charts appear automatically when suitable
   - No manual configuration needed

2. **View Toggle**
   - Easy switching between table and chart
   - Option to show both simultaneously

3. **Interactive Charts**
   - Hover tooltips
   - Clickable legends
   - Responsive sizing

4. **Export Options**
   - One-click CSV export
   - One-click JSON export
   - Clear file naming

5. **Insights Panel**
   - Collapsible panel
   - Key statistics highlighted
   - Easy to scan format

---

**Estimated Time**: 3-4 hours
**Difficulty**: Medium
**Prerequisites**: Chunk 11 (Results Display) ✅


