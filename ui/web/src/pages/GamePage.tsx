import React, { useEffect, useMemo, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { apiRequest } from "../api/client";
import type { Card, GameResponse, HandResponse, Suit, Rank } from "../types";

const SUIT_SYMBOLS: Record<Suit, string> = {
    SPADES: "♠",
    CLUBS: "♣",
    DIAMONDS: "♦",
    HEARTS: "♥"
};

const RANK_SYMBOLS: Record<Rank, string> = {
    SEVEN: "7",
    EIGHT: "8",
    NINE: "9",
    TEN: "10",
    JACK: "J",
    QUEEN: "Q",
    KING: "K",
    ACE: "A"
};

function cardLabel(c: Card): string {
    return `${RANK_SYMBOLS[c.rank]}${SUIT_SYMBOLS[c.suit]}`;
}

function cardColor(c: Card): string {
    return c.suit === "HEARTS" || c.suit === "DIAMONDS" ? "crimson" : "black";
}

export default function GamePage() {
    const { gameId } = useParams<{ gameId: string }>();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [game, setGame] = useState<GameResponse | null>(null);
    const [hand, setHand] = useState<Card[]>([]);

    const playerId = useMemo(() => {
        if (!gameId) return null;
        return localStorage.getItem(`playerId:${gameId}`);
    }, [gameId]);

    const loadGame = useCallback(async () => {
        if (!gameId) return;
        setError(null);
        setLoading(true);
        try {
            const resp = await apiRequest<GameResponse>(`/games/${gameId}`, "GET");
            setGame(resp);
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId]);

    const loadHand = useCallback(async () => {
        if (!gameId || !playerId) return;
        try {
            const resp = await apiRequest<HandResponse>(`/games/${gameId}/players/${playerId}/hand`, "GET");
            setHand(resp.hand ?? []);
        } catch {
            // Hand may not be available yet
        }
    }, [gameId, playerId]);

    const dispatchAction = useCallback(async (actionType: "start" | "deal") => {
        if (!gameId || !playerId) return;
        setError(null);
        setLoading(true);
        try {
            const resp = await apiRequest<GameResponse>(`/games/${gameId}/actions`, "POST", {
                action: { type: actionType, playerId }
            });
            setGame(resp);
            if (actionType === "deal") {
                await loadHand();
            }
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, playerId, loadHand]);

    useEffect(() => {
        void loadGame();
    }, [loadGame]);

    useEffect(() => {
        if (game && game.phase !== "WAITING_FOR_PLAYERS" && game.phase !== "DEALING") {
            void loadHand();
        }
    }, [game, loadHand]);

    const canStart = game?.phase === "WAITING_FOR_PLAYERS" && game.players.length === 3;
    const canDeal = game?.phase === "DEALING";
    const showHand = game && game.phase !== "WAITING_FOR_PLAYERS" && game.phase !== "DEALING";

    return (
        <div>
            <h2>Game {gameId}</h2>

            {!playerId && (
                <p style={{ color: "crimson" }}>
                    Missing playerId for this game. Please join the game first.
                </p>
            )}

            {game && (
                <div style={{ marginBottom: 16, padding: 12, background: "#f5f5f5", borderRadius: 8 }}>
                    <div><strong>Phase:</strong> {game.phase}</div>
                    <div><strong>Players:</strong> {game.players.map(p => p.name).join(", ") || "none"}</div>
                    {game.trump && <div><strong>Trump:</strong> {SUIT_SYMBOLS[game.trump]}</div>}
                    {game.gameType && <div><strong>Game type:</strong> {game.gameType}</div>}
                    {game.error && <div style={{ color: "crimson" }}><strong>Error:</strong> {game.error}</div>}
                </div>
            )}

            <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
                <button onClick={loadGame} disabled={loading}>
                    {loading ? "Loading..." : "Refresh"}
                </button>
                {canStart && (
                    <button onClick={() => dispatchAction("start")} disabled={loading || !playerId}>
                        Start Game
                    </button>
                )}
                {canDeal && (
                    <button onClick={() => dispatchAction("deal")} disabled={loading || !playerId}>
                        Deal Cards
                    </button>
                )}
            </div>

            {error && <p style={{ color: "crimson" }}>{error}</p>}

            {showHand && (
                <>
                    <h3>Your Hand</h3>
                    {hand.length === 0 ? (
                        <p style={{ opacity: 0.6 }}>No cards yet</p>
                    ) : (
                        <div
                            style={{
                                display: "flex",
                                flexWrap: "wrap",
                                gap: 8,
                                maxWidth: 720
                            }}
                        >
                            {hand.map((c, idx) => (
                                <div
                                    key={`${c.suit}-${c.rank}-${idx}`}
                                    style={{
                                        width: 56,
                                        height: 80,
                                        border: "1px solid #ccc",
                                        borderRadius: 8,
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        fontSize: 18,
                                        fontWeight: "bold",
                                        color: cardColor(c),
                                        background: "white",
                                        userSelect: "none"
                                    }}
                                    title={`${c.rank} of ${c.suit}`}
                                >
                                    {cardLabel(c)}
                                </div>
                            ))}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
