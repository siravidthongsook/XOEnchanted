package game.engine;

import game.model.GameState;

/**
 * Validator for PlaceAction.
 */
public class PlaceActionValidator extends ActionValidator {

    @Override
    public void validate(GameState state, Action action) throws IllegalActionException {
        // TODO: Implement place action validation
        // Check:
        // 1. Action is performed by current player
        // 2. Position is valid (within bounds)
        // 3. Position is empty (not occupied or sealed)
        // 4. Player has enough energy (should be 0, so always true)
    }
}
