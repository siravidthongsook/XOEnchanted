package game.engine.skills.doubleplace;

import game.engine.action.DoublePlaceAction;
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

class DoublePlaceActionTest {

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
    void validDoublePlaceOnDiagonalTargetsPlacesTwoPiecesAndCostsEnergy() {
        Position first = new Position(0, 0);
        Position second = new Position(1, 1);

        DoublePlaceAction action = new DoublePlaceAction(first, second);

        assertTrue(action.validate(state));

        int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
        action.apply(state);

        assertEquals(CellType.fromPlayer(currentPlayer), state.getCell(first));
        assertEquals(CellType.fromPlayer(currentPlayer), state.getCell(second));
        assertEquals(initialEnergy - 4, state.getPlayerState(currentPlayer).getEnergy());
    }

    @Test
    void doublePlaceIsInvalidWhenTargetsAreOrthogonallyAdjacent() {
        Position first = new Position(0, 0);
        Position second = new Position(0, 1);

        DoublePlaceAction action = new DoublePlaceAction(first, second);

        assertFalse(action.validate(state));
    }

    @Test
    void doublePlaceIsInvalidWhenTargetsAreSameCell() {
        Position target = new Position(0, 0);
        DoublePlaceAction action = new DoublePlaceAction(target, target);

        assertFalse(action.validate(state));
    }

    @Test
    void doublePlaceIsInvalidWhenAnyTargetIsOccupied() {
        Position first = new Position(0, 0);
        Position second = new Position(2, 2);
        state.placePiece(first, opponentPlayer);

        DoublePlaceAction action = new DoublePlaceAction(first, second);

        assertFalse(action.validate(state));
    }

    @Test
    void doublePlaceIsInvalidWhenAnyTargetIsOutOfBounds() {
        Position first = new Position(-1, 0);
        Position second = new Position(2, 2);

        DoublePlaceAction action = new DoublePlaceAction(first, second);

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }

    @Test
    void doublePlaceIsInvalidWhenEnergyIsBelowCost() {
        state.getPlayerState(currentPlayer).spendEnergy(state.getPlayerState(currentPlayer).getEnergy() - 3);

        DoublePlaceAction action = new DoublePlaceAction(new Position(0, 0), new Position(1, 1));

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }

    @Test
    void doublePlaceIsInvalidWhenTargetIsSealed() {
        Position first = new Position(0, 0);
        Position second = new Position(2, 2);
        state.setCell(first, CellType.SEALED);

        DoublePlaceAction action = new DoublePlaceAction(first, second);

        assertFalse(action.validate(state));
    }
}
