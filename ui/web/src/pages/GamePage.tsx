import React, { useEffect, useMemo, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { apiRequest } from "../api/client";
import { useTransport } from "../hooks/useTransport";
import { ConnectionIndicator } from "../components/ConnectionIndicator";
import type { Card, GameResponse, HandResponse, Suit, Rank, DecisionResponse, TalonResponse, GameType, SelectTrumpAction, PlayCardAction } from "../types";

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
    const [activePlayerId, setActivePlayerId] = useState<string | null>(null);
    const [talon, setTalon] = useState<Card[]>([]);
    const [selectedDiscards, setSelectedDiscards] = useState<Card[]>([]);

    // Real-time transport for game state updates
    const { state: transportState, connectionStatus } = useTransport(gameId ?? null, activePlayerId);

    // Update local game state when transport receives updates
    useEffect(() => {
        if (transportState) {
            setGame(transportState);
        }
    }, [transportState]);

    // Default player from localStorage (the one who joined)
    const defaultPlayerId = useMemo(() => {
        if (!gameId) return null;
        return localStorage.getItem(`playerId:${gameId}`);
    }, [gameId]);

    // Initialize active player from localStorage on first load
    useEffect(() => {
        if (defaultPlayerId && !activePlayerId) {
            setActivePlayerId(defaultPlayerId);
        }
    }, [defaultPlayerId, activePlayerId]);

    // The effective player ID used for all operations
    const playerId = activePlayerId;

    const loadGame = useCallback(async (currentPlayerId: string | null) => {
        if (!gameId) return;
        setError(null);
        // Don't show global loading if we already have game data (e.g. from transport)
        setLoading(prev => {
            if (!game) return true;
            return prev;
        });
        try {
            const url = currentPlayerId ? `/games/${gameId}?playerId=${currentPlayerId}` : `/games/${gameId}`;
            const resp = await apiRequest<GameResponse>(url, "GET");
            setGame(resp);
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, game]);

    const loadHand = useCallback(async () => {
        if (!gameId || !playerId) return;
        try {
            const resp = await apiRequest<HandResponse>(`/games/${gameId}/players/${playerId}/hand`, "GET");
            setHand(resp.hand ?? []);
        } catch {
            setHand([]);
        }
    }, [gameId, playerId]);

    const loadDecision = useCallback(async () => {
        if (!gameId) return;
        try {
            const url = playerId ? `/games/${gameId}/decision?playerId=${playerId}` : `/games/${gameId}/decision`;
            const resp = await apiRequest<DecisionResponse>(url, "GET");
            setDecision(resp);
        } catch {
            setDecision(null);
        }
    }, [gameId, playerId]);

    const loadTalon = useCallback(async () => {
        if (!gameId || !playerId) return;
        try {
            const resp = await apiRequest<TalonResponse>(`/games/${gameId}/talon?playerId=${playerId}`, "GET");
            setTalon(resp.cards ?? []);
        } catch {
            setTalon([]);
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

    const submitExchangeTalon = useCallback(async (cardsToDiscard: Card[]) => {
        if (!gameId || !playerId) return;
        setError(null);
        setLoading(true);
        try {
            const resp = await apiRequest<GameResponse>(`/games/${gameId}/actions`, "POST", {
                action: { type: "exchange", playerId, cardsToDiscard }
            });
            setGame(resp);
            setSelectedDiscards([]);
            setTalon([]);
            await loadHand();
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, playerId, loadHand]);

    const submitSelectTrump = useCallback(async (trump: Suit) => {
        if (!gameId || !playerId) return;
        setError(null);
        setLoading(true);
        try {
            const resp = await apiRequest<GameResponse>(`/games/${gameId}/actions`, "POST", {
                action: { type: "trump", playerId, trump }
            });
            setGame(resp);
            await loadHand();
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, playerId, loadHand]);

    // Load game on mount
    useEffect(() => {
        void loadGame(playerId);
    }, [gameId, playerId, loadGame]);

    // Load hand when player changes or game state changes
    useEffect(() => {
        if (game && playerId) {
            if (game.dealing?.isWaitingForChooser ||
                (game.phase !== "WAITING_FOR_PLAYERS" && game.phase !== "DEALING")) {
                void loadHand();
            }
            if (game.phase === "DEALING" || game.dealing?.isWaitingForChooser) {
                void loadDecision();
            }
            if (game.phase === "TALON_EXCHANGE" && playerId === game.declarerId) {
                void loadTalon();
            }
        }
    }, [game, playerId, loadHand, loadDecision, loadTalon]);

    // Clear selected card when switching players
    const handlePlayerChange = (newPlayerId: string) => {
        setActivePlayerId(newPlayerId);
        setSelectedCard(null);
        setSelectedDiscards([]);
        setTalon([]);
        setHand([]);
        setError(null);
        if (gameId) {
            localStorage.setItem(`playerId:${gameId}`, newPlayerId);
        }
    };

    const canStart = (game?.possibleActions ?? []).some(a => a.type === "start") ?? false;
    const canDeal = (game?.possibleActions ?? []).some(a => a.type === "deal") ?? false;
    const isWaitingForChooser = game?.dealing?.isWaitingForChooser ?? false;
    const isChooser = playerId === game?.dealing?.chooserId;
    const showHand = game && playerId && (
        (game.phase !== "WAITING_FOR_PLAYERS" && game.phase !== "DEALING") ||
        isWaitingForChooser
    );

    const canSelectTrump = (game?.possibleActions ?? []).some(a => a.type === "choosetrump") ?? false;
    const canPass = (game?.possibleActions ?? []).some(a => a.type === "chooserpass") ?? false;

    // Talon exchange state
    const isTalonExchange = game?.phase === "TALON_EXCHANGE";
    const isDeclarer = playerId === game?.declarerId;
    const canExchange = isTalonExchange && isDeclarer;

    // Trump selection phase state
    const isTrumpSelection = game?.phase === "TRUMP_SELECTION";
    const canSelectTrumpPhase = (game?.possibleActions ?? []).some(a => a.type === "trump") ?? false;

    // Avilable trump suits from possibleActions
    const availableTrumpSuits = useMemo(() => {
        return (game?.possibleActions ?? [])
            .filter((a): a is SelectTrumpAction => a.type === "trump")
            .map(a => a.trump);
    }, [game?.possibleActions]);

    // Helper to check if card is selected for discard
    const isSelectedForDiscard = (card: Card) =>
        selectedDiscards.some(c => c.suit === card.suit && c.rank === card.rank);

    // Helper to check if card is Ace or Ten (for validation warning)
    const isAceOrTen = (card: Card) => card.rank === "ACE" || card.rank === "TEN";

    // Check if current game type allows Ace/Ten discard (Misere/Slam)
    const allowsAnyDiscard = game?.gameType === "MISERE" || game?.gameType === "SLAM";

    // Validation: check if selection contains invalid cards
    const hasInvalidDiscard = !allowsAnyDiscard && selectedDiscards.some(isAceOrTen);

    // Play card available?
    const playableCards = useMemo(() => {
        return (game?.possibleActions ?? [])
            .filter((a): a is PlayCardAction => a.type === "play")
            .map(a => a.card);
    }, [game?.possibleActions]);

    const submitPlayCard = useCallback(async (card: Card) => {
        if (!gameId || !playerId) return;
        setError(null);
        setLoading(true);
        try {
            const resp = await apiRequest<GameResponse>(`/games/${gameId}/actions`, "POST", {
                action: { type: "play", playerId, card }
            });
            setGame(resp);
            await loadHand();
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
        } finally {
            setLoading(false);
        }
    }, [gameId, playerId, loadHand]);

    const handleCardClick = (card: Card) => {
        if (canExchange) {
            // Toggle card selection for discard
            if (isSelectedForDiscard(card)) {
                setSelectedDiscards(prev => prev.filter(c => c.suit !== card.suit || c.rank !== card.rank));
            } else if (selectedDiscards.length < 2) {
                setSelectedDiscards(prev => [...prev, card]);
            }
            return;
        }
        if (playableCards.some(c => c.suit === card.suit && c.rank === card.rank)) {
            void submitPlayCard(card);
            return;
        }
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

    const handleExchangeTalon = () => {
        if (selectedDiscards.length === 2) {
            void submitExchangeTalon(selectedDiscards);
        }
    };

    const handleSelectTrumpSuit = (suit: Suit) => {
        void submitSelectTrump(suit);
    };

    const activePlayer = game?.players.find(p => p.playerId === playerId);

    if (loading && !game) {
        return (
            <div style={{ display: "flex", justifyContent: "center", padding: 40 }}>
                <h3>Loading game...</h3>
            </div>
        );
    }

    if (!game) {
        return (
            <div style={{ padding: 20 }}>
                <h2 style={{ margin: 0 }}>Game {gameId}</h2>
                <div style={{ marginTop: 20 }}>
                    <h3>Game not found or failed to load.</h3>
                    {error && <p style={{ color: "crimson" }}>{error}</p>}
                    <button onClick={loadGame}>Retry</button>
                </div>
            </div>
        );
    }

    return (
        <div>
            <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 16 }}>
                <h2 style={{ margin: 0 }}>Game {gameId}</h2>
                <ConnectionIndicator status={connectionStatus} />
            </div>

            {/* Player Switcher */}
            {game && game.players.length > 0 && (
                <div style={{
                    marginBottom: 16,
                    padding: 12,
                    background: "#e8f4fd",
                    borderRadius: 8,
                    border: "1px solid #b8daff"
                }}>
                    <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
                        <strong>Playing as:</strong>
                        <select
                            value={playerId ?? ""}
                            onChange={(e) => handlePlayerChange(e.target.value)}
                            style={{
                                padding: "4px 8px",
                                fontSize: 14,
                                borderRadius: 4,
                                border: "1px solid #ccc"
                            }}
                        >
                            {!playerId && <option value="">Select player...</option>}
                            {game.players.map(p => (
                                <option key={p.playerId} value={p.playerId}>
                                    {p.name} {p.playerId === defaultPlayerId ? "(you)" : ""} {p.isDealer ? "[Dealer]" : ""}
                                </option>
                            ))}
                        </select>
                        {activePlayer && (
                            <span style={{ marginLeft: 8, color: "#666" }}>
                                {activePlayer.cardCount} cards, {activePlayer.points} pts
                            </span>
                        )}
                    </label>
                </div>
            )}

            {!playerId && game && game.players.length > 0 && (
                <p style={{ color: "#856404", background: "#fff3cd", padding: 8, borderRadius: 4 }}>
                    Select a player above to view their hand and make moves.
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

            {/* Talon Exchange UI */}
            {isTalonExchange && (
                <div style={{
                    marginBottom: 16,
                    padding: 16,
                    background: isDeclarer ? "#d4edda" : "#d1ecf1",
                    border: `2px solid ${isDeclarer ? "#28a745" : "#17a2b8"}`,
                    borderRadius: 8
                }}>
                    <h3 style={{ margin: "0 0 8px 0" }}>
                        {isDeclarer
                            ? "Talon Exchange"
                            : `Waiting for ${game?.players.find(p => p.playerId === game?.declarerId)?.name ?? "declarer"} to exchange talon`
                        }
                    </h3>
                    {isDeclarer ? (
                        <>
                            <p style={{ margin: "0 0 12px 0" }}>
                                Pick up the talon cards and select <strong>2 cards</strong> from your hand to discard.
                            </p>

                            {/* Talon cards display */}
                            {talon.length > 0 && (
                                <div style={{ marginBottom: 16 }}>
                                    <h4 style={{ margin: "0 0 8px 0" }}>Talon Cards (added to your hand):</h4>
                                    <div style={{ display: "flex", gap: 8 }}>
                                        {talon.map((c, idx) => (
                                            <CardView
                                                key={`talon-${c.suit}-${c.rank}-${idx}`}
                                                card={c}
                                                highlight
                                            />
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* Selection status */}
                            <p style={{ margin: "0 0 8px 0" }}>
                                Selected for discard: <strong>{selectedDiscards.length}/2</strong>
                                {selectedDiscards.length > 0 && (
                                    <span> ({selectedDiscards.map(c => cardLabel(c)).join(", ")})</span>
                                )}
                            </p>

                            {/* Validation warning */}
                            {hasInvalidDiscard && (
                                <p style={{
                                    margin: "0 0 12px 0",
                                    padding: 8,
                                    background: "#f8d7da",
                                    color: "#721c24",
                                    borderRadius: 4
                                }}>
                                    Warning: Cannot discard Ace or Ten to talon (except in Misere/Slam)
                                </p>
                            )}

                            {/* Confirm button */}
                            <button
                                onClick={handleExchangeTalon}
                                disabled={selectedDiscards.length !== 2 || hasInvalidDiscard || loading}
                                style={{
                                    padding: "8px 16px",
                                    background: selectedDiscards.length === 2 && !hasInvalidDiscard ? "#28a745" : "#ccc",
                                    color: "white",
                                    border: "none",
                                    borderRadius: 4,
                                    cursor: selectedDiscards.length === 2 && !hasInvalidDiscard ? "pointer" : "not-allowed"
                                }}
                            >
                                Confirm Discard
                            </button>
                        </>
                    ) : (
                        <p style={{ margin: 0 }}>
                            The declarer is choosing which cards to discard to the talon.
                        </p>
                    )}
                </div>
            )}

            {/* Trump Selection UI */}
            {canSelectTrumpPhase && (
                <div style={{
                    marginBottom: 16,
                    padding: 16,
                    background: "#fff3cd",
                    border: "2px solid #ffc107",
                    borderRadius: 8
                }}>
                    <h3 style={{ margin: "0 0 8px 0" }}>Select Trump Suit</h3>
                    <p style={{ margin: "0 0 12px 0" }}>
                        Choose the trump suit for this round.
                    </p>
                    <div style={{ display: "flex", gap: 8 }}>
                        {availableTrumpSuits.map(suit => (
                            <button
                                key={suit}
                                onClick={() => handleSelectTrumpSuit(suit)}
                                disabled={loading}
                                style={{
                                    padding: "12px 20px",
                                    fontSize: 24,
                                    background: "white",
                                    color: suit === "HEARTS" || suit === "DIAMONDS" ? "crimson" : "black",
                                    border: "2px solid #ccc",
                                    borderRadius: 8,
                                    cursor: "pointer"
                                }}
                            >
                                {SUIT_SYMBOLS[suit]}
                            </button>
                        ))}
                    </div>
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
                    <h3>
                        {activePlayer?.name ?? "Player"}'s Hand
                        {isWaitingForChooser && isChooser && " (click a card to select as trump)"}
                        {canExchange && " (click cards to select for discard)"}
                    </h3>
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
                                    onClick={(canSelectTrump || canExchange) ? () => handleCardClick(c) : undefined}
                                    selected={
                                        (selectedCard?.suit === c.suit && selectedCard?.rank === c.rank) ||
                                        isSelectedForDiscard(c)
                                    }
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
