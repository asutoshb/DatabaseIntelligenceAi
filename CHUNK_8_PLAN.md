# Chunk 8: Real-time Updates (WebSocket) - Development Plan

## 🎯 Goal
Deliver real-time status updates for long-running operations (NL ➜ SQL conversion and SQL execution) using WebSockets + STOMP.

## 📋 What We'll Build

1. **WebSocket Transport** – STOMP endpoint (`/ws`) with `/topic/**` broadcasts
2. **Status Message Model** – Standard payload with request correlation + metadata
3. **Notification Service** – Helper to publish progress/success/error events
4. **NL ➜ SQL Progress Signals** – Emit stage updates during conversion pipeline
5. **Query Execution Signals** – Emit progress + result metadata during SQL execution
6. **API Enhancements** – Request/response correlation IDs exposed to clients
7. **Security / CORS Updates** – Ensure WebSocket endpoints remain publicly accessible

## 🔧 Technologies We'll Use

- **Spring WebSocket + STOMP** – Real-time messaging over WebSockets/SockJS
- **SimpMessagingTemplate** – Server-side push utility for STOMP topics
- **Spring Security Rules** – Allow unauthenticated WebSocket handshakes
- **UUID Correlation IDs** – Match HTTP responses with WebSocket events

## 📝 Step-by-Step

1. ✅ Add WebSocket/STOMP configuration (`WebSocketConfig`)
2. ✅ Create reusable status DTO (`RealTimeStatusMessage`)
3. ✅ Build `RealTimeUpdateService` for topic publishing
4. ✅ Extend NL ➜ SQL service/controller with progress + request IDs
5. ✅ Extend Query Execution service/controller with progress + request IDs
6. ✅ Update security + CORS to expose `/ws`, `/app`, `/topic`
7. ✅ Document client integration + testing strategy

## 🎓 What You'll Learn

- **How STOMP over WebSocket works** inside Spring Boot
- **Designing event payloads** that clients can render immediately
- **Correlating asynchronous events** with synchronous API calls
- **Safely broadcasting metadata** without exposing sensitive details

## 🔄 End-to-End Flow

1. Frontend creates a `requestId` (or uses server-generated one in the response)
2. Frontend subscribes to `/topic/nl-to-sql` or `/topic/query-execution`
3. Client sends REST request (`/api/nl-to-sql/convert` or `/api/query-execution/execute`)
4. Backend publishes progress events with the shared `requestId`
5. Client filters WebSocket messages by `requestId` to render live updates
6. REST response includes final `requestId` + results for confirmation

## 🚀 Next Steps

**Chunk 9 (Proposed):** Real-time job history + persistence
- Store status history in Postgres
- Build dashboard endpoint for past executions
- Replay events from storage for reconnecting clients

---

**Chunk 8 Ready!** Real-time visibility unlocked for NL ➜ SQL + SQL execution 💡


