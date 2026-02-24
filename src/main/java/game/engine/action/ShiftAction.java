package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerState;
import game.model.Position;

public class ShiftAction implements SkillAction {
    private final Position from;
    private final Position to;

    public ShiftAction(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean validate(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());
        if (actorState.getEnergy() < 2) return false;

        CellType ownPiece = CellType.fromPlayer(state.getCurrentPlayer());
        if (state.getCell(from) != ownPiece) return false; // ต้นทางต้องเป็นหมากตัวเอง
        if (state.getCell(to) != CellType.EMPTY) return false; // ปลายทางต้องว่าง

        // ต้องขยับไปช่องติดกัน (บน/ล่าง/ซ้าย/ขวา) 1 ช่องเท่านั้น
        int dr = Math.abs(from.row() - to.row());
        int dc = Math.abs(from.col() - to.col());
        return (dr + dc) == 1;
    }

    @Override
    public void apply(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());
        actorState.spendEnergy(2);

        // ย้ายหมาก
        state.clearCell(from);
        state.placePiece(to, state.getCurrentPlayer());
    }

    public Position from() { return from; }
    public Position to() { return to; }
}