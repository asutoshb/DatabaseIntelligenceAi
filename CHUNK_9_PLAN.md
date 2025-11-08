# Chunk 9: Frontend - React Setup & UI Components - Development Plan

## 🎯 Goal
Create a beautiful, professional frontend interface with proper React structure, navigation, and API integration.

## 📋 What We'll Build

1. **React Router Setup** - Navigation between pages
2. **Layout Components** - Header, Sidebar, Main content area
3. **API Service Layer** - Centralized API calls with TypeScript interfaces
4. **WebSocket Integration** - Real-time updates hook (connects to Chunk 8)
5. **TypeScript Types** - Interfaces for API requests/responses
6. **Material-UI Theme** - Beautiful, responsive design
7. **Navigation Structure** - Routes for different features

## 🔧 Technologies We'll Use

- **React Router** - Client-side navigation
- **Material-UI (MUI)** - UI component library
- **TypeScript** - Type safety for API calls
- **Axios** - HTTP client for API requests
- **@stomp/stompjs** - WebSocket client for real-time updates
- **React Hooks** - useState, useEffect, custom hooks

## 📝 Step-by-Step

1. ✅ Install WebSocket dependency (`@stomp/stompjs`)
2. ✅ Create TypeScript interfaces/types for API
3. ✅ Create API service layer (`api/` directory)
4. ✅ Create WebSocket hook (`hooks/useWebSocket.ts`)
5. ✅ Create Layout components (`components/layout/`)
6. ✅ Set up React Router with routes
7. ✅ Create theme configuration
8. ✅ Update App.tsx with new structure
9. ✅ Create placeholder pages for future features

## 🎓 What You'll Learn

- **React Component Structure** - How to organize components
- **React Router** - Client-side routing and navigation
- **Material-UI** - Building beautiful UIs quickly
- **TypeScript Interfaces** - Type safety for API calls
- **Custom Hooks** - Reusable logic (WebSocket, API calls)
- **Component Composition** - Building complex UIs from simple parts
- **Responsive Design** - Mobile-friendly layouts

## 🏗️ Project Structure

```
frontend/src/
├── components/
│   ├── layout/
│   │   ├── Header.tsx          # Top navigation bar
│   │   ├── Sidebar.tsx          # Left sidebar navigation
│   │   └── Layout.tsx           # Main layout wrapper
│   └── common/                  # Reusable components
├── pages/
│   ├── HomePage.tsx             # Dashboard/home
│   ├── NLToSQLPage.tsx          # NL to SQL interface (Chunk 10)
│   ├── QueryHistoryPage.tsx     # Query history
│   └── SettingsPage.tsx          # Settings
├── services/
│   └── api.ts                   # API client with axios
├── hooks/
│   └── useWebSocket.ts          # WebSocket subscription hook
├── types/
│   └── index.ts                 # TypeScript interfaces
├── App.tsx                      # Main app with routes
└── main.tsx                     # Entry point
```

## 🔄 Component Architecture

```
App.tsx
  └── Layout
      ├── Header
      │   └── Navigation links, user menu
      ├── Sidebar
      │   └── Menu items, navigation
      └── Main Content
          └── Router Routes
              ├── / → HomePage
              ├── /nl-to-sql → NLToSQLPage
              ├── /history → QueryHistoryPage
              └── /settings → SettingsPage
```

## 🎨 UI Design

- **Header**: Logo, navigation links, user menu (when auth is added)
- **Sidebar**: Collapsible menu with icons
- **Main Content**: Page content with proper spacing
- **Theme**: Modern, clean Material-UI theme
- **Responsive**: Works on mobile, tablet, desktop

## 🔌 API Integration

- **API Service**: Centralized axios instance with base URL
- **TypeScript Types**: Interfaces matching backend DTOs
- **Error Handling**: Consistent error handling across API calls
- **Loading States**: Loading indicators for async operations

## 📡 WebSocket Integration

- **useWebSocket Hook**: Subscribe to `/topic/nl-to-sql` and `/topic/query-execution`
- **Real-time Updates**: Display progress updates in UI
- **Connection Management**: Auto-reconnect, cleanup on unmount

## 🚀 Next Steps

**Chunk 10:** NL Query Interface
- Natural language input form
- Query submission
- Real-time progress display
- Results preview

---

**Chunk 9 Ready!** Beautiful, professional frontend foundation! 🎨

