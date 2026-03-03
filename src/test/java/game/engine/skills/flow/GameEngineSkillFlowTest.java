package game.engine.skills.flow;

import game.engine.GameEngine;
import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineSkillFlowTest {

    @Test
    void allSkillsAreBlockedWhileWaitingForLineSelection() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.setCell(new Position(0, 1), CellType.X);
        state.setCell(new Position(0, 2), CellType.X);
        state.setCell(new Position(1, 0), CellType.X);
        state.setCell(new Position(2, 0), CellType.X);

        engine.playPlacementTurn(new Position(0, 0));

        assertTrue(state.isWaitingForLineSelection());
        assertThrows(IllegalStateException.class, () -> engine.useSealSkill(new Position(3, 3)));
        assertThrows(IllegalStateException.class, () -> engine.useMoveSkill(new Position(0, 0), new Position(3, 3)));
        assertThrows(IllegalStateException.class, () -> engine.useDisruptSkill(new Position(3, 3)));
        assertThrows(IllegalStateException.class, () -> engine.useDoublePlaceSkill(new Position(3, 2), new Position(2, 3)));
    }

    @Test
    void moveThenPlacementInSameTurnAppliesTurnStartGainOnlyOnce() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.placePiece(new Position(1, 1), PlayerId.X);
        state.getPlayerState(PlayerId.X).gainEnergy(1);

        engine.useMoveSkill(new Position(1, 1), new Position(2, 2));

        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(CellType.X, state.getCell(new Position(2, 2)));

        engine.playPlacementTurn(new Position(0, 0));

        assertEquals(1, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(PlayerId.O, state.getCurrentPlayer());
        assertEquals(1, state.getTotalTurnCount());
    }

    @Test
    void disruptPenaltySkipsNextTurnStartGainThenClears() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.placePiece(new Position(2, 2), PlayerId.O);
        state.getPlayerState(PlayerId.X).gainEnergy(3);

        engine.useDisruptSkill(new Position(2, 2));
        assertTrue(state.getPlayerState(PlayerId.X).isSkipNextTurnEnergyGain());

        engine.playPlacementTurn(new Position(0, 0));
        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());
        assertFalse(state.getPlayerState(PlayerId.X).isSkipNextTurnEnergyGain());

        engine.playPlacementTurn(new Position(3, 3));
        engine.playPlacementTurn(new Position(0, 1));

        assertEquals(1, state.getPlayerState(PlayerId.X).getEnergy());
    }

    @Test
    void doublePlaceUsesPriorityTurnGainAndConsumesPriorityFlag() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(2);
        state.getPlayerState(PlayerId.X).setPriorityTurn(true);

        engine.useDoublePlaceSkill(new Position(0, 0), new Position(1, 1));

        assertEquals(CellType.X, state.getCell(new Position(0, 0)));
        assertEquals(CellType.X, state.getCell(new Position(1, 1)));
        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());
        assertFalse(state.getPlayerState(PlayerId.X).isPriorityTurn());
        assertEquals(PlayerId.O, state.getCurrentPlayer());
    }

    @Test
    void sealExpiresAfterOpponentsDoublePlaceTurn() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        Position sealedCell = new Position(3, 0);
        state.getPlayerState(PlayerId.X).gainEnergy(2);
        engine.useSealSkill(sealedCell);
        engine.playPlacementTurn(new Position(0, 0));

        assertEquals(PlayerId.O, state.getCurrentPlayer());
        assertEquals(CellType.SEALED, state.getCell(sealedCell));

        state.getPlayerState(PlayerId.O).gainEnergy(3);
        engine.useDoublePlaceSkill(new Position(1, 1), new Position(2, 2));

        assertEquals(CellType.EMPTY, state.getCell(sealedCell));
        assertFalse(state.getActiveSeals().containsKey(sealedCell));
    }
}
