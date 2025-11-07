# WebSocket Message Flow Explained

## 🎯 The Confusion

You see:
- **Comment says:** "Server publishes updates to `/topic/**`"
- **Code does:** `send("/topic/nl-to-sql", ...)` 

**Question:** Is the server sending TO `/topic` or is the client sending?

## ✅ The Answer

**The server is sending TO `/topic`**, and **clients subscribe TO `/topic`** to receive those messages.

Think of `/topic` like a **TV channel** or **radio station**:
- **Server = TV Station** (broadcasts to the channel)
- **Clients = TV Viewers** (tune in to watch the channel)
- **`/topic/nl-to-sql` = Channel Name** (e.g., "Channel 5")

---

## 📡 How It Actually Works

### 1. **Server Side (Backend)**

```java
// Server SENDS messages TO /topic/nl-to-sql
messagingTemplate.convertAndSend("/topic/nl-to-sql", payload);
```

**What this does:**
- Server creates a message
- Server broadcasts it to the `/topic/nl-to-sql` channel
- **All clients subscribed to this channel will receive it**

### 2. **Client Side (Frontend)**

```javascript
// Client SUBSCRIBES to /topic/nl-to-sql to RECEIVE messages
client.subscribe('/topic/nl-to-sql', (message) => {
    console.log('Received:', message.body);
});
```

**What this does:**
- Client says: "I want to listen to `/topic/nl-to-sql`"
- When server broadcasts to this topic, client automatically receives it

---

## 🔄 Complete Flow Example

```
┌─────────────┐                    ┌─────────────┐
│   SERVER    │                    │   CLIENT    │
│  (Backend)  │                    │ (Frontend)  │
└──────┬──────┘                    └──────┬──────┘
       │                                 │
       │  1. Client connects to /ws      │
       │◄────────────────────────────────│
       │                                 │
       │  2. Client subscribes           │
       │◄────────────────────────────────│
       │  SUBSCRIBE /topic/nl-to-sql    │
       │                                 │
       │  3. Server sends message        │
       │─────────────────────────────────►│
       │  SEND to /topic/nl-to-sql      │
       │  { "stage": "COMPLETED", ... }  │
       │                                 │
       │  4. Client receives message     │
       │     (automatically delivered)   │
       │                                 │
```

---

## 📚 STOMP Destination Types

### `/topic/**` - **Server → Client** (Broadcast)
- **Server publishes** messages here
- **Clients subscribe** to receive them
- **One-to-many**: One server message goes to all subscribed clients
- **Example:** Progress updates, notifications

### `/app/**` - **Client → Server** (Point-to-Point)
- **Clients send** messages here
- **Server listens** to process them
- **Many-to-one**: Multiple clients can send to server
- **Example:** Chat messages, commands (we don't use this yet)

---

## 🔍 In Our Code

### WebSocketConfig.java

```java
registry.enableSimpleBroker("/topic");  // Server can broadcast to /topic/**
registry.setApplicationDestinationPrefixes("/app");  // Clients can send to /app/**
```

**Translation:**
- `enableSimpleBroker("/topic")` = "Enable a message broker that handles `/topic/**` destinations"
- This allows the server to **send** messages to `/topic/**`
- Clients can **subscribe** to `/topic/**` to receive them

### RealTimeUpdateService.java

```java
send("/topic/nl-to-sql", ...);  // Server sends TO this topic
```

**Translation:**
- Server is **publishing** a message to the `/topic/nl-to-sql` channel
- All clients who subscribed to `/topic/nl-to-sql` will receive it

---

## 💡 Key Takeaway

**`/topic` is a BROADCAST channel:**
- ✅ Server **sends** messages **TO** `/topic/**`
- ✅ Clients **subscribe** **TO** `/topic/**` to **receive** messages
- ✅ It's like a radio station: server broadcasts, clients tune in

**The word "to" in "Server publishes updates to /topic" means:**
- Server is the **sender**
- `/topic` is the **destination/channel**
- Clients are the **receivers**

---

## 🎬 Real Example

**Step 1:** Client connects and subscribes
```javascript
// Frontend code
client.subscribe('/topic/nl-to-sql', (message) => {
    const update = JSON.parse(message.body);
    console.log('Progress:', update.stage);  // "RETRIEVING_SCHEMA", "COMPLETED", etc.
});
```

**Step 2:** Client makes REST request
```javascript
fetch('/api/nl-to-sql/convert', { ... });
```

**Step 3:** Server processes and broadcasts
```java
// Backend code (in NLToSQLService)
realTimeUpdateService.publishNlToSqlProgress(
    requestId, 
    "RETRIEVING_SCHEMA", 
    "Retrieving relevant schema context",
    metadata
);
// This calls: messagingTemplate.convertAndSend("/topic/nl-to-sql", payload)
```

**Step 4:** Client automatically receives the message
```javascript
// Frontend automatically gets the message in the subscribe callback
// No polling needed! Real-time update received.
```

---

## ✅ Summary

| Direction | Destination | Who Sends | Who Receives |
|-----------|------------|-----------|--------------|
| **Server → Client** | `/topic/**` | Server (backend) | Clients (frontend) |
| **Client → Server** | `/app/**` | Clients (frontend) | Server (backend) |

**In our app:**
- We use `/topic/**` for server-to-client updates (progress, status)
- We don't use `/app/**` yet (but it's configured for future use)

The confusion was thinking "client sends to topic" - but actually:
- **Server sends TO topic** (broadcasts)
- **Clients subscribe TO topic** (receive)

Both use "TO" but mean different things:
- Server: "I'm sending this message **TO** the topic"
- Client: "I'm subscribing **TO** the topic to receive messages"

