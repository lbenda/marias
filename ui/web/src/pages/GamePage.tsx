import React, { useEffect, useMemo, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { apiRequest } from "../api/client";
import type { Card, GameResponse, HandResponse, Suit, Rank, DecisionResponse } from "../types";

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

type CardProps = {
    card: Card;
    onClick?: () => void;
    selected?: boolean;
    faceDown?: boolean;
    highlight?: boolean;
};

function CardView({ card, onClick, selected, faceDown, highlight }: CardProps) {
    return (
        <div
            onClick={onClick}
            style={{
                width: 56,
                height: 80,
                border: selected ? "3px solid gold" : highlight ? "2px solid blue" : "1px solid #ccc",
                borderRadius: 8,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: 18,
                fontWeight: "bold",
                color: faceDown ? "#666" : cardColor(card),
                background: faceDown ? "linear-gradient(135deg, #4a5568 25%, #2d3748 75%)" : "white",
                userSelect: "none",
                cursor: onClick ? "pointer" : "default",
                boxShadow: selected ? "0 0 8px gold" : "none",
                transition: "all 0.2s"
            }}
            title={faceDown ? "Face down" : `${card.rank} of ${card.suit}`}
        >
            {faceDown ? "🂠" : cardLabel(card)}
        </div>
    );
}

export default function GamePage() {
    const { gameId } = useParams<{ gameId: string }>();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [game, setGame] = useState<GameResponse | null>(null);
    const [hand, setHand] = useState<Card[]>([]);
    const [decision, setDecision] = useState<DecisionResponse | null>(null);
    const [selectedCard, setSelectedCard] = useState<Card | null>(null);

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

    const loadDecision = useCallback(async () => {
        if (!gameId) return;
        try {
            const resp = await apiRequest<DecisionResponse>(`/games/${gameId}/decision`, "GET");
            setDecision(resp);
        } catch {
            setDecision(null);
        }
    }, [gameId]);

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
                await loadDecision();
            }
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, playerId, loadHand, loadDecision]);

    const submitDecision = useCallback(async (decisionType: "SELECT_TRUMP" | "PASS", card?: Card) => {
        if (!gameId || !playerId) return;
        setError(null);
        setLoading(true);
        try {
            const resp = await apiRequest<GameResponse>(`/games/${gameId}/decision`, "POST", {
                playerId,
                decisionType,
                card: card ?? null
            });
            setGame(resp);
            setSelectedCard(null);
            await loadHand();
            await loadDecision();
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, playerId, loadHand, loadDecision]);

    useEffect(() => {
        void loadGame();
    }, [loadGame]);

    useEffect(() => {
        if (game) {
            // Load hand and decision when dealing is paused or after dealing
            if (game.dealing?.isWaitingForChooser ||
                (game.phase !== "WAITING_FOR_PLAYERS" && game.phase !== "DEALING")) {
                void loadHand();
            }
            // Load decision state when dealing is in progress
            if (game.phase === "DEALING" || game.dealing?.isWaitingForChooser) {
                void loadDecision();
            }
        }
    }, [game, loadHand, loadDecision]);

    const canStart = game?.phase === "WAITING_FOR_PLAYERS" && game.players.length === 3;
    const canDeal = game?.phase === "DEALING" && !game.dealing?.isWaitingForChooser;
    const isWaitingForChooser = game?.dealing?.isWaitingForChooser ?? false;
    const isChooser = playerId === game?.dealing?.chooserId;
    const showHand = game && (
        (game.phase !== "WAITING_FOR_PLAYERS" && game.phase !== "DEALING") ||
        isWaitingForChooser
    );

    const canSelectTrump = decision?.hasDecision &&
        decision.playerId === playerId &&
        decision.availableDecisions.includes("SELECT_TRUMP");
    const canPass = decision?.hasDecision &&
        decision.playerId === playerId &&
        decision.availableDecisions.includes("PASS");

    const handleCardClick = (card: Card) => {
        if (!canSelectTrump) return;
        setSelectedCard(prev =>
            prev?.suit === card.suit && prev?.rank === card.rank ? null : card
        );
    };

    const handleSelectTrump = () => {
        if (selectedCard) {
            void submitDecision("SELECT_TRUMP", selectedCard);
        }
    };

    const handlePass = () => {
        void submitDecision("PASS");
    };

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
                    {game.trumpCard && (
                        <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                            <strong>Trump Card:</strong>
                            <CardView card={game.trumpCard} highlight />
                        </div>
                    )}
                    {game.gameType && <div><strong>Game type:</strong> {game.gameType}</div>}
                    {game.declarerId && (
                        <div><strong>Declarer:</strong> {game.players.find(p => p.playerId === game.declarerId)?.name ?? game.declarerId}</div>
                    )}
                    {game.dealing && (
                        <div>
                            <strong>Dealing:</strong> {game.dealing.phase}
                            {game.dealing.pendingCardsCount > 0 && ` (${game.dealing.pendingCardsCount} cards remaining)`}
                        </div>
                    )}
                    {game.error && <div style={{ color: "crimson" }}><strong>Error:</strong> {game.error}</div>}
                </div>
            )}

            {/* Chooser Decision UI */}
            {isWaitingForChooser && (
                <div style={{
                    marginBottom: 16,
                    padding: 16,
                    background: isChooser ? "#fff3cd" : "#d1ecf1",
                    border: `2px solid ${isChooser ? "#ffc107" : "#17a2b8"}`,
                    borderRadius: 8
                }}>
                    <h3 style={{ margin: "0 0 8px 0" }}>
                        {isChooser
                            ? "🎯 Your Decision"
                            : `⏳ Waiting for ${game?.players.find(p => p.playerId === game?.dealing?.chooserId)?.name ?? "chooser"}`
                        }
                    </h3>
                    {isChooser ? (
                        <>
                            <p style={{ margin: "0 0 12px 0" }}>
                                You have 7 cards. Choose a card to declare trump, or pass to continue to bidding.
                            </p>
                            {selectedCard && (
                                <p style={{ margin: "0 0 12px 0" }}>
                                    Selected: <strong>{cardLabel(selectedCard)}</strong> — this card's suit ({SUIT_SYMBOLS[selectedCard.suit]}) will become trump.
                                </p>
                            )}
                            <div style={{ display: "flex", gap: 8 }}>
                                <button
                                    onClick={handleSelectTrump}
                                    disabled={!selectedCard || loading}
                                    style={{
                                        padding: "8px 16px",
                                        background: selectedCard ? "#28a745" : "#ccc",
                                        color: "white",
                                        border: "none",
                                        borderRadius: 4,
                                        cursor: selectedCard ? "pointer" : "not-allowed"
                                    }}
                                >
                                    Declare Trump
                                </button>
                                {canPass && (
                                    <button
                                        onClick={handlePass}
                                        disabled={loading}
                                        style={{
                                            padding: "8px 16px",
                                            background: "#6c757d",
                                            color: "white",
                                            border: "none",
                                            borderRadius: 4,
                                            cursor: "pointer"
                                        }}
                                    >
                                        Pass
                                    </button>
                                )}
                            </div>
                        </>
                    ) : (
                        <p style={{ margin: 0 }}>
                            The chooser is deciding whether to declare trump early or pass to bidding.
                        </p>
                    )}
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
                    <h3>Your Hand {isWaitingForChooser && isChooser && "(click a card to select as trump)"}</h3>
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
                                <CardView
                                    key={`${c.suit}-${c.rank}-${idx}`}
                                    card={c}
                                    onClick={canSelectTrump ? () => handleCardClick(c) : undefined}
                                    selected={selectedCard?.suit === c.suit && selectedCard?.rank === c.rank}
                                />
                            ))}
                        </div>
                    )}
                </>
            )}

            {/* Pending cards visualization */}
            {isWaitingForChooser && decision && decision.pendingCardsCount > 0 && (
                <div style={{ marginTop: 16 }}>
                    <h4>Pending Cards ({decision.pendingCardsCount})</h4>
                    <div style={{ display: "flex", flexWrap: "wrap", gap: 4 }}>
                        {Array.from({ length: Math.min(decision.pendingCardsCount, 25) }).map((_, i) => (
                            <div
                                key={i}
                                style={{
                                    width: 28,
                                    height: 40,
                                    background: "linear-gradient(135deg, #4a5568 25%, #2d3748 75%)",
                                    borderRadius: 4,
                                    border: "1px solid #1a202c"
                                }}
                            />
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}
