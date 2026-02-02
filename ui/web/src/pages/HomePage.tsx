import React, { useState } from "react";
import { apiRequest } from "../api/client";
import { useNavigate } from "react-router-dom";
import type { CreateGameResponse } from "../api/types";

export default function HomePage() {
    const nav = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function onCreate() {
        setError(null);
        setLoading(true);
        try {
            // TODO: adjust endpoint/path/payload to match your server
            const resp = await apiRequest<CreateGameResponse>("/api/games", "POST", {});
            nav(`/game/${resp.gameId}`);
        } catch (e: any) {
            setError(e?.message ?? String(e));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h2>Create a game</h2>
            <button onClick={onCreate} disabled={loading}>
                {loading ? "Creating..." : "Create"}
            </button>
            {error && <p style={{ color: "crimson" }}>{error}</p>}
            <p style={{ marginTop: 12, opacity: 0.75 }}>
                Note: endpoint paths are placeholders — wire them to docs/API.md.
            </p>
        </div>
    );
}
