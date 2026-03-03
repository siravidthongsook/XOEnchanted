package game.engine.skills.seal;

import game.engine.action.SealAction;
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

class SealActionTest {

    private GameState state;
    private PlayerId currentPlayer;

    @BeforeEach
    void setUp() {
        state = new GameState();
        currentPlayer = state.getCurrentPlayer();
        state.getPlayerState(currentPlayer).gainEnergy(10);
    }

    @Test
    void validSealMarksCellTracksSealAndCostsEnergy() {
        Position target = new Position(1, 1);
        SealAction action = new SealAction(target);

        assertTrue(action.validate(state));

        int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
        action.apply(state);

        assertEquals(CellType.SEALED, state.getCell(target));
        assertEquals(initialEnergy - 2, state.getPlayerState(currentPlayer).getEnergy());
        assertTrue(state.getActiveSeals().containsKey(target));
    }

    @Test
    void sealIsInvalidWhenEnergyIsBelowCost() {
        int energyToDrain = state.getPlayerState(currentPlayer).getEnergy() - 1;
        state.getPlayerState(currentPlayer).spendEnergy(energyToDrain);

        Position target = new Position(1, 1);
        SealAction action = new SealAction(target);

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }

    @Test
    void sealIsInvalidWhenCellIsNotEmpty() {
        Position target = new Position(1, 1);
        state.placePiece(target, currentPlayer);

        SealAction action = new SealAction(target);

        assertFalse(action.validate(state));
    }

    @Test
    void sealValidateThrowsForOutOfBoundsTarget() {
        SealAction action = new SealAction(new Position(-1, 0));

        assertThrows(IllegalArgumentException.class, () -> action.validate(state));
        assertThrows(IllegalArgumentException.class, () -> action.apply(state));
    }
}
