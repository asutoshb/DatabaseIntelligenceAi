# Chunk 10: Frontend - NL Query Interface - Explanation

## 🎯 What We Built

We created a complete natural language query interface that allows users to:
1. Select a database from available databases
2. Enter natural language queries
3. See real-time progress updates via WebSocket
4. View generated SQL with validation status
5. Access query history stored in localStorage

## 📁 Files Created/Modified

### 1. **`frontend/src/services/api.ts`**
Added database API endpoints:
```typescript
export const databaseApi = {
  getAll: async (): Promise<DatabaseInfo[]> => {
    const response = await apiClient.get<DatabaseInfo[]>('/databases');
    return response.data;
  },
  getById: async (id: number): Promise<DatabaseInfo> => {
    const response = await apiClient.get<DatabaseInfo>(`/databases/${id}`);
    return response.data;
  },
};
```

**Why?** We need to fetch available databases so users can select which database to query.

### 2. **`frontend/src/pages/NLToSQLPage.tsx`**
Complete rewrite of the placeholder page with full functionality.

## 🔧 Key Features Explained

### 1. **Database Selection**

```typescript
const [databases, setDatabases] = useState<DatabaseInfo[]>([]);
const [selectedDatabaseId, setSelectedDatabaseId] = useState<number | ''>('');

// Load databases on mount
useEffect(() => {
  const loadDatabases = async () => {
    const dbList = await databaseApi.getAll();
    setDatabases(dbList);
    if (dbList.length > 0 && !selectedDatabaseId) {
      setSelectedDatabaseId(dbList[0].id); // Auto-select first database
    }
  };
  loadDatabases();
}, []);
```

**How it works:**
- On component mount, fetches all available databases from `/api/databases`
- Automatically selects the first database if available
- Displays databases in a Material-UI Select dropdown

### 2. **Query Input Form**

```typescript
const [query, setQuery] = useState('');

<TextField
  fullWidth
  multiline
  rows={6}
  label="Enter your question in natural language"
  value={query}
  onChange={(e) => setQuery(e.target.value)}
  disabled={isSubmitting}
  helperText={`${query.length} characters`}
  required
/>
```

**Features:**
- Large text area (6 rows) for comfortable query input
- Character counter
- Disabled during submission to prevent duplicate requests
- Required validation

### 3. **Real-time Progress Updates**

```typescript
const handleNlToSqlUpdate = useCallback((message: RealTimeStatusMessage) => {
  if (currentRequestId && message.requestId === currentRequestId) {
    setProgressStage(message.stage);
    setProgressStatus(message.status);
    setProgressMessage(message.message);
    // ... handle success/error
  }
}, [currentRequestId]);

useWebSocket({
  onNlToSqlUpdate: handleNlToSqlUpdate,
});
```

**How it works:**
- WebSocket hook subscribes to `/topic/nl-to-sql` topic
- Filters updates by `requestId` to only show progress for current request
- Updates UI in real-time as backend processes the query
- Shows stages like: "Processing" → "SQL Generated" → "Validating" → "Complete"

**Progress Display:**
```typescript
{isSubmitting && (
  <Box>
    {progressStatus === 'IN_PROGRESS' && <LinearProgress />}
    <Typography>
      {progressStage}: {progressMessage || 'Processing...'}
    </Typography>
  </Box>
)}
```

### 4. **Query Submission**

```typescript
const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  
  // Generate unique request ID
  const requestId = `nl-to-sql-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  setCurrentRequestId(requestId);

  const request: NLToSQLRequest = {
    databaseInfoId: selectedDatabaseId as number,
    naturalLanguageQuery: query.trim(),
    topK: 5,
    clientRequestId: requestId, // For WebSocket correlation
  };

  const result = await nlToSqlApi.convert(request);
  setResponse(result);
  // ... save to history
};
```

**Key points:**
- Generates unique `requestId` for correlating WebSocket updates
- Sends request to `/api/nl-to-sql/convert`
- Handles errors gracefully
- Saves successful queries to history

### 5. **Generated SQL Preview**

```typescript
{response && (
  <Box>
    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
      <Typography variant="h6">Generated SQL</Typography>
      <Chip
        label={response.isValid ? 'Valid SQL' : 'Invalid SQL'}
        color={response.isValid ? 'success' : 'error'}
      />
      <IconButton onClick={handleCopySQL}>
        <CopyIcon />
      </IconButton>
    </Box>
    <Paper sx={{ fontFamily: 'monospace' }}>
      {response.sqlQuery}
    </Paper>
    {/* Validation errors, explanation, relevant schemas */}
  </Box>
)}
```

**Features:**
- Displays SQL in monospace font for readability
- Shows validation status (Valid/Invalid)
- Copy to clipboard button
- Displays validation errors if any
- Shows AI explanation
- Lists relevant schemas used

### 6. **Query History**

```typescript
const QUERY_HISTORY_KEY = 'nl-to-sql-query-history';
const MAX_HISTORY_ITEMS = 20;

