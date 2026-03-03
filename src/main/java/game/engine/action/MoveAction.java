package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;

public class MoveAction implements SkillAction {
    private static final int COST = 1;
    private final Position from;
    private final Position to;

    public MoveAction(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean validate(GameState state) {
        // 1. Validate Energy
        if (state.getPlayerState(state.getCurrentPlayer()).getEnergy() < COST) {
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

        // 4. Validate source piece is not frozen
        if (state.isFrozen(from)) {
            return false;
        }

        // 5. Validate Destination is Empty
        if (state.getCell(to) != CellType.EMPTY) {
            return false;
        }

        return true;
    }

    @Override
    public void apply(GameState state) {
        if (!validate(state)) {
            throw new IllegalStateException("Invalid Move Action");
        }

        // 1. Move the piece
        state.clearCell(from);
        state.placePiece(to, state.getCurrentPlayer());

        // 2. Spend Energy
        state.getPlayerState(state.getCurrentPlayer()).spendEnergy(COST);

        // 3. Reset the inactivity counters (Crucial for the Frozen Rule!)
        // Clear the data from the old cell where it used to be
        state.clearPieceMetadata(from);
        // Defensive clear in case old metadata remained on an empty cell
        state.clearPieceMetadata(to);
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
