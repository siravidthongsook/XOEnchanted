package game.model;

/**
 * Represents the result of a completed game.
 */
public sealed interface GameResult permits GameResult.Victory, GameResult.SuddenDeath {
    
    /**
     * A player has won the game.
     */
    record Victory(PlayerId winner, VictoryType type) implements GameResult {}
    
    /**
     * Game has entered sudden death phase (tied after 24 turns).
     */
    record SuddenDeath() implements GameResult {}
    
    /**
     * The type of victory condition that was met.
     */
    enum VictoryType {
        /** Reached 3 points first */
        SCORE_LIMIT,
        /** Had higher score after 24 turns */
        TURN_LIMIT,
        /** Scored first point in sudden death */
        SUDDEN_DEATH
    }
}
