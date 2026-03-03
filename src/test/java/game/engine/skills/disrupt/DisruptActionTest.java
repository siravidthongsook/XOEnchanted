package game.engine.skills.disrupt;

import game.engine.action.DisruptAction;
import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisruptActionTest {

    private GameState state;
    private PlayerId currentPlayer;
    private PlayerId opponentPlayer;

    @BeforeEach
    void setUp() {
        state = new GameState();
        currentPlayer = state.getCurrentPlayer();
        opponentPlayer = currentPlayer.opponent();
        state.getPlayerState(currentPlayer).gainEnergy(10);
    }

    @Test
    void validDisruptClearsOpponentPieceCostsEnergyAndAppliesPenalty() {
        Position target = new Position(2, 2);
        state.placePiece(target, opponentPlayer);

        DisruptAction action = new DisruptAction(target);

        assertTrue(action.validate(state));

        int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
        action.apply(state);

        assertEquals(CellType.EMPTY, state.getCell(target));
        assertEquals(initialEnergy - 3, state.getPlayerState(currentPlayer).getEnergy());
        assertTrue(state.getPlayerState(currentPlayer).isSkipNextTurnEnergyGain());
    }

    @Test
    void disruptIsInvalidOnCurrentPlayersOwnPiece() {
        Position target = new Position(2, 2);
        state.placePiece(target, currentPlayer);

        DisruptAction action = new DisruptAction(target);

        assertFalse(action.validate(state));
    }

    @Test
    void disruptIsInvalidOnFrozenTarget() {
        Position target = new Position(2, 2);
        state.placePiece(target, opponentPlayer);
        state.addFrozenCell(target);

        DisruptAction action = new DisruptAction(target);

        assertFalse(action.validate(state));
    }

    @Test
    void disruptIsInvalidWhenEnergyIsBelowCost() {
        state.getPlayerState(currentPlayer).spendEnergy(state.getPlayerState(currentPlayer).getEnergy() - 2);
        Position target = new Position(2, 2);
        state.placePiece(target, opponentPlayer);

        DisruptAction action = new DisruptAction(target);

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }

    @Test
    void disruptIsInvalidWhenTargetIsOutOfBounds() {
        DisruptAction action = new DisruptAction(new Position(10, 10));

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }
}
