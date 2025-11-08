/**
 * WebSocket Hook for Real-time Updates
 * 
 * Subscribes to STOMP topics for real-time progress updates:
 * - /topic/nl-to-sql (for NL to SQL conversion progress)
 * - /topic/query-execution (for SQL execution progress)
 */

import { useEffect, useRef, useState, useCallback } from 'react';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { RealTimeStatusMessage } from '../types';

interface UseWebSocketOptions {
  onNlToSqlUpdate?: (message: RealTimeStatusMessage) => void;
  onQueryExecutionUpdate?: (message: RealTimeStatusMessage) => void;
  onConnect?: () => void;
  onDisconnect?: () => void;
  onError?: (error: any) => void;
}

interface UseWebSocketReturn {
  isConnected: boolean;
  client: Client | null;
  connect: () => void;
  disconnect: () => void;
  subscribeToRequest: (requestId: string, callback: (message: RealTimeStatusMessage) => void) => () => void;
}

/**
 * Custom hook for WebSocket connection and subscriptions
 * 
 * @param options - Callback functions for different events
 * @returns WebSocket connection state and control functions
 */
export function useWebSocket(options: UseWebSocketOptions = {}): UseWebSocketReturn {
  const [isConnected, setIsConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<Map<string, any>>(new Map());

  const {
    onNlToSqlUpdate,
    onQueryExecutionUpdate,
    onConnect,
    onDisconnect,
    onError,
  } = options;

  // WebSocket URL (SockJS endpoint)
  // Note: With SockJS, we use HTTP protocol (not WS)
  // Backend has context-path=/api, so WebSocket endpoint is at /api/ws
  // SockJS will handle the protocol upgrade automatically
  const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/api/ws';

  const connect = useCallback(() => {
    if (clientRef.current?.active) {
      return; // Already connected
    }

    // Create STOMP client with SockJS
    // webSocketFactory should return a new SockJS instance
    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (_str) => {
        // Suppress all STOMP debug messages - they're too verbose
        // Only log actual errors via onStompError callback
        // This prevents "connected to server undefined" and other noise
      },
    });

    client.onConnect = () => {
      const timestamp = new Date().toLocaleTimeString();
      console.log(`[${timestamp}] ✅ WebSocket connected`);
      setIsConnected(true);
      onConnect?.();

      // Subscribe to NL to SQL topic
      const nlToSqlSubscription = client.subscribe('/topic/nl-to-sql', (message: IMessage) => {
        try {
          const update: RealTimeStatusMessage = JSON.parse(message.body);
          // Only log updates in development, and only important ones
          if (import.meta.env.DEV && (update.status === 'ERROR' || update.status === 'SUCCESS')) {
            console.log(`📊 NL to SQL: ${update.stage} - ${update.status}`);
          }
          onNlToSqlUpdate?.(update);
        } catch (error) {
          console.error('Error parsing NL to SQL message:', error);
        }
      });

      // Subscribe to Query Execution topic
      const queryExecutionSubscription = client.subscribe(
        '/topic/query-execution',
        (message: IMessage) => {
          try {
            const update: RealTimeStatusMessage = JSON.parse(message.body);
            // Only log updates in development, and only important ones
            if (import.meta.env.DEV && (update.status === 'ERROR' || update.status === 'SUCCESS')) {
              console.log(`⚡ Query Execution: ${update.stage} - ${update.status}`);
            }
            onQueryExecutionUpdate?.(update);
          } catch (error) {
            console.error('Error parsing Query Execution message:', error);
          }
        }
      );

      // Store subscriptions for cleanup
      subscriptionsRef.current.set('nl-to-sql', nlToSqlSubscription);
      subscriptionsRef.current.set('query-execution', queryExecutionSubscription);
    };

    client.onStompError = (frame) => {
      const timestamp = new Date().toLocaleTimeString();
      console.error(`[${timestamp}] ❌ STOMP Error:`, frame);
      setIsConnected(false);
      onError?.(frame);
    };

    client.onWebSocketClose = () => {
      // Use functional update to check current state
      setIsConnected((prevConnected) => {
        if (prevConnected) {
          const timestamp = new Date().toLocaleTimeString();
          console.log(`[${timestamp}] 🔌 WebSocket closed`);
        }
        return false;
      });
      onDisconnect?.();
    };

    client.onDisconnect = () => {
      // Use functional update to check current state
      setIsConnected((prevConnected) => {
        if (prevConnected) {
          const timestamp = new Date().toLocaleTimeString();
          console.log(`[${timestamp}] 🔌 WebSocket disconnected`);
        }
        return false;
      });
      onDisconnect?.();
    };

    client.activate();
    clientRef.current = client;
  }, [wsUrl, onNlToSqlUpdate, onQueryExecutionUpdate, onConnect, onDisconnect, onError]);

  const disconnect = useCallback(() => {
    if (clientRef.current) {
      // Unsubscribe from all topics
      subscriptionsRef.current.forEach((subscription) => {
        subscription.unsubscribe();
      });
      subscriptionsRef.current.clear();

      // Deactivate client
      clientRef.current.deactivate();
      clientRef.current = null;
      setIsConnected(false);
    }
  }, []);

  /**
   * Subscribe to updates for a specific request ID
   * Useful for filtering updates by requestId
   * 
   * @param requestId - The request ID to filter by
   * @param callback - Callback function when update is received
   * @returns Unsubscribe function
   * 
   * Note: This is a simplified implementation. In production, you'd want
   * a more robust subscription manager that tracks multiple request-specific subscriptions.
   */
  const subscribeToRequest = useCallback(
    (_requestId: string, _callback: (message: RealTimeStatusMessage) => void) => {
      // TODO: Implement request-specific subscription filtering
      // For now, all updates are handled by the global callbacks (onNlToSqlUpdate, onQueryExecutionUpdate)
      // Clients can filter by requestId in their callback functions
      return () => {
        // Cleanup would go here
      };
    },
    []
  );

  // Auto-connect on mount, cleanup on unmount
  useEffect(() => {
    connect();

    return () => {
      disconnect();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // Only run once on mount - connect/disconnect are stable functions

  return {
    isConnected,
    client: clientRef.current,
    connect,
    disconnect,
    subscribeToRequest,
  };
}

