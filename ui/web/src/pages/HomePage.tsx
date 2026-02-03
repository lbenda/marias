import React, { useState } from "react";
import { apiRequest } from "../api/client";
import { useNavigate } from "react-router-dom";
import type { GameResponse } from "../types";

export default function HomePage() {
    const nav = useNavigate();
    const [playerName, setPlayerName] = useState("Player1");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function onCreate() {
        if (!playerName.trim()) {
            setError("Player name is required");
            return;
        }
        setError(null);
        setLoading(true);
        try {
            const playerId = `p-${Date.now()}`;
            const resp = await apiRequest<GameResponse>("/games", "POST", {
                playerId,
                playerName: playerName.trim()
            });
            localStorage.setItem(`playerId:${resp.gameId}`, playerId);
            nav(`/game/${resp.gameId}`);
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h2>Create a game</h2>
            <div style={{ display: "grid", gap: 8, maxWidth: 360 }}>
                <label>
                    Your name
                    <input
                        value={playerName}
                        onChange={(e) => setPlayerName(e.target.value)}
                        style={{ width: "100%" }}
                    />
                </label>
                <button onClick={onCreate} disabled={loading}>
                    {loading ? "Creating..." : "Create Game"}
                </button>
            </div>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
        </div>
    );
}
