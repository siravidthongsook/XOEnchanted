package game.engine;

import game.model.CellType;
import game.model.GameState;
import game.model.PlayerId;
import game.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineTest {

    @Test
    void precomputedLineCountIsTwentyEight() {
        assertEquals(28, LineDetector.allLines().size());
    }

    @Test
    void sealExpiresImmediatelyAfterOpponentPlacementEvenWhenLineSelectionIsPending() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(2);

        Position sealedCell = new Position(3, 0);
        engine.useSealSkill(sealedCell);
        engine.playPlacementTurn(new Position(0, 0));

        state.setCell(new Position(1, 0), CellType.O);
        state.setCell(new Position(0, 1), CellType.O);
        state.setCell(new Position(1, 2), CellType.O);
        state.setCell(new Position(2, 1), CellType.O);
        state.setCell(new Position(1, 3), CellType.O);
        state.setCell(new Position(3, 1), CellType.O);

        assertEquals(CellType.SEALED, state.getCell(sealedCell));

        engine.playPlacementTurn(new Position(1, 1));

        assertTrue(state.isWaitingForLineSelection());
        assertEquals(CellType.EMPTY, state.getCell(sealedCell));
        assertFalse(state.getActiveSeals().containsKey(sealedCell));
    }

    @Test
    void sealExpiresAfterOpponentRegularPlacement() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(2);

        Position sealedCell = new Position(3, 0);
        engine.useSealSkill(sealedCell);
        engine.playPlacementTurn(new Position(0, 0));

        assertEquals(CellType.SEALED, state.getCell(sealedCell));

        engine.playPlacementTurn(new Position(1, 0));

        assertEquals(CellType.EMPTY, state.getCell(sealedCell));
        assertFalse(state.getActiveSeals().containsKey(sealedCell));
    }

    @Test
    void sealDoesNotExpireAfterOwnerPlacement() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(2);

        Position sealedCell = new Position(3, 0);
        engine.useSealSkill(sealedCell);
        engine.playPlacementTurn(new Position(0, 0));

        assertEquals(CellType.SEALED, state.getCell(sealedCell));
        assertTrue(state.getActiveSeals().containsKey(sealedCell));
    }

    @Test
    void invalidPlacementOnSealedCellDoesNotGrantTurnStartEnergy() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(2);
        Position sealedCell = new Position(0, 0);
        engine.useSealSkill(sealedCell);

        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());

        assertThrows(IllegalArgumentException.class, () -> engine.playPlacementTurn(sealedCell));

        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());

        engine.playPlacementTurn(new Position(0, 1));
        assertEquals(1, state.getPlayerState(PlayerId.X).getEnergy());
    }

    @Test
    void doublePlaceWithThreeEnergyUsesTurnStartGainAndSucceeds() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(3);

        engine.useDoublePlaceSkill(new Position(0, 0), new Position(1, 1));

        assertEquals(CellType.X, state.getCell(new Position(0, 0)));
        assertEquals(CellType.X, state.getCell(new Position(1, 1)));
        assertEquals(0, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(PlayerId.O, state.getCurrentPlayer());
        assertEquals(1, state.getTotalTurnCount());
    }

    @Test
    void invalidDoublePlaceDoesNotGrantTurnStartEnergy() {
        GameEngine engine = new GameEngine();
        GameState state = engine.getGameState();

        state.getPlayerState(PlayerId.X).gainEnergy(3);

        assertThrows(IllegalArgumentException.class,
                () -> engine.useDoublePlaceSkill(new Position(0, 0), new Position(0, 1)));

        assertEquals(3, state.getPlayerState(PlayerId.X).getEnergy());
        assertEquals(CellType.EMPTY, state.getCell(new Position(0, 0)));
        assertEquals(CellType.EMPTY, state.getCell(new Position(0, 1)));
        assertEquals(PlayerId.X, state.getCurrentPlayer());
    }
}
