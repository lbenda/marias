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

export type GameType = "GAME" | "SEVEN" | "HUNDRED" | "HUNDRED_SEVEN" | "MISERE" | "SLAM" | "TWO_SEVENS";

export type DealingPhase = "NOT_STARTED" | "PHASE_A" | "PAUSED" | "PHASE_B" | "COMPLETE";

export type DealingDto = {
    phase: DealingPhase;
    chooserId: string | null;
    pendingCardsCount: number;
    isWaitingForChooser: boolean;
};

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
    trumpCard: Card | null;
    gameType: GameType | null;
    declarerId: string | null;
    dealing: DealingDto | null;
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

export type ChooserDecisionType = "SELECT_TRUMP" | "PASS" | "TAKE_TALON";

export type DecisionResponse = {
    hasDecision: boolean;
    playerId: string | null;
    availableDecisions: ChooserDecisionType[];
    mandatory: boolean;
    pendingCardsCount: number;
    trumpCard: Card | null;
};

// Action types for /games/{id}/actions
export type JoinAction = { type: "join"; playerId: string; playerName: string };
export type LeaveAction = { type: "leave"; playerId: string };
export type StartAction = { type: "start"; playerId: string };
export type DealAction = { type: "deal"; playerId: string; twoPhase?: boolean };
export type ChooseTrumpAction = { type: "choosetrump"; playerId: string; card: Card };
export type ChooserPassAction = { type: "chooserpass"; playerId: string };
export type ExchangeTalonAction = { type: "exchange"; playerId: string; cardsToDiscard: Card[] };
export type SelectTrumpAction = { type: "trump"; playerId: string; trump: Suit };
export type GameAction = JoinAction | LeaveAction | StartAction | DealAction | ChooseTrumpAction | ChooserPassAction | ExchangeTalonAction | SelectTrumpAction;

// Talon response
export type TalonResponse = { cards: Card[] };
