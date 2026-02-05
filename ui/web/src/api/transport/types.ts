import type { GameResponse, GameAction } from "../../types";

export type ConnectionState =
    | "disconnected"
    | "connecting"
    | "connected-websocket"
    | "connected-longpoll"
    | "connected-shortpoll"
    | "reconnecting";

export interface TransportOptions {
    preferWebSocket?: boolean;      // default: true
    longPollTimeout?: number;       // default: 30 seconds
    shortPollInterval?: number;     // default: 2 seconds
    maxRetries?: number;            // default: 3
    onStateChange?: (state: GameResponse) => void;
    onConnectionChange?: (status: ConnectionState) => void;
    onError?: (error: Error) => void;
}

export interface WebSocketMessage {
    type: "state" | "error";
    version?: number;
    data?: GameResponse;
    message?: string;
}

export const DEFAULT_OPTIONS: Required<Omit<TransportOptions, "onStateChange" | "onConnectionChange" | "onError">> = {
    preferWebSocket: true,
    longPollTimeout: 30,
    shortPollInterval: 2,
    maxRetries: 3,
};
