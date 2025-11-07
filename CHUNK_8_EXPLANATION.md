# Chunk 8: Real-time Updates (WebSocket) - Detailed Explanation

## 🎯 What We Built

We enabled **live status tracking** for the two longest-running backend workflows:

1. **Natural Language ➜ SQL conversion** (RAG + LLM pipeline)
2. **SQL execution against customer databases**

Clients can now subscribe to WebSocket topics and watch each stage unfold without polling REST endpoints.

---

## 🧠 Core Concepts

### 1. STOMP over WebSocket (Spring)

- **STOMP** = Simple Text Oriented Messaging Protocol
- Provides pub/sub semantics on top of WebSockets (topics, destinations)
- Spring Boot pairs STOMP with SockJS for graceful fallbacks

**How it works in our app:**
- Clients connect to `/ws`
- Server broadcasts on `/topic/{feature}` (e.g. `/topic/nl-to-sql`)
- Clients optionally send messages to `/app/**` (not required yet)

### 2. Correlation IDs

- Each REST request carries/receives a `requestId`
- All WebSocket notifications for that request include the same `requestId`
- Frontend filters incoming messages by ID to display the correct timeline

**Sources:**
- Client-supplied via `clientRequestId` (optional)
- Server-generated UUID when client omits it

### 3. Status Message Envelope

We standardised the payload in `RealTimeStatusMessage`:

- `feature` – `NL_TO_SQL` or `QUERY_EXECUTION`
- `requestId` – correlation identifier
- `stage` – semantic milestone (`REQUEST_RECEIVED`, `EXECUTING`, `COMPLETED`, ...)
- `status` – high-level state (`IN_PROGRESS`, `SUCCESS`, `ERROR`)
- `message` – friendly description for UI display
- `data` – key/value metadata (row counts, validation errors, etc.)
- `timestamp` – server-side creation time

### 4. Broadcast Service (`RealTimeUpdateService`)

`SimpMessagingTemplate` is the workhorse that pushes STOMP frames. We wrapped it inside `RealTimeUpdateService` to keep controllers/services clean:

- `publishNlToSqlProgress / Success / Error`
- `publishQueryExecutionProgress / Success / Error`

Each helper method maps to a STOMP topic (`/topic/nl-to-sql`, `/topic/query-execution`).

### 5. Secure but Accessible Channels

JWT-secured REST APIs remain protected, but WebSocket handshakes need to be publicly reachable so the browser can connect **before** login (or while token is being obtained). We explicitly permit:

- `/ws/**` – STOMP handshake endpoint
- `/topic/**` + `/app/**` – STOMP broker destinations

CORS also now includes `allowedOriginPattern="*"` to prevent SockJS preflight failures during local development.

---

## ⚙️ Implementation Highlights

### WebSocket Configuration

```
registerStompEndpoints → /ws (SockJS enabled)
configureMessageBroker → /topic (broker), /app (application prefix)
```

### NL ➜ SQL Flow Instrumentation

- Emit events for: request received, schema retrieval, prompt building, LLM call, validation, completion, errors
- Attach metadata: schema count, validation status, database ID
- Response now includes `requestId`

### Query Execution Flow Instrumentation

- Emit events for: request received, validation, DB lookup, connection, execution, completion, timeouts/errors
- Attach metadata: timeout, SQL preview, row/column counts, SQLState on errors
- Response now includes `requestId`

### DTO Enhancements

- `NLToSQLRequest` + `QueryExecutionRequest` accept optional `clientRequestId`
- `NLToSQLResponse` + `QueryExecutionResponse` expose `requestId`
- `RealTimeStatusMessage` created as reusable payload

---

## 📡 Client Integration Cheatsheet

1. Generate a `requestId` on the frontend before calling REST
2. Subscribe to `/topic/nl-to-sql` or `/topic/query-execution`
3. Filter incoming messages by matching `feature` + `requestId`
4. Render timeline chips/cards using `stage`, `status`, `message`, `data`
5. Send REST request and await final response (still authoritative result)

**Tip:** Use the REST response `requestId` if you prefer server-generated identifiers.

---

## ✅ Benefits

- **Zero polling** – UI reacts immediately to backend progress
- **Consistent messaging format** – any feature can plug into the same service
- **Better UX** – users see where time is spent (RAG, LLM, DB execution)
- **Future-friendly** – ready for dashboards, notifications, multi-tenant clients

---

## 🔭 Where We Can Go Next

- Persist status history in Postgres for audits + offline dashboards
- Push notifications to specific users (per-session queues)
- Surface retry suggestions when rate limits / timeouts happen
- Include overall job progress percentages for longer multi-step tasks

---

Chunk 8 gives us the communication backbone for a responsive AI database assistant. 🚀


