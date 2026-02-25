package game.engine.action;

import game.model.GameState;
import game.model.Position;

public class SealAction implements SkillAction {
    private final Position target;

    public SealAction(Position target) {
        this.target = target;
    }

    @Override
    public boolean validate(GameState state) {
        // TODO: validate energy cost and "empty + not already sealed" target rules.
        throw new UnsupportedOperationException("TODO: implement SealAction.validate");
    }

    @Override
    public void apply(GameState state) {
        // TODO: mark target as sealed and attach next-turn expiry tracking.
        throw new UnsupportedOperationException("TODO: implement SealAction.apply");
    }

    public Position target() {
        return target;
    }
}
