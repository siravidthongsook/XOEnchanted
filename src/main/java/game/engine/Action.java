package game.engine;

import game.model.PlayerId;

/**
 * Base interface for all player actions.
 * Uses sealed interface to ensure exhaustive handling of all action types.
 */
public sealed interface Action permits
    PlaceAction, ShiftAction, DisruptAction, SealAction, DoublePlaceAction {

    /**
     * Get the player performing this action.
     */
    PlayerId getPlayer();

    /**
     * Get the energy cost of this action.
     */
    int getEnergyCost();
}
