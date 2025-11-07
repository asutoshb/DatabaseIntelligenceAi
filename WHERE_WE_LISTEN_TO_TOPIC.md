# Where We Listen to `/topic` Responses

## 🎯 Your Question

**"So where we are listening to /topic responses sent by ws?"**

## ✅ The Answer

**The backend DOESN'T listen to `/topic` - it only SENDS to it!**

**The frontend (client) is where we SUBSCRIBE to `/topic` to receive messages.**

---

## 📍 Current Status

### Backend (Server) - ✅ Implemented
- **Sends** messages TO `/topic/nl-to-sql` and `/topic/query-execution`
- **Does NOT listen** to `/topic` (servers don't subscribe to topics)

### Frontend (Client) - ❌ Not Implemented Yet
- **Should SUBSCRIBE** to `/topic/nl-to-sql` and `/topic/query-execution`
- **Currently missing** - needs to be added

---

## 🔍 Backend Code (What We Have)

### RealTimeUpdateService.java - Server SENDS to `/topic`

```java
// Line 74: Server sends message TO /topic/nl-to-sql
messagingTemplate.convertAndSend("/topic/nl-to-sql", payload);
```

**What this does:**
- Server creates a message
- Server broadcasts it to `/topic/nl-to-sql`
- **All clients subscribed to this topic will receive it automatically**

**Key Point:** The server doesn't "listen" - it just broadcasts. The WebSocket broker handles delivery to subscribed clients.

---

## 🎯 Frontend Code (What We Need to Add)

### Current Frontend (App.tsx) - Missing WebSocket

The current `App.tsx` only has REST API calls, no WebSocket subscription.

### Example: How to Subscribe to `/topic` in Frontend

Here's what needs to be added to the frontend:

```typescript
// frontend/src/App.tsx (or create a new WebSocket hook)

import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';

function useWebSocketSubscription() {
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    // Create STOMP client
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws/websocket',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // When connected, subscribe to topics
    client.onConnect = () => {
      console.log('✅ WebSocket connected!');

      // Subscribe to NL to SQL updates
      client.subscribe('/topic/nl-to-sql', (message) => {
        const update = JSON.parse(message.body);
        console.log('📊 NL to SQL Update:', update);
        
        // Handle the update
        // update.feature = "NL_TO_SQL"
        // update.requestId = "abc-123"
        // update.stage = "RETRIEVING_SCHEMA", "COMPLETED", etc.
        // update.status = "IN_PROGRESS", "SUCCESS", "ERROR"
        // update.message = "Retrieving relevant schema context"
        // update.data = { schemaCount: 3, isValid: true, ... }
      });

      // Subscribe to Query Execution updates
      client.subscribe('/topic/query-execution', (message) => {
        const update = JSON.parse(message.body);
        console.log('⚡ Query Execution Update:', update);
        
        // Handle the update
        // update.feature = "QUERY_EXECUTION"
        // update.stage = "EXECUTING", "COMPLETED", etc.
        // update.data = { rowCount: 10, columns: [...], ... }
      });
    };

    client.onStompError = (frame) => {
      console.error('❌ WebSocket error:', frame);
    };

    // Activate the client
    client.activate();
    clientRef.current = client;

    // Cleanup on unmount
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, []);

  return clientRef.current;
}
```

---

## 🔄 Complete Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND (Server)                          │
│                                                              │
│  1. NLToSQLService.convertToSQL()                          │
│     ↓                                                        │
│  2. realTimeUpdateService.publishNlToSqlProgress()         │
│     ↓                                                        │
│  3. messagingTemplate.convertAndSend("/topic/nl-to-sql")    │
│     ↓                                                        │
│  4. WebSocket Broker broadcasts to all subscribers          │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                          │
                          │ (WebSocket message)
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Client)                         │
│                                                              │
│  1. client.subscribe('/topic/nl-to-sql', callback)         │
│     ↓                                                        │
│  2. callback receives message automatically                 │
│     ↓                                                        │
│  3. Update UI with progress (stage, status, message)       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📝 Step-by-Step: Where to Add Frontend Code

### Option 1: Create a Custom Hook

**File:** `frontend/src/hooks/useWebSocket.ts`

```typescript
import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';

interface RealTimeStatusMessage {
  feature: string;
  requestId: string;
  stage: string;
  status: string;
  message: string;
  data?: Record<string, any>;
  timestamp: string;
}

export function useWebSocket() {
  const [updates, setUpdates] = useState<RealTimeStatusMessage[]>([]);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws/websocket',
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      console.log('✅ WebSocket connected');

      // Subscribe to NL to SQL
      client.subscribe('/topic/nl-to-sql', (message) => {
        const update: RealTimeStatusMessage = JSON.parse(message.body);
        setUpdates(prev => [...prev, update]);
        console.log('NL to SQL:', update);
      });

      // Subscribe to Query Execution
      client.subscribe('/topic/query-execution', (message) => {
        const update: RealTimeStatusMessage = JSON.parse(message.body);
        setUpdates(prev => [...prev, update]);
        console.log('Query Execution:', update);
      });
    };

    client.activate();
    clientRef.current = client;

    return () => {
      clientRef.current?.deactivate();
    };
  }, []);

  return { updates, client: clientRef.current };
}
```

### Option 2: Use in App.tsx

**File:** `frontend/src/App.tsx`

```typescript
import { useWebSocket } from './hooks/useWebSocket';

function App() {
  const { updates } = useWebSocket();
  
  // Filter updates by requestId if needed
  const nlToSqlUpdates = updates.filter(u => u.feature === 'NL_TO_SQL');
  const queryUpdates = updates.filter(u => u.feature === 'QUERY_EXECUTION');

  return (
    <Container>
      {/* Display updates */}
      {nlToSqlUpdates.map(update => (
        <div key={update.timestamp}>
          {update.stage}: {update.message}
        </div>
      ))}
    </Container>
  );
}
```

---

## 📦 Required Frontend Dependencies

To use WebSocket in the frontend, you need to install STOMP client:

```bash
cd frontend
npm install @stomp/stompjs
# or
yarn add @stomp/stompjs
```

---

## ✅ Summary

| Location | Action | Status |
|----------|--------|--------|
| **Backend** | Sends TO `/topic/**` | ✅ Implemented |
| **Backend** | Listens to `/topic/**` | ❌ Not needed (servers don't subscribe) |
| **Frontend** | Subscribes TO `/topic/**` | ❌ **Not implemented yet** |
| **Frontend** | Receives FROM `/topic/**` | ❌ **Not implemented yet** |

**Answer to your question:**
- **Backend:** We DON'T listen to `/topic` - we only send to it
- **Frontend:** This is where we SHOULD subscribe to `/topic` to receive messages (but it's not implemented yet)

The WebSocket broker automatically delivers messages from `/topic` to all subscribed clients. The backend just broadcasts; the frontend needs to subscribe to receive them.

