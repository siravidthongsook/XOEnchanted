package game.engine.action;

import game.model.GameState;
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
        // TODO: validate ownership, one-step orthogonal movement, and empty destination.
        throw new UnsupportedOperationException("TODO: implement ShiftAction.validate");
    }

    @Override
    public void apply(GameState state) {
        // TODO: move piece and reset/refresh piece freeze counters.
        throw new UnsupportedOperationException("TODO: implement ShiftAction.apply");
    }

    public Position from() {
        return from;
    }

    public Position to() {
        return to;
    }
}
