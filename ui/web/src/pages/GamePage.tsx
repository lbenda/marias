import React, { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { apiRequest } from "../api/client";
import type { Card, PlayerHandResponse } from "../api/types";

function cardLabel(c: Card): string {
    // Prefer a provided code if API has it, otherwise rank+suit initial.
    if (c.code) return c.code;
    const suit =
        c.suit === "SPADES" ? "♠" :
            c.suit === "HEARTS" ? "♥" :
                c.suit === "DIAMONDS" ? "♦" :
                    c.suit === "CLUBS" ? "♣" :
                        c.suit;
    return `${c.rank}${suit}`;
}

export default function GamePage() {
    const { gameId } = useParams<{ gameId: string }>();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [hand, setHand] = useState<Card[]>([]);

    const playerId = useMemo(() => {
        if (!gameId) return null;
        return localStorage.getItem(`playerId:${gameId}`);
    }, [gameId]);

    async function loadHand() {
        if (!gameId || !playerId) return;
        setError(null);
        setLoading(true);
        try {
            // TODO: adjust endpoint to your "player hand" endpoint
            const resp = await apiRequest<PlayerHandResponse>(`/api/games/${gameId}/players/${playerId}/hand`, "GET");
            setHand(resp.hand ?? []);
        } catch (e: any) {
            setError(e?.message ?? String(e));
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        void loadHand();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [gameId, playerId]);

    return (
        <div>
            <h2>Game {gameId}</h2>

            {!playerId && (
                <p style={{ color: "crimson" }}>
                    Missing playerId for this game. Please join the game first.
                </p>
            )}

            <div style={{ display: "flex", gap: 8, alignItems: "center", marginBottom: 12 }}>
                <button onClick={loadHand} disabled={loading || !playerId}>
                    {loading ? "Loading..." : "Refresh hand"}
                </button>
            </div>

            {error && <p style={{ color: "crimson" }}>{error}</p>}

            <h3>Hand</h3>
            <div
                style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fill, minmax(56px, 1fr))",
                    gap: 8,
                    maxWidth: 720
                }}
            >
                {hand.map((c, idx) => (
                    <div
                        key={`${c.code ?? cardLabel(c)}:${idx}`}
                        style={{
                            width: 56,
                            height: 80,
                            border: "1px solid #ccc",
                            borderRadius: 8,
                            display: "grid",
                            placeItems: "center",
                            fontSize: 18,
                            userSelect: "none"
                        }}
                        title={JSON.stringify(c)}
                    >
                        {cardLabel(c)}
                    </div>
                ))}
            </div>
        </div>
    );
}
