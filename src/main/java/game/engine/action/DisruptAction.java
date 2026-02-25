package game.engine.action;

import game.model.GameState;
import game.model.Position;

public class DisruptAction implements SkillAction {
    private final Position target;

    public DisruptAction(Position target) {
        this.target = target;
    }

    @Override
    public boolean validate(GameState state) {
        // TODO: validate opponent target, frozen immunity, and caster energy.
        throw new UnsupportedOperationException("TODO: implement DisruptAction.validate");
    }

    @Override
    public void apply(GameState state) {
        // TODO: remove target and set caster "skip normal gain" next-turn penalty.
        throw new UnsupportedOperationException("TODO: implement DisruptAction.apply");
    }

    public Position target() {
        return target;
    }
}
