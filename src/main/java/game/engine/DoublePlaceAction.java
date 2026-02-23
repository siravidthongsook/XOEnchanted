package game.engine;

import game.model.PlayerId;
import game.model.Position;

/**
 * Action to place two pieces in a single turn.
 * Restriction: The two positions must NOT be orthogonally adjacent.
 * Note: Still only awards maximum 1 point per turn (must choose 1 line if multiple form).
 * Cost: 4 energy
 */
public record DoublePlaceAction(PlayerId player, Position pos1, Position pos2) implements Action {
    
    @Override
    public int getEnergyCost() {
        return 4;
    }
}
