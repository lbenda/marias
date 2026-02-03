export type Suit = "SPADES" | "CLUBS" | "DIAMONDS" | "HEARTS";
export type Rank = "SEVEN" | "EIGHT" | "NINE" | "TEN" | "JACK" | "QUEEN" | "KING" | "ACE";

export type Card = { suit: Suit; rank: Rank };

export type GamePhase =
    | "WAITING_FOR_PLAYERS"
    | "DEALING"
    | "BIDDING"
    | "TALON_EXCHANGE"
    | "TRUMP_SELECTION"
    | "PLAYING"
    | "SCORING";

export type GameType = "HRA" | "SEDMA" | "KILO" | "BETL" | "DURCH";

export type PlayerDto = {
    playerId: string;
    name: string;
    cardCount: number;
    points: number;
    isDealer: boolean;
};

export type TrickDto = {
    cards: { playerId: string; card: Card }[];
    leadPlayerId: string | null;
};

export type GameResponse = {
    gameId: string;
    version: number;
    phase: GamePhase;
    players: PlayerDto[];
    currentPlayerId: string | null;
    dealerId: string | null;
    trump: Suit | null;
    gameType: GameType | null;
    declarerId: string | null;
    trick: TrickDto;
    tricksPlayed: number;
    roundNumber: number;
    error: string | null;
};

export type HandResponse = {
    hand: Card[];
    validCards: Card[];
};

export type GameListItem = {
    gameId: string;
    phase: GamePhase;
    playerCount: number;
};

// Action types for /games/{id}/actions
export type JoinAction = { type: "join"; playerId: string; playerName: string };
export type LeaveAction = { type: "leave"; playerId: string };
export type StartAction = { type: "start"; playerId: string };
export type DealAction = { type: "deal"; playerId: string };
export type GameAction = JoinAction | LeaveAction | StartAction | DealAction;
