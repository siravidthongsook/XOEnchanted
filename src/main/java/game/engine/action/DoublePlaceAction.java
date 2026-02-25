package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;

import static java.lang.Math.abs;

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
        // 1. Validate Energy (Must have at least 4)
        if (state.getPlayerState(state.getCurrentPlayer()).getEnergy() < COST) {
            return false;
        }

        // 2. Validate Board Bounds
        if (!state.isInsideBoard(first) || !state.isInsideBoard(second)) {
            return false;
        }

        // 3. Validate they are not the exact same cell
        if (first.equals(second)) {
            return false;
        }

        // 4. Validate Both Targets are Empty
        if (state.getCell(first) != CellType.EMPTY || state.getCell(second) != CellType.EMPTY) {
            return false;
        }

        // 5. Validate Non-Adjacent Rule (ห้ามติดกันแบบ orthogonal)
        // Orthogonal means they share an edge (Up, Down, Left, Right).
        // In math: absolute row difference + absolute col difference == 1
        int rowDiff = abs(first.row() - second.row());
        int colDiff = abs(first.col() - second.col());
        if (rowDiff + colDiff == 1) {
            return false; // They are orthogonally adjacent!
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
}