package game.engine;

import game.model.GameState;

/**
 * Validator for DisruptAction.
 */
public class DisruptActionValidator extends ActionValidator {

    @Override
    public void validate(GameState state, Action action) throws IllegalActionException {
        // TODO: Implement disrupt action validation
        // Check:
        // 1. Action is performed by current player
        // 2. Player has enough energy (3)
        // 3. Target position contains opponent's piece
        // 4. Target piece is NOT frozen
    }
}
