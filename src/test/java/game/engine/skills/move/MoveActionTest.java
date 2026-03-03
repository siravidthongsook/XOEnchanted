package game.engine.skills.move;

import game.engine.action.MoveAction;
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

class MoveActionTest {

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
    void validMoveRelocatesPieceResetsMetadataAndCostsEnergy() {
        Position from = new Position(1, 1);
        Position to = new Position(3, 3);
        state.placePiece(from, currentPlayer);

        MoveAction action = new MoveAction(from, to);

        assertTrue(action.validate(state));

        int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
        action.apply(state);

        assertEquals(CellType.EMPTY, state.getCell(from));
        assertEquals(CellType.fromPlayer(currentPlayer), state.getCell(to));
        assertEquals(initialEnergy - 1, state.getPlayerState(currentPlayer).getEnergy());
        assertEquals(0, state.getPieceInactivity(to));
    }

    @Test
    void moveAllowsDiagonalDestination() {
        Position from = new Position(1, 1);
        Position to = new Position(2, 2);
        state.placePiece(from, currentPlayer);

        MoveAction action = new MoveAction(from, to);

        assertTrue(action.validate(state));
    }

    @Test
    void moveIsInvalidWhenSourcePieceBelongsToOpponent() {
        Position from = new Position(1, 1);
        Position to = new Position(1, 2);
        state.placePiece(from, opponentPlayer);

        MoveAction action = new MoveAction(from, to);

        assertFalse(action.validate(state));
    }

    @Test
    void moveIsInvalidWhenSourcePieceIsFrozen() {
        Position from = new Position(1, 1);
        Position to = new Position(1, 2);
        state.placePiece(from, currentPlayer);
        state.addFrozenCell(from);

        MoveAction action = new MoveAction(from, to);

        assertFalse(action.validate(state));
    }

    @Test
    void moveIsInvalidWhenDestinationIsOccupied() {
        Position from = new Position(1, 1);
        Position to = new Position(1, 2);
        state.placePiece(from, currentPlayer);
        state.placePiece(to, opponentPlayer);

        MoveAction action = new MoveAction(from, to);

        assertFalse(action.validate(state));
    }

    @Test
    void moveApplyClearsStaleFrozenMetadataAtDestination() {
        Position from = new Position(1, 1);
        Position to = new Position(3, 0);
        state.placePiece(from, currentPlayer);
        state.addFrozenCell(to);
        state.setPieceInactivity(to, 2);

        MoveAction action = new MoveAction(from, to);

        assertTrue(action.validate(state));
        action.apply(state);

        assertFalse(state.isFrozen(to));
        assertEquals(0, state.getPieceInactivity(to));
    }

    @Test
    void moveIsInvalidWhenEnergyIsBelowCost() {
        state.getPlayerState(currentPlayer).spendEnergy(state.getPlayerState(currentPlayer).getEnergy());
        Position from = new Position(1, 1);
        Position to = new Position(1, 2);
        state.placePiece(from, currentPlayer);

        MoveAction action = new MoveAction(from, to);

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }

    @Test
    void moveIsInvalidWhenEitherPositionIsOutOfBounds() {
        MoveAction action = new MoveAction(new Position(-1, 0), new Position(1, 2));

        assertFalse(action.validate(state));
        assertThrows(IllegalStateException.class, () -> action.apply(state));
    }
}
