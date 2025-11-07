package com.databaseai.service;

import com.databaseai.dto.RealTimeStatusMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Real-time Update Service (Chunk 8)
 *
 * Sends WebSocket notifications to subscribed clients using STOMP topics.
 *
 * How it works:
 * - Server SENDS messages TO `/topic/**` (this service does that)
 * - Clients SUBSCRIBE TO `/topic/**` to RECEIVE messages (frontend does that)
 * - Think of `/topic` as a broadcast channel: server broadcasts, clients tune in
 *
 * Topics we broadcast to:
 * - /topic/nl-to-sql (for NL to SQL conversion progress)
 * - /topic/query-execution (for SQL execution progress)
 *
 * Example client subscription (frontend):
 *   client.subscribe('/topic/nl-to-sql', (message) => {
 *       const update = JSON.parse(message.body);
 *       console.log('Progress:', update.stage);
 *   });
 */
@Service
public class RealTimeUpdateService {

    public static final String FEATURE_NL_TO_SQL = "NL_TO_SQL";
    public static final String FEATURE_QUERY_EXECUTION = "QUERY_EXECUTION";

    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    private final SimpMessagingTemplate messagingTemplate;

    public RealTimeUpdateService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /* ============================ NL TO SQL ================================= */

    public void publishNlToSqlProgress(String requestId, String stage, String message, Map<String, Object> data) {
        send("/topic/nl-to-sql", FEATURE_NL_TO_SQL, requestId, stage, STATUS_IN_PROGRESS, message, data);
    }

    public void publishNlToSqlSuccess(String requestId, String stage, String message, Map<String, Object> data) {
        send("/topic/nl-to-sql", FEATURE_NL_TO_SQL, requestId, stage, STATUS_SUCCESS, message, data);
    }

    public void publishNlToSqlError(String requestId, String stage, String message, Map<String, Object> data) {
        send("/topic/nl-to-sql", FEATURE_NL_TO_SQL, requestId, stage, STATUS_ERROR, message, data);
    }

    /* ========================= QUERY EXECUTION ============================== */

    public void publishQueryExecutionProgress(String requestId, String stage, String message, Map<String, Object> data) {
        send("/topic/query-execution", FEATURE_QUERY_EXECUTION, requestId, stage, STATUS_IN_PROGRESS, message, data);
    }

    public void publishQueryExecutionSuccess(String requestId, String stage, String message, Map<String, Object> data) {
        send("/topic/query-execution", FEATURE_QUERY_EXECUTION, requestId, stage, STATUS_SUCCESS, message, data);
    }

    public void publishQueryExecutionError(String requestId, String stage, String message, Map<String, Object> data) {
        send("/topic/query-execution", FEATURE_QUERY_EXECUTION, requestId, stage, STATUS_ERROR, message, data);
    }

    /* ============================== INTERNAL ================================ */

    private void send(
            String destination,
            String feature,
            String requestId,
            String stage,
            String status,
            String message,
            Map<String, Object> data
    ) {
        RealTimeStatusMessage payload = new RealTimeStatusMessage(feature, requestId, stage, status, message, data);
        messagingTemplate.convertAndSend(destination, payload);
    }
}


