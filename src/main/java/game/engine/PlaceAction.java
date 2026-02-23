package game.engine;

import game.model.PlayerId;
import game.model.Position;

/**
 * Action to place a piece on an empty cell.
 * This is the basic free action available every turn.
 */
public record PlaceAction(PlayerId player, Position position) implements Action {
    
    @Override
    public int getEnergyCost() {
        return 0; // Placing is free
    }
}
