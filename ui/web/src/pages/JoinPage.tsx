import React, { useState } from "react";
import { apiRequest } from "../api/client";
import { useNavigate } from "react-router-dom";
import type { JoinGameResponse } from "../api/types";

export default function JoinPage() {
    const nav = useNavigate();
    const [gameId, setGameId] = useState("");
    const [name, setName] = useState("Player");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function onJoin() {
        setError(null);
        setLoading(true);
        try {
            // TODO: adjust endpoint/path/payload to match your server
            const resp = await apiRequest<JoinGameResponse>(`/api/games/${gameId}/players`, "POST", { name });
            // store playerId locally for now (prototype)
            localStorage.setItem(`playerId:${gameId}`, resp.playerId);
            nav(`/game/${gameId}`);
        } catch (e: any) {
            setError(e?.message ?? String(e));
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
                    <input value={gameId} onChange={(e) => setGameId(e.target.value)} style={{ width: "100%" }} />
                </label>
                <label>
                    Name
                    <input value={name} onChange={(e) => setName(e.target.value)} style={{ width: "100%" }} />
                </label>
                <button onClick={onJoin} disabled={loading || !gameId}>
                    {loading ? "Joining..." : "Join"}
                </button>
            </div>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
        </div>
    );
}
