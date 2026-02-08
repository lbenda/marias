import type { WebSocketMessage } from "./types";

export type MessageHandler = (message: WebSocketMessage) => void;
export type CloseHandler = (event: CloseEvent) => void;
export type ErrorHandler = (error: Event) => void;

export class WebSocketTransport {
    private ws: WebSocket | null = null;
    private messageHandlers: MessageHandler[] = [];
    private closeHandlers: CloseHandler[] = [];
    private errorHandlers: ErrorHandler[] = [];

    constructor(private baseUrl: string = "") {
        // Convert http(s) to ws(s)
        if (this.baseUrl.startsWith("http://")) {
            this.baseUrl = this.baseUrl.replace("http://", "ws://");
        } else if (this.baseUrl.startsWith("https://")) {
            this.baseUrl = this.baseUrl.replace("https://", "wss://");
        }
    }

    /**
     * Connect to WebSocket endpoint.
     * @param gameId Game ID to connect to
     * @param playerId Optional player ID to get possible actions
     * @returns Promise that resolves when connected
     */
    connect(gameId: string, playerId?: string | null): Promise<void> {
        return new Promise((resolve, reject) => {
            const query = playerId ? `?playerId=${playerId}` : "";
            const wsUrl = `${this.baseUrl}/games/${gameId}/ws${query}`;

            try {
                this.ws = new WebSocket(wsUrl);
            } catch (error) {
                reject(new Error(`Failed to create WebSocket: ${error}`));
                return;
            }

            const timeout = setTimeout(() => {
                this.close();
                reject(new Error("WebSocket connection timeout"));
            }, 10000);

            this.ws.onopen = () => {
                clearTimeout(timeout);
                resolve();
            };

            this.ws.onerror = (event) => {
                clearTimeout(timeout);
                this.errorHandlers.forEach((h) => h(event));
                reject(new Error("WebSocket connection failed"));
            };

            this.ws.onclose = (event) => {
                clearTimeout(timeout);
                this.closeHandlers.forEach((h) => h(event));
            };

            this.ws.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data) as WebSocketMessage;
                    this.messageHandlers.forEach((h) => h(message));
                } catch (error) {
                    console.error("Failed to parse WebSocket message:", error);
                }
            };
        });
    }

    /**
     * Register message handler.
     */
    onMessage(handler: MessageHandler): void {
        this.messageHandlers.push(handler);
    }

    /**
     * Register close handler.
     */
    onClose(handler: CloseHandler): void {
        this.closeHandlers.push(handler);
    }

    /**
     * Register error handler.
     */
    onError(handler: ErrorHandler): void {
        this.errorHandlers.push(handler);
    }

    /**
     * Send data to server.
     */
    send(data: unknown): void {
        if (this.ws?.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(data));
        }
    }

    /**
     * Close the connection.
     */
    close(): void {
        if (this.ws) {
            this.ws.onclose = null;
            this.ws.onerror = null;
            this.ws.onmessage = null;
            this.ws.close();
            this.ws = null;
        }
        this.messageHandlers = [];
        this.closeHandlers = [];
        this.errorHandlers = [];
    }

    /**
     * Check if connected.
     */
    isConnected(): boolean {
        return this.ws?.readyState === WebSocket.OPEN;
    }
}
