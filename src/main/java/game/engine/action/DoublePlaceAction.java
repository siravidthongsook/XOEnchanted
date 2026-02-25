package game.engine.action;

import game.model.GameState;
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
        // TODO: validate caster energy, both targets empty, and non-adjacent rule.
        throw new UnsupportedOperationException("TODO: implement DoublePlaceAction.validate");
    }

    @Override
    public void apply(GameState state) {
        // TODO: place two pieces for current player and consume action group.
        throw new UnsupportedOperationException("TODO: implement DoublePlaceAction.apply");
    }

    public Position first() {
        return first;
    }

    public Position second() {
        return second;
    }
}
