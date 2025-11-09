# Chunk 10: Frontend - NL Query Interface - Testing Guide

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
   - If no databases exist, register one via Settings page or API

## ✅ Test Cases

### 1. Database Selection

**Test:** Load and select database
1. Navigate to "NL to SQL" page
2. Check if database dropdown loads
3. Verify databases are displayed
4. Select a database from dropdown
5. Verify selection is saved

**Expected:**
- Dropdown shows available databases
- First database is auto-selected
- Selection persists when switching databases

**Edge Cases:**
- If no databases: Shows "No databases available"
- If API fails: Shows error message

### 2. Query Input

**Test:** Enter natural language query
1. Type a query in the text area
2. Verify character counter updates
3. Try submitting empty query
4. Verify validation prevents submission

**Expected:**
- Character counter shows correct count
- Empty query shows validation error
- Form is disabled during submission

**Test Queries:**
```
"Show me top 5 customers"
"List all orders from last month"
"Find customers with revenue greater than 1000"
"What are the total sales by product?"
```

### 3. Query Submission

**Test:** Submit query and get SQL
1. Select a database
2. Enter query: "Show me top 5 customers"
3. Click "Convert to SQL"
4. Verify loading state appears
5. Wait for response

**Expected:**
- Button shows "Generating SQL..." during submission
- Form is disabled during processing
- Response appears after completion
- SQL is displayed in preview section

### 4. Real-time Progress Updates

**Test:** WebSocket progress updates
1. Submit a query
2. Watch the progress indicator
3. Verify stages update in real-time

**Expected Stages:**
- "Processing" → "Generating SQL" → "Validating" → "Complete"
- Progress bar shows during processing
- Status messages update dynamically

**Verify:**
- WebSocket connection is active (check HomePage status)
- Updates appear without page refresh
- Progress messages are clear and informative

### 5. Generated SQL Preview

**Test:** View generated SQL
1. Submit a query
2. Wait for response
3. Check SQL preview section

**Expected:**
- SQL is displayed in monospace font
- Validation status chip shows (Valid/Invalid)
- Copy button is visible
- SQL is properly formatted

**Test Copy Function:**
1. Click copy button
2. Paste in text editor
3. Verify SQL was copied correctly

### 6. Validation Errors

**Test:** Invalid SQL handling
1. Submit a query that might generate invalid SQL
2. Check validation status
3. Review validation errors if any

**Expected:**
- Invalid SQL shows red "Invalid SQL" chip
- Validation errors are displayed in alert
- Error messages are clear and actionable

### 7. Query History

**Test:** Save and load query history
1. Submit a few queries
2. Click "Show History" button
3. Verify queries appear in sidebar
4. Click on a history item
5. Verify query loads into form

**Expected:**
- History sidebar appears/disappears on toggle
- Recent queries are listed (max 20)
- Each item shows: timestamp, query, database, validation status
- Clicking item loads query and database
- History persists after page refresh (localStorage)

**Test History Persistence:**
1. Submit a query
2. Refresh the page
3. Click "Show History"
4. Verify query is still there

### 8. Error Handling

**Test:** API errors
1. Stop the backend server
2. Try to submit a query
3. Verify error message appears

**Expected:**
- Error alert is displayed
- Error message is user-friendly
- Form is re-enabled after error
- Can dismiss error alert

**Test Network Errors:**
1. Disconnect internet
2. Submit query
3. Verify error handling

### 9. Responsive Design

**Test:** Mobile/Tablet view
1. Open browser DevTools
2. Switch to mobile view (375px width)
3. Test all features

**Expected:**
- Layout adapts to smaller screen
- Query history sidebar stacks or collapses
- All buttons are accessible
- Text is readable

**Test Desktop View:**
1. Switch to desktop view (1920px width)
2. Verify layout uses full width
3. Check query history sidebar positioning

### 10. WebSocket Connection

**Test:** WebSocket status
1. Navigate to HomePage
2. Check WebSocket status card
3. Verify it shows "Connected"
4. Go to NL to SQL page
5. Submit a query
6. Verify real-time updates work

**Expected:**
- WebSocket shows "Connected" on HomePage
- Real-time updates work on NL to SQL page
- Connection persists across page navigation

## 🐛 Common Issues & Solutions

### Issue: "No databases available"
**Solution:**
- Register a database via Settings page or API
- Check backend is running
- Verify database API endpoint works: `curl http://localhost:8080/api/databases`

### Issue: WebSocket not connecting
**Solution:**
- Check HomePage shows "Connected"
- Verify backend WebSocket endpoint is accessible
- Check browser console for errors
- Ensure backend is running

### Issue: Query submission fails
**Solution:**
- Check backend logs for errors
- Verify database is properly configured
- Check OpenAI API key is set
- Verify schema embeddings are indexed

### Issue: History not saving
**Solution:**
- Check browser localStorage is enabled
- Verify no errors in console
- Check localStorage quota (max 20 items)

## 📊 Test Results Template

```
Test Date: ___________
Backend Version: ___________
Frontend Version: ___________

✅ Database Selection: PASS / FAIL
✅ Query Input: PASS / FAIL
✅ Query Submission: PASS / FAIL
✅ Real-time Updates: PASS / FAIL
✅ SQL Preview: PASS / FAIL
✅ Validation Errors: PASS / FAIL
✅ Query History: PASS / FAIL
✅ Error Handling: PASS / FAIL
✅ Responsive Design: PASS / FAIL
✅ WebSocket Connection: PASS / FAIL

Notes:
_______________________________________
_______________________________________
```

## 🚀 Quick Test Script

```bash
# 1. Start backend
cd backend && ./mvnw spring-boot:run &

# 2. Start frontend
cd frontend && npm run dev &

# 3. Open browser
open http://localhost:3000

# 4. Navigate to NL to SQL page
# 5. Test all features above
```

## ✅ Success Criteria

All tests pass when:
- ✅ Can select database
- ✅ Can enter and submit queries
- ✅ Real-time updates work
- ✅ SQL preview displays correctly
- ✅ Query history saves and loads
- ✅ Errors are handled gracefully
- ✅ UI is responsive
- ✅ WebSocket connection is stable

---

**Chunk 10 Testing Complete!** 🎉

