package game.engine;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SealLifecycleService {
    public void updateAtEndOfTurn(GameState state) {
        List<Position> expiredSeals = new ArrayList<>();

        // Find which seals have reached their expiry turn
        for (Map.Entry<Position, Integer> entry : state.getActiveSeals().entrySet()) {
            if (state.getTotalTurnCount() >= entry.getValue()) {
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
