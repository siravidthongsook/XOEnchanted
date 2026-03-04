package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;

import static java.lang.Math.abs;

/**
 * Skill action that places two non-adjacent pieces for an energy cost.
 */
public class DoublePlaceAction implements SkillAction {
    private static final int COST = 4; // Updated to 4 Energy
    private final Position first;
    private final Position second;

    public DoublePlaceAction(Position first, Position second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean validate(GameState state) {
        if (!validateTargets(state)) {
            return false;
        }

        if (state.getPlayerState(state.getCurrentPlayer()).getEnergy() < COST) {
            return false;
        }

        return true;
    }

    public boolean validateTargets(GameState state) {
        if (!state.isInsideBoard(first) || !state.isInsideBoard(second)) {
            return false;
        }

        if (first.equals(second)) {
            return false;
        }

        if (state.getCell(first) != CellType.EMPTY || state.getCell(second) != CellType.EMPTY) {
            return false;
        }

        if (isOrthogonallyAdjacent(first, second)) {
            return false;
        }

        return true;
    }

    @Override
    public void apply(GameState state) {
        if (!validate(state)) {
            throw new IllegalStateException("Invalid Double Place Action: Check energy, empty cells, or adjacency.");
        }

        // 1. Spend Energy (4 Energy)
        state.getPlayerState(state.getCurrentPlayer()).spendEnergy(COST);

        // 2. Place both pieces for the current player
        state.placePiece(first, state.getCurrentPlayer());
        state.placePiece(second, state.getCurrentPlayer());
    }

    public Position first() {
        return first;
    }

    public Position second() {
        return second;
    }

    private boolean isOrthogonallyAdjacent(Position a, Position b) {
        int rowDiff = abs(a.row() - b.row());
        int colDiff = abs(a.col() - b.col());
        return rowDiff + colDiff == 1;
    }
}
