package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;

import static java.lang.Math.abs;

public class ShiftAction implements SkillAction {
    private final Position from;
    private final Position to;

    public ShiftAction(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean validate(GameState state) {
        // 1. Validate Energy
        if (state.getPlayerState(state.getCurrentPlayer()).getEnergy() < 2) {
            return false;
        }

        // 2. Validate Board Bounds
        if (!state.isInsideBoard(from) || !state.isInsideBoard(to)) {
            return false;
        }

        // 3. Validate Ownership (Must belong to current player)
        if (state.getCell(from) != CellType.fromPlayer(state.getCurrentPlayer())) {
            return false;
        }

        // 4. Validate Destination is Empty
        if (state.getCell(to) != CellType.EMPTY) {
            return false;
        }

        // 5. Validate One-Step Orthogonal Movement
        int rowDiff = abs(to.row() - from.row());
        int colDiff = abs(to.col() - from.col());
        if (rowDiff + colDiff != 1) {
            return false; // If it's 2, it's diagonal. If it's 0, they clicked the same cell.
        }

        return true;
    }

    @Override
    public void apply(GameState state) {
        if (!validate(state)) {
            throw new IllegalStateException("Invalid Shift Action");
        }

        // 1. Move the piece
        state.clearCell(from);
        state.placePiece(to, state.getCurrentPlayer());

        // 2. Spend Energy
        state.getPlayerState(state.getCurrentPlayer()).spendEnergy(2);

        // 3. Reset the inactivity counters (Crucial for the Frozen Rule!)
        // Clear the data from the old cell where it used to be
        state.clearPieceMetadata(from);
        // Ensure the piece starts fresh with 0 inactivity at its new location
        state.setPieceInactivity(to, 0);
    }

    public Position from() {
        return from;
    }

    public Position to() {
        return to;
    }
}