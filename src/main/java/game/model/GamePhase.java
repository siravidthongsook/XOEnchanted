package game.model;

/**
 * Represents the current phase of the game.
 */
public enum GamePhase {
    /**
     * Normal play phase (turns 1-24)
     */
    NORMAL,
    
    /**
     * Sudden death phase (after turn 24 if scores are tied)
     */
    SUDDEN_DEATH
}
