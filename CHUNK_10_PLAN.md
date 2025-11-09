# Chunk 10: Frontend - NL Query Interface

## 🎯 Goal
Build a complete natural language query interface where users can:
- Select a database
- Enter natural language queries
- See real-time progress updates
- View generated SQL
- Access query history

## 📋 What We'll Build

### 1. **Database Selection**
- Dropdown to select from available databases
- Fetch databases from `/api/databases` endpoint
- Display database name and type

### 2. **Query Input Form**
- Large text area for natural language input
- Character counter
- Submit button with loading state
- Form validation

### 3. **Real-time Progress Updates**
- WebSocket integration for live updates
- Progress indicator showing current stage
- Status messages (Processing → SQL Generated → Validating → Complete)

### 4. **Generated SQL Display**
- Code block showing generated SQL
- Syntax highlighting (optional)
- Copy to clipboard button
- Validation status (valid/invalid)

### 5. **Query History**
- Sidebar or section showing recent queries
- Click to reuse previous queries
- Store in localStorage (temporary, will use backend in Chunk 11)

### 6. **Error Handling**
- Display validation errors
- Show API errors gracefully
- Retry functionality

## 🛠️ Technical Implementation

### Files to Create/Modify

1. **`frontend/src/services/api.ts`**
   - Add `databaseApi.getAll()` to fetch databases

2. **`frontend/src/pages/NLToSQLPage.tsx`**
   - Complete rewrite with full functionality
   - Form handling with React hooks
   - WebSocket integration
   - State management

3. **`frontend/src/types/index.ts`**
   - Already has all needed types (DatabaseInfo, NLToSQLRequest, etc.)

4. **`frontend/src/hooks/useWebSocket.ts`**
   - Already implemented, just need to use it

### Components Structure

```
NLToSQLPage
├── DatabaseSelector (dropdown)
├── QueryInputForm
│   ├── TextArea
│   ├── Submit Button
│   └── Loading Indicator
├── ProgressIndicator (WebSocket updates)
├── SQLPreview
│   ├── Generated SQL
│   ├── Copy Button
│   └── Validation Status
└── QueryHistory (sidebar)
```

## 📦 Dependencies

All dependencies are already installed:
- `@mui/material` - UI components
- `@stomp/stompjs` - WebSocket client
- `sockjs-client` - WebSocket transport
- `axios` - HTTP client

## 🎨 UI/UX Features

1. **Responsive Design**
   - Works on desktop and mobile
   - Collapsible query history on mobile

2. **Visual Feedback**
   - Loading spinners
   - Progress bars
   - Success/error alerts
   - Disabled states during processing

3. **Accessibility**
   - Proper labels
   - Keyboard navigation
   - ARIA attributes

## ✅ Deliverables

- [x] Database selection dropdown
- [x] Natural language input form
- [x] Real-time progress updates via WebSocket
- [x] Generated SQL preview
- [x] Query history (localStorage)
- [x] Error handling and validation
- [x] Loading states
- [x] Responsive design

## 🧪 Testing Checklist

1. **Database Selection**
   - [ ] Loads databases from API
   - [ ] Shows empty state if no databases
   - [ ] Handles API errors

2. **Query Submission**
   - [ ] Validates required fields
   - [ ] Shows loading state
   - [ ] Sends correct request to backend
   - [ ] Handles errors gracefully

3. **Real-time Updates**
   - [ ] Receives WebSocket updates
   - [ ] Displays progress stages
   - [ ] Shows success/error messages

4. **SQL Preview**
   - [ ] Displays generated SQL
   - [ ] Shows validation status
   - [ ] Copy button works

5. **Query History**
   - [ ] Saves queries to localStorage
   - [ ] Displays recent queries
   - [ ] Click to reuse query

## 🚀 Next Steps

After Chunk 10:
- **Chunk 11**: Results Display - Show query execution results
- **Chunk 12**: Data Visualization - Charts and insights

---

**Estimated Time**: 2-3 hours
**Difficulty**: Medium
**Prerequisites**: Chunk 9 (Frontend Setup) ✅

