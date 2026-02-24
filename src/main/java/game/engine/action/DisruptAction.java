package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerState;
import game.model.Position;

public class DisruptAction implements SkillAction {
    private final Position target;

    public DisruptAction(Position target) {
        this.target = target;
    }

    @Override
    public boolean validate(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());
        CellType targetCell = state.getCell(target);
        CellType opponentCell = CellType.fromPlayer(state.getCurrentPlayer().opponent());

        // ต้องมี 3 Energy และเป้าหมายต้องเป็นหมากของศัตรู
        return actorState.getEnergy() >= 3 && targetCell == opponentCell;
    }

    @Override
    public void apply(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());

        actorState.spendEnergy(3);
        state.clearCell(target); // ลบหมากเป้าหมาย
        actorState.setSkipNextEnergyGain(true); // ติด Penalty เทิร์นถัดไปไม่ได้ Energy
    }

    public Position target() {
        return target;
    }
}