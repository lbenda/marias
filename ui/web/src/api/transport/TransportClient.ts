import type { GameResponse } from "../../types";
import { PollingTransport } from "./PollingTransport";
import { WebSocketTransport } from "./WebSocketTransport";
import type { ConnectionState, TransportOptions, WebSocketMessage } from "./types";
import { DEFAULT_OPTIONS } from "./types";

/**
 * Transport client that automatically negotiates best available
 * communication method: WebSocket -> Long Polling -> Short Polling.
 */
export class TransportClient {
    private gameId: string | null = null;
    private playerId: string | null = null;
    private options: Required<Omit<TransportOptions, "onStateChange" | "onConnectionChange" | "onError">> & TransportOptions;
    private connectionState: ConnectionState = "disconnected";
    private wsTransport: WebSocketTransport;
    private pollingTransport: PollingTransport;
    private pollTimer: ReturnType<typeof setTimeout> | null = null;
    private reconnectAttempts = 0;
    private isDestroyed = false;

    constructor(
        private baseUrl: string = "",
        options: TransportOptions = {}
    ) {
        this.options = { ...DEFAULT_OPTIONS, ...options };
        this.wsTransport = new WebSocketTransport(baseUrl);
        this.pollingTransport = new PollingTransport(baseUrl);
    }

    /**
     * Connect to a game and start receiving updates.
     */
    async connect(gameId: string, playerId?: string | null): Promise<void> {
        this.gameId = gameId;
        this.playerId = playerId ?? null;
        this.isDestroyed = false;
        this.reconnectAttempts = 0;

        await this.establishConnection();
    }

    /**
     * Disconnect from the game.
     */
    disconnect(): void {
        this.isDestroyed = true;
        this.gameId = null;
        this.playerId = null;
        this.stopPolling();
        this.wsTransport.close();
        this.setConnectionState("disconnected");
    }

    /**
     * Get current connection state.
     */
    getConnectionState(): ConnectionState {
        return this.connectionState;
    }

    private setConnectionState(state: ConnectionState): void {
        if (this.connectionState !== state) {
            this.connectionState = state;
            this.options.onConnectionChange?.(state);
        }
    }

    private async establishConnection(): Promise<void> {
        if (this.isDestroyed || !this.gameId) return;

        this.setConnectionState("connecting");

        if (this.options.preferWebSocket) {
            try {
                await this.connectWebSocket();
                return;
            } catch (error) {
                console.log("WebSocket failed, falling back to polling:", error);
            }
        }

        // Fall back to polling
        this.startPolling(true);
    }

    private async connectWebSocket(): Promise<void> {
        if (!this.gameId) throw new Error("No game ID");

        this.wsTransport = new WebSocketTransport(this.baseUrl);

        this.wsTransport.onMessage((message: WebSocketMessage) => {
            if (message.type === "state" && message.data) {
                this.options.onStateChange?.(message.data);
            } else if (message.type === "error") {
                this.options.onError?.(new Error(message.message ?? "Unknown error"));
            }
        });

        this.wsTransport.onClose((event) => {
            if (!this.isDestroyed) {
                console.log("WebSocket closed:", event.code, event.reason);
                this.handleDisconnect();
            }
        });

        this.wsTransport.onError(() => {
            if (!this.isDestroyed) {
                this.handleDisconnect();
            }
        });

        await this.wsTransport.connect(this.gameId, this.playerId);
        this.reconnectAttempts = 0;
        this.setConnectionState("connected-websocket");
    }

    private startPolling(tryLongPoll: boolean): void {
        if (this.isDestroyed || !this.gameId) return;

        const poll = async () => {
            if (this.isDestroyed || !this.gameId) return;

            try {
                const state = await this.pollingTransport.poll(
                    this.gameId,
                    tryLongPoll,
                    this.options.longPollTimeout,
                    this.playerId
                );

                if (state) {
                    this.reconnectAttempts = 0;
                    this.options.onStateChange?.(state);
                }

                // Update connection state based on whether long poll worked
                if (tryLongPoll) {
                    this.setConnectionState("connected-longpoll");
                } else {
                    this.setConnectionState("connected-shortpoll");
                }

                // Schedule next poll
                if (!this.isDestroyed) {
                    const interval = tryLongPoll ? 0 : this.options.shortPollInterval * 1000;
                    this.pollTimer = setTimeout(poll, interval);
                }
            } catch (error) {
                if (this.isDestroyed) return;

                // If long poll fails, try short poll
                if (tryLongPoll && error instanceof Error && error.name !== "AbortError") {
                    console.log("Long polling failed, switching to short polling");
                    this.startPolling(false);
                    return;
                }

                this.handleDisconnect();
            }
        };

        poll();
    }

    private stopPolling(): void {
        if (this.pollTimer) {
            clearTimeout(this.pollTimer);
            this.pollTimer = null;
        }
        this.pollingTransport.abort();
    }

    private handleDisconnect(): void {
        if (this.isDestroyed) return;

        this.stopPolling();
        this.wsTransport.close();
        this.pollingTransport.reset();

        this.reconnectAttempts++;
        if (this.reconnectAttempts > this.options.maxRetries) {
            this.setConnectionState("disconnected");
            this.options.onError?.(new Error("Max reconnection attempts reached"));
            return;
        }

        this.setConnectionState("reconnecting");

        // Exponential backoff
        const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts - 1), 30000);
        setTimeout(() => {
            if (!this.isDestroyed) {
                this.establishConnection();
            }
        }, delay);
    }
}
