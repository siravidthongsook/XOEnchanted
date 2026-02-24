package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerState;
import game.model.Position;

public class DoublePlaceAction implements SkillAction {
    private final Position first;
    private final Position second;

    public DoublePlaceAction(Position first, Position second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean validate(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());
        if (actorState.getEnergy() < 4) return false;

        if (state.getCell(first) != CellType.EMPTY || state.getCell(second) != CellType.EMPTY) return false;
        if (first.equals(second)) return false; // ห้ามเลือกช่องเดียวกัน

        // ห้ามวางติดกันแบบ Orthogonal (บน/ล่าง/ซ้าย/ขวา)
        int dr = Math.abs(first.row() - second.row());
        int dc = Math.abs(first.col() - second.col());
        if ((dr + dc) == 1) return false;

        return true;
    }

    @Override
    public void apply(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());
        actorState.spendEnergy(4);

        state.placePiece(first, state.getCurrentPlayer());
        state.placePiece(second, state.getCurrentPlayer());
    }

    public Position first() { return first; }
    public Position second() { return second; }
}