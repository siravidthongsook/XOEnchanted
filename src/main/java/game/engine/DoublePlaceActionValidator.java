package game.engine;

import game.model.GameState;

/**
 * Validator for DoublePlaceAction.
 */
public class DoublePlaceActionValidator extends ActionValidator {

    @Override
    public void validate(GameState state, Action action) throws IllegalActionException {
        // TODO: Implement double place action validation
        // Check:
        // 1. Action is performed by current player
        // 2. Player has enough energy (4)
        // 3. Both positions are valid
        // 4. Both positions are empty
        // 5. The two positions are NOT orthogonally adjacent
    }
}
