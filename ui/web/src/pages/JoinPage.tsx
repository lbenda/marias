import React, { useState } from "react";
import { apiRequest } from "../api/client";
import { useNavigate } from "react-router-dom";
import type { GameResponse } from "../types";

export default function JoinPage() {
    const nav = useNavigate();
    const [gameId, setGameId] = useState("");
    const [playerName, setPlayerName] = useState("Player");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function onJoin() {
        if (!gameId.trim()) {
            setError("Game ID is required");
            return;
        }
        if (!playerName.trim()) {
            setError("Player name is required");
            return;
        }
        setError(null);
        setLoading(true);
        try {
            const playerId = `p-${Date.now()}`;
            await apiRequest<GameResponse>(`/games/${gameId.trim()}/actions`, "POST", {
                action: {
                    type: "join",
                    playerId,
                    playerName: playerName.trim()
                }
            });
            localStorage.setItem(`playerId:${gameId.trim()}`, playerId);
            nav(`/game/${gameId.trim()}`);
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h2>Join a game</h2>
            <div style={{ display: "grid", gap: 8, maxWidth: 360 }}>
                <label>
                    Game ID
                    <input
                        value={gameId}
                        onChange={(e) => setGameId(e.target.value)}
                        style={{ width: "100%" }}
                    />
                </label>
                <label>
                    Your name
                    <input
                        value={playerName}
                        onChange={(e) => setPlayerName(e.target.value)}
                        style={{ width: "100%" }}
                    />
                </label>
                <button onClick={onJoin} disabled={loading || !gameId.trim()}>
                    {loading ? "Joining..." : "Join"}
                </button>
            </div>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
        </div>
    );
}
