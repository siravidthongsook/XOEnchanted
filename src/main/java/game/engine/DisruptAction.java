package game.engine;

import game.model.PlayerId;
import game.model.Position;

/**
 * Action to remove an opponent's piece from the board.
 * Cannot target frozen pieces.
 * Penalty: Next turn, no normal energy gain (+1).
 * Cost: 3 energy
 */
public record DisruptAction(PlayerId player, Position target) implements Action {
    
    @Override
    public int getEnergyCost() {
        return 3;
    }
}
