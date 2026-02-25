package game.engine;

import game.model.GameState;
import game.model.PlayerId;
import game.model.PlayerState;

public class OverheatRuleService {
    private static final int OVERHEAT_ENERGY_LEVEL = 5;
    private static final int PENALTY_DROP_TO = 3;
    private static final int MAX_CONSECUTIVE_TURNS = 2;

    public void applyPreGainAdjustment(GameState state, PlayerId actor) {
        PlayerState playerState = state.getPlayerState(actor);

        // If they have hoarded energy for 2 turns...
        if (playerState.getOverheatCounter() >= MAX_CONSECUTIVE_TURNS) {

            int currentEnergy = playerState.getEnergy();

            // Drop their energy to 3 (by spending the difference)
            if (currentEnergy > PENALTY_DROP_TO) {
                playerState.spendEnergy(currentEnergy - PENALTY_DROP_TO);
            }

            // Reset the counter so they don't get punished repeatedly
            // unless they hoard it again
            playerState.setOverheatCounter(0);
        }
    }

    public void updateTrackerAtEndOfTurn(GameState state, PlayerId actor) {
        PlayerState playerState = state.getPlayerState(actor);

        // Check if they are ending the turn at the max energy level
        if (playerState.getEnergy() >= OVERHEAT_ENERGY_LEVEL) {
            playerState.setOverheatCounter(playerState.getOverheatCounter() + 1);
        } else {
            // They spent energy, so reset the consecutive counter
            playerState.setOverheatCounter(0);
        }
    }
}