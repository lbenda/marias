import type { GameResponse } from "../../types";

export class PollingTransport {
    private version: number | null = null;
    private abortController: AbortController | null = null;

    constructor(private baseUrl: string = "") {}

    /**
     * Poll for game state changes.
     * @param gameId Game ID to poll
     * @param longPoll Whether to use long polling (with Prefer header)
     * @param timeout Long poll timeout in seconds
     * @returns Game state if changed, null if no changes (304)
     */
    async poll(
        gameId: string,
        longPoll: boolean = false,
        timeout: number = 30
    ): Promise<GameResponse | null> {
        const headers: Record<string, string> = {
            "Cache-Control": "no-cache",
        };

        if (this.version !== null) {
            headers["If-None-Match"] = `v${this.version}`;
        }

        if (longPoll) {
            headers["Prefer"] = `wait=${timeout}`;
        }

        this.abortController = new AbortController();

        try {
            const response = await fetch(
                `${this.baseUrl}/games/${gameId}/events`,
                {
                    headers,
                    signal: this.abortController.signal,
                }
            );

            if (response.status === 304) {
                return null; // No changes
            }

            if (response.status === 404) {
                throw new Error("Game not found");
            }

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const state = await response.json() as GameResponse;
            this.version = state.version;
            return state;
        } finally {
            this.abortController = null;
        }
    }

    /**
     * Abort any pending poll request.
     */
    abort(): void {
        this.abortController?.abort();
        this.abortController = null;
    }

    /**
     * Reset version tracking (e.g., on reconnect).
     */
    reset(): void {
        this.version = null;
    }
}
