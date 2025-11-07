# Chunk 8 Testing Guide – Real-time Updates

## 🧪 Purpose
Verify that WebSocket broadcasts, correlation IDs, and REST responses all stay in sync for NL ➜ SQL conversion and SQL execution flows.

---

## ✅ Pre-requisites

- Backend running on `http://localhost:8080`
- WebSocket/STOMP client available (choose one):
  - Browser devtools with [SockJS/STOMP console script](https://stomp-js.github.io/guide/stompjs/)
  - `wscat` or `websocat`
  - Postman (supports WebSocket)

---

## 🔌 Connect to WebSocket

### Using `wscat`

```bash
wscat -c ws://localhost:8080/ws/websocket
```

> NOTE: SockJS adds `/websocket` suffix during negotiation. If your client does not handle SockJS, use a STOMP client (e.g., stomp.js) that understands the protocol. For quick checks you can also hit `http://localhost:8080/ws/info` to confirm the endpoint is alive.

### Using `stomp.js` in browser console

```javascript
const client = new StompJs.Client({
  brokerURL: "ws://localhost:8080/ws/websocket"
});

client.onConnect = () => {
  client.subscribe('/topic/nl-to-sql', message => {
    console.log('NL ➜ SQL', JSON.parse(message.body));
  });

  client.subscribe('/topic/query-execution', message => {
    console.log('Query Execution', JSON.parse(message.body));
  });
};

client.activate();
```

---

## 🧭 Test Scenarios

### 1. NL ➜ SQL Success Flow

1. (Optional) Pick a custom `requestId`:
   ```bash
   export REQUEST_ID=$(uuidgen)
   ```
2. Call REST endpoint:
   ```bash
   curl -X POST http://localhost:8080/api/nl-to-sql/convert \
     -H "Content-Type: application/json" \
     -d '{
       "databaseInfoId": 1,
       "naturalLanguageQuery": "List top 5 customers by revenue",
       "clientRequestId": "'$REQUEST_ID'"
     }'
   ```
3. Observe WebSocket logs:
   - Expect stages: `REQUEST_RECEIVED`, `RETRIEVING_SCHEMA`, `PROMPT_BUILDING`, `LLM_CALL`, `VALIDATION`, `COMPLETED`
   - Each payload features `feature = NL_TO_SQL`, matching `requestId`, helpful metadata (`schemaCount`, `isValid`, etc.)
4. Confirm REST response includes the same `requestId` and a finished SQL query.

### 2. NL ➜ SQL Failure Path

1. Trigger an error (e.g., use unknown `databaseInfoId`):
   ```bash
   curl -X POST http://localhost:8080/api/nl-to-sql/convert \
     -H "Content-Type: application/json" \
     -d '{
       "databaseInfoId": 9999,
       "naturalLanguageQuery": "List top 5 customers",
       "clientRequestId": "chunk8-error-test"
     }'
   ```
2. Expected WebSocket event:
   - `stage = DATABASE_LOOKUP_FAILED`
   - `status = ERROR`
   - `message` echoes the failure reason
3. REST response returns `500` with `requestId = chunk8-error-test`.

### 3. Query Execution Success Flow

1. Use SQL generated from scenario #1:
   ```bash
   curl -X POST http://localhost:8080/api/query-execution/execute \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT_TOKEN>" \
     -d '{
       "databaseInfoId": 1,
       "sqlQuery": "SELECT * FROM test_customers LIMIT 5",
       "clientRequestId": "'$REQUEST_ID'"
     }'
   ```
2. WebSocket expectations:
   - Stages: `REQUEST_RECEIVED`, `VALIDATED`, `CONNECTING`, `EXECUTING`, `COMPLETED`
   - Success payload contains `rowCount`, `columns`
3. REST response returns rows, `success = true`, and same `requestId`.

### 4. Query Execution Timeout/Error

1. Submit intentionally slow/invalid query:
   ```bash
   curl -X POST http://localhost:8080/api/query-execution/execute \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT_TOKEN>" \
     -d '{
       "databaseInfoId": 1,
       "sqlQuery": "SELECT pg_sleep(35)",
       "timeoutSeconds": 5,
       "clientRequestId": "chunk8-timeout"
     }'
   ```
2. WebSocket expectations:
   - Final event with `stage = TIMEOUT`, `status = ERROR`
   - Metadata includes enforced timeout seconds
3. REST response returns `400` with message `Query timeout...` and `requestId = chunk8-timeout`.

---

## 🛠 Troubleshooting

| Issue | Fix |
| --- | --- |
| No messages received | Ensure client subscribes **after** connection, check console for handshake failures |
| SockJS 404 on `/ws` | Backend not running or Spring Boot still starting |
| `403 Forbidden` during handshake | Verify `SecurityConfig` change deployed, restart backend |
| REST response has different `requestId` | Confirm single backend instance, check if client overrides ID per request |
| Messages arrive but not filtered | Frontend must filter by `requestId` before rendering |

---

## ✅ Regression Checklist

- Existing REST responses still return data even without WebSocket client
- Authentication flow unchanged (WebSocket endpoints stay open)
- NL ➜ SQL and Query Execution controllers continue to validate inputs

Chunk 8 testing ensures our new real-time layer is reliable and ready for frontend integration.


