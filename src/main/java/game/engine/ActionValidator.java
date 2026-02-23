package game.engine;

import game.model.GameState;
import game.model.PlayerState;

/**
 * Abstract base class for validating actions.
 * Each action type should have its own validator subclass.
 */
public abstract class ActionValidator {
    
    /**
     * Validate if the action is legal in the current game state.
     * @param state current game state
     * @param action action to validate
     * @throws IllegalActionException if action is invalid, with explanation
     */
    public abstract void validate(GameState state, Action action) throws IllegalActionException;

    /**
     * Check if the player has enough energy to perform the action.
     * @param player player state
     * @param action action to check
     * @return true if player has enough energy
     */
    protected boolean hasEnoughEnergy(PlayerState player, Action action) {
        return player.energy() >= action.getEnergyCost();
    }
}
