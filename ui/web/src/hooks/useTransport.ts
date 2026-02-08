import { useEffect, useRef, useState, useCallback } from "react";
import { TransportClient, type ConnectionState, type TransportOptions } from "../api/transport";
import type { GameResponse } from "../types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

interface UseTransportResult {
    state: GameResponse | null;
    connectionStatus: ConnectionState;
    error: Error | null;
}

/**
 * Hook for managing real-time connection to a game.
 */
export function useTransport(
    gameId: string | null,
    playerId?: string | null,
    options?: Partial<TransportOptions>
): UseTransportResult {
    const [state, setState] = useState<GameResponse | null>(null);
    const [connectionStatus, setConnectionStatus] = useState<ConnectionState>("disconnected");
    const [error, setError] = useState<Error | null>(null);
    const clientRef = useRef<TransportClient | null>(null);

    const handleStateChange = useCallback((newState: GameResponse) => {
        setState(newState);
        setError(null);
    }, []);

    const handleConnectionChange = useCallback((status: ConnectionState) => {
        setConnectionStatus(status);
    }, []);

    const handleError = useCallback((err: Error) => {
        setError(err);
    }, []);

    useEffect(() => {
        if (!gameId) {
            setState(null);
            setConnectionStatus("disconnected");
            return;
        }

        const client = new TransportClient(API_BASE_URL, {
            ...options,
            onStateChange: handleStateChange,
            onConnectionChange: handleConnectionChange,
            onError: handleError,
        });

        clientRef.current = client;
        client.connect(gameId, playerId).catch(handleError);

        return () => {
            client.disconnect();
            clientRef.current = null;
        };
    }, [gameId, playerId, options, handleStateChange, handleConnectionChange, handleError]);

    return { state, connectionStatus, error };
}
