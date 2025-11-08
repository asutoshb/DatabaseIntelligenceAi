# Chunk 9: Frontend - React Setup & UI Components - Explanation

## 🎯 What We Built

We created a professional, modern frontend interface with:
- **React Router** for navigation
- **Material-UI** for beautiful components
- **Layout system** with Header and Sidebar
- **API service layer** for backend communication
- **WebSocket integration** for real-time updates
- **TypeScript** for type safety

## 📁 Project Structure

```
frontend/src/
├── components/
│   └── layout/
│       ├── Header.tsx          # Top navigation bar
│       ├── Sidebar.tsx          # Left sidebar menu
│       └── Layout.tsx              # Main layout wrapper
├── pages/
│   ├── HomePage.tsx             # Dashboard/home
│   ├── NLToSQLPage.tsx          # NL to SQL interface (placeholder)
│   ├── QueryHistoryPage.tsx     # Query history (placeholder)
│   └── SettingsPage.tsx         # Settings (placeholder)
├── services/
│   └── api.ts                   # API client with axios
├── hooks/
│   └── useWebSocket.ts          # WebSocket subscription hook
├── types/
│   └── index.ts                 # TypeScript interfaces
├── App.tsx                      # Main app with routes
└── main.tsx                     # Entry point
```

## 🔧 Key Components

### 1. **Layout System**

#### Header Component
- Fixed top navigation bar
- Logo and app title
- Mobile menu button
- Responsive design

#### Sidebar Component
- Navigation menu with icons
- Active route highlighting
- Collapsible on mobile
- Menu items: Home, NL to SQL, History, Settings

#### Layout Component
- Wraps all pages
- Manages Header and Sidebar
- Responsive layout (desktop vs mobile)
- Proper spacing and theming

### 2. **API Service Layer** (`services/api.ts`)

**Purpose:** Centralized API client for all backend communication.

**Features:**
- Axios instance with base URL configuration
- Request interceptor (adds JWT token when available)
- Response interceptor (error handling)
- Type-safe API methods

**Example Usage:**
```typescript
import { nlToSqlApi } from '../services/api';

const response = await nlToSqlApi.convert({
  databaseInfoId: 1,
  naturalLanguageQuery: "Show me top 5 customers"
});
```

### 3. **WebSocket Hook** (`hooks/useWebSocket.ts`)

**Purpose:** Subscribe to real-time updates from backend.

**Features:**
- Auto-connects on mount
- Subscribes to `/topic/nl-to-sql` and `/topic/query-execution`
- Callback functions for different update types
- Auto-reconnect on disconnect
- Cleanup on unmount

**Example Usage:**
```typescript
const { isConnected } = useWebSocket({
  onNlToSqlUpdate: (message) => {
    console.log('Progress:', message.stage);
  },
  onQueryExecutionUpdate: (message) => {
    console.log('Execution:', message.stage);
  }
});
```

### 4. **TypeScript Types** (`types/index.ts`)

**Purpose:** Type safety for API requests and responses.

**Interfaces:**
- `RealTimeStatusMessage` - WebSocket message format
- `NLToSQLRequest` / `NLToSQLResponse` - NL to SQL API
- `QueryExecutionRequest` / `QueryExecutionResponse` - Query execution API
- `HealthResponse` - Health check API

**Benefits:**
- Autocomplete in IDE
- Compile-time error checking
- Self-documenting code

### 5. **React Router Setup**

**Routes:**
- `/` - HomePage (dashboard)
- `/nl-to-sql` - NL to SQL interface
- `/history` - Query history
- `/settings` - Settings

**Features:**
- Client-side routing (no page reloads)
- Active route highlighting in sidebar
- Programmatic navigation

### 6. **Material-UI Theme**

**Configuration:**
- Light theme (can be extended to dark mode)
- Custom primary/secondary colors
- Typography settings
- Responsive breakpoints

## 🎨 UI Design

### Layout Structure
```
┌─────────────────────────────────────────┐
│ Header (Fixed Top)                      │
├──────────┬──────────────────────────────┤
│          │                              │
│ Sidebar  │  Main Content Area           │
│ (240px)  │  (Flexible width)            │
│          │                              │
│          │  - HomePage                  │
│          │  - NLToSQLPage               │
│          │  - QueryHistoryPage          │
│          │  - SettingsPage              │
│          │                              │
└──────────┴──────────────────────────────┘
```

### Responsive Design
- **Desktop:** Sidebar always visible, full layout
- **Mobile:** Sidebar hidden, toggleable via menu button
- **Tablet:** Adaptive layout based on screen size

## 🔌 API Integration

### Base URL Configuration
```typescript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

**Environment Variables:**
- `VITE_API_BASE_URL` - Backend API URL (optional, defaults to localhost)
- `VITE_WS_URL` - WebSocket URL (optional, defaults to localhost)

### Authentication (Future)
The API service is ready for JWT authentication:
- Token stored in `localStorage.getItem('authToken')`
- Automatically added to requests via interceptor
- Will be used when Chunk 7 auth is integrated

## 📡 WebSocket Integration

### Connection Flow
1. Component mounts → `useWebSocket` hook initializes
2. STOMP client connects to `ws://localhost:8080/ws/websocket`
3. Subscribes to `/topic/nl-to-sql` and `/topic/query-execution`
4. Receives real-time updates via callbacks
5. Cleans up on component unmount

### Message Format
```typescript
{
  feature: "NL_TO_SQL" | "QUERY_EXECUTION",
  requestId: "uuid-here",
  stage: "RETRIEVING_SCHEMA" | "COMPLETED" | "ERROR",
  status: "IN_PROGRESS" | "SUCCESS" | "ERROR",
  message: "Human readable message",
  data: { /* optional metadata */ },
  timestamp: "2025-01-15T10:30:00Z"
}
```

## 🎓 What You Learned

### React Concepts
- **Component Composition** - Building complex UIs from simple parts
- **Custom Hooks** - Reusable logic (useWebSocket)
- **React Router** - Client-side navigation
- **Context/Props** - Data flow between components

### TypeScript
- **Interfaces** - Type definitions for API contracts
- **Type Safety** - Compile-time error checking
- **Autocomplete** - IDE support for API calls

### Material-UI
- **Theme System** - Consistent styling
- **Responsive Design** - Mobile-first approach
- **Component Library** - Pre-built UI components

### WebSocket
- **STOMP Protocol** - Structured messaging over WebSocket
- **Real-time Updates** - Server-to-client communication
- **Connection Management** - Auto-reconnect, cleanup

## 🚀 Next Steps

**Chunk 10:** NL Query Interface
- Natural language input form
- Database selection dropdown
- Real-time progress display
- Generated SQL preview
- Query execution button

The foundation is ready! Now we'll build the actual NL to SQL interface in Chunk 10.

