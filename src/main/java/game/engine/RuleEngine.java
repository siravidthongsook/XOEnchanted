package game.engine;

import game.model.GameState;

/**
 * Abstract base class for applying game rules.
 * Uses template method pattern to structure turn execution.
 */
public abstract class RuleEngine {
    
    /**
     * Apply rules at the start of a turn.
     * Handles: energy gain, overheat reset, disrupt penalty, priority turn.
     * @param state current game state
     * @return new game state after turn start rules applied
     */
    public abstract GameState applyTurnStartRules(GameState state);

    /**
     * Apply the action to the game state.
     * @param state current game state
     * @param action action to apply
     * @return new game state after action applied
     */
    public abstract GameState applyAction(GameState state, Action action);

    /**
     * Apply rules at the end of a turn.
     * Handles: scoring detection, line removal, frozen status updates, seal expiration.
     * @param state current game state
     * @return new game state after turn end rules applied
     */
    public abstract GameState applyTurnEndRules(GameState state);

    /**
     * Execute a complete turn using the template method pattern.
     * @param state current game state
     * @param action action to execute
     * @return new game state after full turn execution
     */
    public GameState executeTurn(GameState state, Action action) {
        GameState afterStart = applyTurnStartRules(state);
        GameState afterAction = applyAction(afterStart, action);
        GameState afterEnd = applyTurnEndRules(afterAction);
        return afterEnd;
    }
}
