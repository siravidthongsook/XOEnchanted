package game.engine.action;

import game.model.GameState;

/**
 * Contract for validating and applying a skill action to the game state.
 */
public interface SkillAction {
    boolean validate(GameState state);

    void apply(GameState state);
}