// Save to history
const saveToHistory = useCallback((item: QueryHistoryItem) => {
  setQueryHistory((prev) => {
    const updated = [item, ...prev].slice(0, MAX_HISTORY_ITEMS);
    localStorage.setItem(QUERY_HISTORY_KEY, JSON.stringify(updated));
    return updated;
  });
}, []);

// Load from history
useEffect(() => {
  const stored = localStorage.getItem(QUERY_HISTORY_KEY);
  if (stored) {
    setQueryHistory(JSON.parse(stored));
  }
}, []);
```

**How it works:**
- Stores queries in browser's `localStorage`
- Limits to 20 most recent queries
- Displays in collapsible sidebar
- Click to reuse previous query
- Shows timestamp, database, and validation status

**History Item Structure:**
```typescript
interface QueryHistoryItem {
  id: string;
  query: string;
  sql: string;
  databaseId: number;
  databaseName: string;
  timestamp: string;
  success: boolean;
}
```

### 7. **Error Handling**

```typescript
try {
  const result = await nlToSqlApi.convert(request);
  setResponse(result);
} catch (err: any) {
  setError(err.response?.data?.message || err.message || 'Failed to convert query');
  setIsSubmitting(false);
  setProgressStatus('ERROR');
}
```

**Error Display:**
```typescript
{error && (
  <Alert severity="error" onClose={() => setError(null)}>
    {error}
  </Alert>
)}
```

**Features:**
- Catches API errors
- Displays user-friendly error messages
- Dismissible alert
- Resets loading state on error

## 🎨 UI/UX Features

### Responsive Design
- Uses Material-UI Grid system
- Query history sidebar collapses on mobile
- Works on desktop, tablet, and mobile

### Visual Feedback
- Loading spinners during submission
- Progress bar for real-time updates
- Success/error alerts
- Disabled states during processing
- Character counter for query input

### Accessibility
- Proper form labels
- Keyboard navigation support
- ARIA attributes (via Material-UI)

## 🔄 Data Flow

```
User enters query
  ↓
Selects database
  ↓
Clicks "Convert to SQL"
  ↓
Frontend generates requestId
  ↓
Sends POST /api/nl-to-sql/convert
  ↓
Backend processes (sends WebSocket updates)
  ↓
Frontend receives real-time progress
  ↓
Backend returns response
  ↓
Frontend displays SQL, saves to history
```

## 🧪 Testing the Feature

1. **Start Backend:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Start Frontend:**
   ```bash
   cd frontend
   npm run dev
   ```

3. **Test Flow:**
   - Navigate to "NL to SQL" page
   - Select a database (or register one first)
   - Enter query: "Show me top 5 customers"
   - Click "Convert to SQL"
   - Watch real-time progress updates
   - View generated SQL
   - Check query history

## 📚 Key React Concepts Used

### 1. **useState Hook**
Manages component state (form inputs, responses, errors)

### 2. **useEffect Hook**
- Loads databases on mount
- Loads query history on mount
- Cleanup on unmount

### 3. **useCallback Hook**
- Memoizes WebSocket callback to prevent unnecessary re-renders
- Memoizes history save function

### 4. **Controlled Components**
Form inputs are controlled by React state:
```typescript
<TextField value={query} onChange={(e) => setQuery(e.target.value)} />
```

### 5. **Conditional Rendering**
Shows/hides UI based on state:
```typescript
{response && <SQLPreview />}
{error && <Alert />}
{isSubmitting && <ProgressIndicator />}
```

## 🚀 Next Steps

**Chunk 11:** Results Display
- Execute generated SQL
- Display query results in table
- Show execution time
- Handle errors gracefully

**Chunk 12:** Data Visualization
- Auto-detect chart types
- Generate charts from results
- AI insights display

---

**Chunk 10 Complete!** You now have a fully functional NL to SQL interface! 🎉

