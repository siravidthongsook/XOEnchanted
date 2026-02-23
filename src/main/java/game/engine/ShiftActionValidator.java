package game.engine;

import game.model.GameState;

/**
 * Validator for ShiftAction.
 */
public class ShiftActionValidator extends ActionValidator {

    @Override
    public void validate(GameState state, Action action) throws IllegalActionException {
        // TODO: Implement shift action validation
        // Check:
        // 1. Action is performed by current player
        // 2. Player has enough energy (2)
        // 3. 'from' position contains a piece owned by player
        // 4. 'to' position is empty
        // 5. 'to' position is orthogonally adjacent to 'from'
    }
}
