package com.databaseai.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Real-time Status Message DTO (Chunk 8)
 *
 * Represents a WebSocket payload sent to clients to describe
 * progress or status updates for long-running operations.
 */
public class RealTimeStatusMessage {

    private String feature;          // e.g. NL_TO_SQL, QUERY_EXECUTION
    private String requestId;        // Correlates HTTP request with WebSocket messages
    private String stage;            // Logical stage (STARTED, COMPLETED, ERROR, etc.)
    private String status;           // IN_PROGRESS, SUCCESS, ERROR
    private String message;          // Human readable description
    private Map<String, Object> data; // Optional payload (progress, metadata)
    private Instant timestamp;       // When message was created

    public RealTimeStatusMessage() {
        this.timestamp = Instant.now();
    }

    public RealTimeStatusMessage(
            String feature,
            String requestId,
            String stage,
            String status,
            String message,
            Map<String, Object> data
    ) {
        this.feature = feature;
        this.requestId = requestId;
        this.stage = stage;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}


