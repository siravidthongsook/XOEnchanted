package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;

public class DisruptAction implements SkillAction {
    private static final int COST = 3; // Updated to 3 Energy
    private final Position target;

    public DisruptAction(Position target) {
        this.target = target;
    }

    @Override
    public boolean validate(GameState state) {
        // 1. Validate Energy (Must have at least 3)
        if (state.getPlayerState(state.getCurrentPlayer()).getEnergy() < COST) {
            return false;
        }

        // 2. Validate Board Bounds
        if (!state.isInsideBoard(target)) {
            return false;
        }

        // 3. Validate Opponent Target (ลบหมากคู่ต่อสู้ 1 ตัว)
        if (state.getCell(target) != CellType.fromPlayer(state.getCurrentPlayer().opponent())) {
            return false;
        }

        // 4. Validate Frozen Immunity (Assuming pieces can be immune)
        if (state.isFrozen(target)) {
            return false;
        }

        return true;
    }

    @Override
    public void apply(GameState state) {
        if (!validate(state)) {
            throw new IllegalStateException("Invalid Disrupt Action: Check energy, target ownership, or frozen status.");
        }

        // 1. Spend Energy (3 Energy)
        state.getPlayerState(state.getCurrentPlayer()).spendEnergy(COST);

        // 2. Remove the target piece (ลบหมากคู่ต่อสู้ 1 ตัว)
        state.clearCell(target);

        // 3. Apply Penalty: skip next turn's normal gain (เทิร์นถัดไปไม่ได้ +1 ปกติ)
        state.getPlayerState(state.getCurrentPlayer()).setSkipNextTurnEnergyGain(true);
    }

    public Position target() {
        return target;
    }
}