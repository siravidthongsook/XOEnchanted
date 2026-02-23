package game.engine;

import game.model.PlayerId;
import game.model.Position;

/**
 * Action to shift (move) one of the player's pieces to an adjacent empty cell.
 * Cost: 2 energy
 */
public record ShiftAction(PlayerId player, Position from, Position to) implements Action {
    
    @Override
    public int getEnergyCost() {
        return 2;
    }
}
