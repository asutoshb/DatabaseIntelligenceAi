/**
 * TypeScript interfaces matching backend DTOs
 */

// Real-time Status Message (WebSocket)
export interface RealTimeStatusMessage {
  feature: string; // "NL_TO_SQL" | "QUERY_EXECUTION"
  requestId: string;
  stage: string;
  status: "IN_PROGRESS" | "SUCCESS" | "ERROR";
  message: string;
  data?: Record<string, any>;
  timestamp: string;
}

// NL to SQL Request
export interface NLToSQLRequest {
  databaseInfoId: number;
  naturalLanguageQuery: string;
  topK?: number;
  clientRequestId?: string;
}

// Schema Context (used in NL to SQL response)
export interface SchemaContext {
  tableName: string;
  schema: string;
  description?: string;
  columns?: Array<{
    name: string;
    type: string;
    description?: string;
  }>;
}

// NL to SQL Response
export interface NLToSQLResponse {
  sqlQuery: string;
  naturalLanguageQuery: string;
  relevantSchemas: SchemaContext[];
  explanation: string;
  isValid: boolean;
  validationErrors?: string[];
  databaseInfoId: number;
  requestId: string;
}

// Query Execution Request
export interface QueryExecutionRequest {
  databaseInfoId: number;
  sqlQuery: string;
  timeoutSeconds?: number;
  clientRequestId?: string;
}

// Query Execution Response
export interface QueryExecutionResponse {
  success: boolean;
  rows: Record<string, any>[];
  columns: string[];
  rowCount: number;
  executionTimeMs: number;
  executedAt: string;
  errorMessage?: string;
  sqlQuery: string;
  databaseInfoId: number;
  requestId: string;
}

// Health Check Response
export interface HealthResponse {
  status: string;
  message: string;
  timestamp: string;
}

// Database Info (for future use)
export interface DatabaseInfo {
  id: number;
  name: string;
  host: string;
  port: number;
  databaseName: string;
  username: string;
  // Note: password is never sent to frontend
}

