package game.engine;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;

import java.util.ArrayList;
import java.util.List;

public class FrozenRuleService {
    // Change this number to whatever your game's rule requires!
    private static final int INACTIVITY_THRESHOLD = 3;

    public void updateAtEndOfTurn(GameState state) {
        PlayerId endingPlayer = state.getCurrentPlayer();

        List<Position> toUnfreeze = new ArrayList<>();
        List<Position> toFreeze = new ArrayList<>();

        // Scan the entire board
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                CellType cell = state.getCell(pos);

                // Only update pieces belonging to the player whose turn is ending
                if (cell == CellType.fromPlayer(endingPlayer)) {

                    if (state.isFrozen(pos)) {
                        // 1. If it's already frozen, the 1-owner-turn window is over. Thaw it out.
                        toUnfreeze.add(pos);
                    } else {
                        // 2. Otherwise, increase its inactivity counter
                        int currentInactivity = state.getPieceInactivity(pos) + 1;

                        if (currentInactivity >= INACTIVITY_THRESHOLD) {
                            // Reached the limit! Mark for freezing
                            toFreeze.add(pos);
                        } else {
                            // Update the counter in the state
                            state.setPieceInactivity(pos, currentInactivity);
                        }
                    }
                }
            }
        }

        // Apply the Thaw (Unfreeze)
        for (Position pos : toUnfreeze) {
            state.removeFrozenCell(pos);
            state.setPieceInactivity(pos, 0); // Reset inactivity so the cycle can start over
        }

        // Apply the Freeze
        for (Position pos : toFreeze) {
            state.addFrozenCell(pos);
            state.setPieceInactivity(pos, 0); // Reset the counter while frozen
        }
    }

    public boolean isFrozenAt(GameState state, int row, int col) {
        // Return frozen status from the metadata we set up earlier
        return state.isFrozen(new Position(row, col));
    }
}