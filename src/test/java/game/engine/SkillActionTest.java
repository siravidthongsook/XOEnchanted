package game.engine;

import game.engine.action.SealAction;
import game.engine.action.ShiftAction;
import game.engine.action.DoublePlaceAction;
import game.engine.action.DisruptAction;
import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

    class SkillActionTest {

        private GameState state;
        private PlayerId currentPlayer;
        private PlayerId opponentPlayer;

        @BeforeEach
        void setUp() {
            // Run this before every single test to ensure a clean slate
            state = new GameState();
            currentPlayer = state.getCurrentPlayer(); // Usually PlayerId.X
            opponentPlayer = currentPlayer.opponent(); // Usually PlayerId.O

            // Give the current player plenty of energy for testing valid actions
            state.getPlayerState(currentPlayer).gainEnergy(10);
        }

        // ==========================================
        // SEAL ACTION TESTS (Cost: 2, Empty Target)
        // ==========================================

        @Test
        void testSealAction_Valid() {
            Position target = new Position(1, 1);
            SealAction action = new SealAction(target);

            assertTrue(action.validate(state), "Seal should be valid on an empty cell with enough energy");

            int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
            action.apply(state);

            assertEquals(CellType.SEALED, state.getCell(target), "Cell should be marked as SEALED");
            assertEquals(initialEnergy - 2, state.getPlayerState(currentPlayer).getEnergy(), "Seal should cost 2 energy");
            assertTrue(state.getActiveSeals().containsKey(target), "Seal should be tracked in GameState");
        }

        @Test
        void testSealAction_Invalid_NotEnoughEnergy() {
            // Drain energy to 1
            state.getPlayerState(currentPlayer).spendEnergy(9);

            Position target = new Position(1, 1);
            SealAction action = new SealAction(target);

            assertFalse(action.validate(state), "Seal should be invalid with < 2 energy");
            assertThrows(IllegalStateException.class, () -> action.apply(state));
        }

        @Test
        void testSealAction_Invalid_CellNotEmpty() {
            Position target = new Position(1, 1);
            state.placePiece(target, currentPlayer); // Occupy the cell

            SealAction action = new SealAction(target);

            assertFalse(action.validate(state), "Seal should be invalid if cell is not empty");
        }

        // ==========================================
        // SHIFT ACTION TESTS (Cost: 2, Own piece, Orthogonal)
        // ==========================================

        @Test
        void testShiftAction_Valid() {
            Position from = new Position(1, 1);
            Position to = new Position(1, 2); // 1 step right (Orthogonal)
            state.placePiece(from, currentPlayer);

            ShiftAction action = new ShiftAction(from, to);

            assertTrue(action.validate(state), "Shift should be valid orthogonally to an empty cell");

            int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
            action.apply(state);

            assertEquals(CellType.EMPTY, state.getCell(from), "Original cell should be empty");
            assertEquals(CellType.fromPlayer(currentPlayer), state.getCell(to), "Piece should be at new cell");
            assertEquals(initialEnergy - 2, state.getPlayerState(currentPlayer).getEnergy(), "Shift should cost 2 energy");
            assertEquals(0, state.getPieceInactivity(to), "Inactivity counter should be reset to 0");
        }

        @Test
        void testShiftAction_Invalid_Diagonal() {
            Position from = new Position(1, 1);
            Position to = new Position(2, 2); // Diagonal move
            state.placePiece(from, currentPlayer);

            ShiftAction action = new ShiftAction(from, to);

            assertFalse(action.validate(state), "Shift should be invalid for diagonal movement");
        }

        @Test
        void testShiftAction_Invalid_WrongOwner() {
            Position from = new Position(1, 1);
            Position to = new Position(1, 2);
            state.placePiece(from, opponentPlayer); // Opponent's piece

            ShiftAction action = new ShiftAction(from, to);

            assertFalse(action.validate(state), "Shift should be invalid on opponent's piece");
        }

        // ==========================================
        // DISRUPT ACTION TESTS (Cost: 3, Opponent piece)
        // ==========================================

        @Test
        void testDisruptAction_Valid() {
            Position target = new Position(2, 2);
            state.placePiece(target, opponentPlayer);

            DisruptAction action = new DisruptAction(target);

            assertTrue(action.validate(state), "Disrupt should be valid on opponent's piece");

            int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
            action.apply(state);

            assertEquals(CellType.EMPTY, state.getCell(target), "Target cell should be cleared");
            assertEquals(initialEnergy - 3, state.getPlayerState(currentPlayer).getEnergy(), "Disrupt should cost 3 energy");
            assertTrue(state.getPlayerState(currentPlayer).isSkipNextTurnEnergyGain(), "Penalty flag should be set to true");
        }

        @Test
        void testDisruptAction_Invalid_OwnPiece() {
            Position target = new Position(2, 2);
            state.placePiece(target, currentPlayer); // Current player's own piece

            DisruptAction action = new DisruptAction(target);

            assertFalse(action.validate(state), "Disrupt should be invalid on your own piece");
        }

        @Test
        void testDisruptAction_Invalid_FrozenImmunity() {
            Position target = new Position(2, 2);
            state.placePiece(target, opponentPlayer);
            state.addFrozenCell(target); // Make it immune

            DisruptAction action = new DisruptAction(target);

            assertFalse(action.validate(state), "Disrupt should be invalid on frozen pieces");
        }

        // ==========================================
        // DOUBLE PLACE ACTION TESTS (Cost: 4, 2 Empty, Not Orthogonal)
        // ==========================================

        @Test
        void testDoublePlaceAction_Valid_Diagonal() {
            Position first = new Position(0, 0);
            Position second = new Position(1, 1); // Diagonal (Not orthogonally touching)

            DoublePlaceAction action = new DoublePlaceAction(first, second);

            assertTrue(action.validate(state), "Double place should be valid for non-orthogonal targets");

            int initialEnergy = state.getPlayerState(currentPlayer).getEnergy();
            action.apply(state);

            assertEquals(CellType.fromPlayer(currentPlayer), state.getCell(first), "First piece should be placed");
            assertEquals(CellType.fromPlayer(currentPlayer), state.getCell(second), "Second piece should be placed");
            assertEquals(initialEnergy - 4, state.getPlayerState(currentPlayer).getEnergy(), "Double Place should cost 4 energy");
        }

        @Test
        void testDoublePlaceAction_Invalid_OrthogonallyAdjacent() {
            Position first = new Position(0, 0);
            Position second = new Position(0, 1); // Orthogonally adjacent (Right next to it)

            DoublePlaceAction action = new DoublePlaceAction(first, second);

            assertFalse(action.validate(state), "Double place should be invalid if targets touch orthogonally");
        }

        @Test
        void testDoublePlaceAction_Invalid_SameCell() {
            Position target = new Position(0, 0);

            DoublePlaceAction action = new DoublePlaceAction(target, target);

            assertFalse(action.validate(state), "Double place should be invalid if both targets are the same cell");
        }

        @Test
        void testDoublePlaceAction_Invalid_Occupied() {
            Position first = new Position(0, 0);
            Position second = new Position(2, 2);
            state.placePiece(first, opponentPlayer); // Occupy the first target

            DoublePlaceAction action = new DoublePlaceAction(first, second);

            assertFalse(action.validate(state), "Double place should be invalid if a target is already occupied");
        }
    }

