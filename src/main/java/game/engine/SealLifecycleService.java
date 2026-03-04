package game.engine;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles seal expiration after opponent placements.
 */
public class SealLifecycleService {
    public void updateAtEndOfTurn(GameState state) {
        // Seals now expire on opponent placement, handled by updateAfterPlacement.
    }

    public void updateAfterPlacement(GameState state) {
        List<Position> expiredSeals = new ArrayList<>();
        PlayerId actor = state.getCurrentPlayer();

        // Seals placed by the opponent of the actor expire right after the actor places.
        for (Map.Entry<Position, PlayerId> entry : state.getActiveSeals().entrySet()) {
            if (entry.getValue() == actor.opponent()) {
                expiredSeals.add(entry.getKey());
            }
        }

        // Clear the expired seals from the board and our tracker
        for (Position pos : expiredSeals) {
            if (state.getCell(pos) == CellType.SEALED) { // Safety check
                state.clearCell(pos); // Sets it back to EMPTY
            }
            state.removeSeal(pos);
        }
    }
}
