export type GameId = string;

export type Card = {
    // adapt to your API shape
    rank: string; // e.g. "A", "10", "K"
    suit: string; // e.g. "SPADES" | "HEARTS" | ...
    code?: string; // e.g. "AS"
};

export type CreateGameResponse = { gameId: GameId };

export type JoinGameResponse = { playerId: string };

export type PlayerHandResponse = {
    gameId: GameId;
    playerId: string;
    hand: Card[];
};
