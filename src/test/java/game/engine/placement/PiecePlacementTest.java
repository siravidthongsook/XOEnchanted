package game.engine.placement;

import game.engine.GameEngine;
import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PiecePlacementTest {

    @Test
    void placementTurnGainsEnergyAndSwitchesPlayer() {
        GameEngine engine = new GameEngine();

        engine.playPlacementTurn(new Position(0, 0));

        GameState state = engine.getGameState();
        assertEquals(CellType.X, state.getCell(new Position(0, 0)));
        assertEquals(1, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(PlayerId.O, state.getCurrentPlayer());
        assertEquals(1, state.getTotalTurnCount());
    }

    @Test
    void placementOnOccupiedCellThrowsAndDoesNotAdvanceTurn() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();
        state.setCell(new Position(0, 0), CellType.O);

        assertThrows(IllegalArgumentException.class, () -> engine.playPlacementTurn(new Position(0, 0)));

        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(PlayerId.X, state.getCurrentPlayer());
        assertEquals(0, state.getTotalTurnCount());
    }

    @Test
    void outOfBoundsPlacementThrowsAndDoesNotGrantEnergy() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        assertThrows(IllegalArgumentException.class, () -> engine.playPlacementTurn(new Position(-1, 0)));

        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(PlayerId.X, state.getCurrentPlayer());
        assertEquals(0, state.getTotalTurnCount());
    }

    @Test
    void placementWithPriorityTurnGainsTwoEnergyAndConsumesPriority() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).setPriorityTurn(true);
        engine.playPlacementTurn(new Position(0, 0));

        assertEquals(2, state.getPlayerState(PlayerId.X).getEnergy());
        assertFalse(state.getPlayerState(PlayerId.X).isPriorityTurn());
    }

    @Test
    void placementCreatingTwoLinesWaitsForLineSelectionAndDoesNotEndTurn() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.setCell(new Position(1, 0), CellType.X);
        state.setCell(new Position(1, 2), CellType.X);
        state.setCell(new Position(0, 1), CellType.X);
        state.setCell(new Position(2, 1), CellType.X);

        engine.playPlacementTurn(new Position(1, 1));

        assertTrue(state.isWaitingForLineSelection());
        assertEquals(2, state.getPendingLines().size());
        assertEquals(PlayerId.X, state.getCurrentPlayer());
        assertEquals(0, state.getTotalTurnCount());
        assertEquals(1, state.getPlayerState(PlayerId.X).getEnergy());
    }

    @Test
    void scoringAwardsOnePointClearsSelectedLineAndGivesPriority() {
        GameEngine engine = new GameEngine();

        engine.playPlacementTurn(new Position(0, 0));
        engine.playPlacementTurn(new Position(3, 3));
        engine.playPlacementTurn(new Position(0, 1));
        engine.playPlacementTurn(new Position(3, 2));
        engine.playPlacementTurn(new Position(0, 2));

        GameState state = engine.getGameState();
        assertEquals(1, state.getPlayerState(PlayerId.X).getScore());
        assertEquals(CellType.EMPTY, state.getCell(new Position(0, 0)));
        assertEquals(CellType.EMPTY, state.getCell(new Position(0, 1)));
        assertEquals(CellType.EMPTY, state.getCell(new Position(0, 2)));
        assertTrue(state.getPlayerState(PlayerId.O).isPriorityTurn());
    }

    @Test
    void noWinnerAfterSingleScoringTurn() {
        GameEngine engine = new GameEngine();

        engine.playPlacementTurn(new Position(0, 0));
        engine.playPlacementTurn(new Position(3, 3));
        engine.playPlacementTurn(new Position(0, 1));
        engine.playPlacementTurn(new Position(3, 2));
        engine.playPlacementTurn(new Position(0, 2));

        GameState state = engine.getGameState();
        assertFalse(state.isGameOver());
    }

    @Test
    void fullBoardEndsGameAndHigherScoreWins() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        preloadBoardLeavingOneCell(state);
        state.getPlayerState(PlayerId.X).addScore(2);
        state.getPlayerState(PlayerId.O).addScore(1);

        engine.playPlacementTurn(new Position(3, 3));

        assertTrue(state.isGameOver());
        assertEquals(PlayerId.X, state.getWinner());
    }

    @Test
    void fullBoardEndsAsTieWhenScoresAreEqual() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        preloadBoardLeavingOneCell(state);
        state.getPlayerState(PlayerId.X).addScore(1);
        state.getPlayerState(PlayerId.O).addScore(1);

        engine.playPlacementTurn(new Position(3, 3));

        assertTrue(state.isGameOver());
        assertNull(state.getWinner());
    }

    private static void preloadBoardLeavingOneCell(GameState state) {
        String[] rows = {
                "XXOX",
                "XXOX",
                "OOOO",
                "XXO."
        };

        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[row].length(); col++) {
                char cell = rows[row].charAt(col);
                Position position = new Position(row, col);
                if (cell == 'X') {
                    state.setCell(position, CellType.X);
                } else if (cell == 'O') {
                    state.setCell(position, CellType.O);
                }
            }
        }
    }
}
