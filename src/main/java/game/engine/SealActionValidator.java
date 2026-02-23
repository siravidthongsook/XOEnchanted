package game.engine;

import game.model.GameState;

/**
 * Validator for SealAction.
 */
public class SealActionValidator extends ActionValidator {

    @Override
    public void validate(GameState state, Action action) throws IllegalActionException {
        // TODO: Implement seal action validation
        // Check:
        // 1. Action is performed by current player
        // 2. Player has enough energy (2)
        // 3. Target position is valid
        // 4. Target position is empty (not already occupied or sealed)
    }
}
