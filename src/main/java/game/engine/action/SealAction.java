package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerState;
import game.model.Position;

public class SealAction implements SkillAction {
    private final Position target;

    public SealAction(Position target) {
        this.target = target;
    }

    @Override
    public boolean validate(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());
        // ต้องมีพลังงาน >= 2 และเป้าหมายต้องเป็นช่องว่าง
        return actorState.getEnergy() >= 2 && state.getCell(target) == CellType.EMPTY;
    }

    @Override
    public void apply(GameState state) {
        PlayerState actorState = state.getPlayerState(state.getCurrentPlayer());

        actorState.spendEnergy(2); // จ่าย 2 Energy

        // คำนวณเทิร์นที่จะปลดล็อก (สิ้นสุดในเทิร์นของฝ่ายตรงข้าม)
        int unlockTurn = state.getTotalTurnCount() + 1;
        state.sealCell(target, unlockTurn);
    }

    public Position target() {
        return target;
    }
}