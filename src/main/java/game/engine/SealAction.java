package game.engine;

import game.model.PlayerId;
import game.model.Position;

/**
 * Action to seal an empty cell, preventing placement or shifting for 1 turn.
 * Cost: 2 energy
 */
public record SealAction(PlayerId player, Position target) implements Action {
    
    @Override
    public int getEnergyCost() {
        return 2;
    }
}
