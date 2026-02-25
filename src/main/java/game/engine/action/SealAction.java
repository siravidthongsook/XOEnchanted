package game.engine.action;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;
import game.engine.SealLifecycleService;

public class SealAction implements SkillAction {
    private final Position target;

    public SealAction(Position target) {
        this.target = target;
    }

    @Override
    public boolean validate(GameState state) {
        if(state.getPlayerState(state.getCurrentPlayer()).getEnergy() < 2) {
            return false;
        }
        if(state.getCell(target) != CellType.EMPTY) {
            return false;
        }
        return true;
    }

    @Override
    public void apply(GameState state) {
        if (!validate(state)) {
            throw new IllegalStateException("Invalid Seal Action: Not enough energy or cell not empty.");
        }

        state.getPlayerState(state.getCurrentPlayer()).spendEnergy(2);

        state.setCell(target, CellType.SEALED);

        // 3. Track expiry: If placed on turn N, it blocks the opponent on turn N+1,
        // and expires at the end of N+1 (so it expires when totalTurnCount reaches N+2).
        int expiryTurn = state.getTotalTurnCount() + 2;
        state.addSeal(target, expiryTurn);
    }

    public Position target() {
        return target;
    }
}
