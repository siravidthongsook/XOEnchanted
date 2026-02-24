package game.engine;

import game.model.GameState;
import game.model.Line;
import game.model.PlayerId;
import game.model.PlayerState;
import game.model.Position;

import java.util.List;

public class GameEngine {
    private static final int WIN_SCORE = 3;
    private static final int MAX_TURNS = 24;

    private final GameState gameState;
    private GameEventListener eventListener;

    public GameEngine() {
        this.gameState = new GameState();
    }

    public void setEventListener(GameEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void playPlacementTurn(Position position) {
        ensureGameIsActive();
        if (gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Waiting for line selection");
        }

        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        startTurnGainEnergy(actorState);
        gameState.placePiece(position, actor);
        if (eventListener != null) {
            eventListener.onPiecePlaced(position, actor);
        }

        boolean scored = resolveScoring(actor, actorState);

        if (!gameState.isWaitingForLineSelection()) {
            endTurn(actor, scored);
        }
    }

    public void selectLine(Line line) {
        if (!gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Not waiting for line selection");
        }
        if (!gameState.getPendingLines().contains(line)) {
            throw new IllegalArgumentException("Line not in pending lines");
        }

        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        applyScoring(actor, actorState, line);
        gameState.setWaitingForLineSelection(false);
        gameState.setPendingLines(null);

        endTurn(actor, true);
    }

    public void useSealSkill(Position position) {
        throw new UnsupportedOperationException("TODO: implement Seal skill");
    }

    public void useShiftSkill(Position from, Position to) {
        throw new UnsupportedOperationException("TODO: implement Shift skill");
    }

    public void useDisruptSkill(Position position) {
        throw new UnsupportedOperationException("TODO: implement Disrupt skill");
    }

    public void useDoublePlaceSkill(Position first, Position second) {
        throw new UnsupportedOperationException("TODO: implement Double Place skill");
    }

    private void startTurnGainEnergy(PlayerState actorState) {
        int gain = actorState.consumeTurnStartGain();
        actorState.gainEnergy(gain);
        // TODO: apply disrupt penalty and overheat pre-processing.
    }

    private boolean resolveScoring(PlayerId actor, PlayerState actorState) {
        List<Line> lines = LineDetector.detectLines(gameState, actor);
        if (lines.isEmpty()) {
            return false;
        }

        if (lines.size() > 1) {
            gameState.setWaitingForLineSelection(true);
            gameState.setPendingLines(lines);
            if (eventListener != null) {
                eventListener.onLineSelectionRequired(lines);
            }
            return false; // Turn hasn't "finished" scoring yet
        }

        applyScoring(actor, actorState, lines.get(0));
        return true;
    }

    private void applyScoring(PlayerId actor, PlayerState actorState, Line selectedLine) {
        actorState.addScore(1);
        LineDetector.resolveSelectedLine(gameState, selectedLine);
        if (eventListener != null) {
            eventListener.onLineCleared(selectedLine);
        }
        actorState.gainEnergy(1);
        gameState.getPlayerState(actor.opponent()).setPriorityTurn(true);
    }

    private void endTurn(PlayerId actor, boolean scored) {
        // TODO: implement frozen lifecycle, seal expiry, and overheat tracker updates.
        if (gameState.isSuddenDeath() && scored) {
            gameState.setWinner(actor);
            return;
        }

        if (gameState.getPlayerState(actor).getScore() >= WIN_SCORE) {
            gameState.setWinner(actor);
            return;
        }

        gameState.incrementTurn();
        if (gameState.getTotalTurnCount() >= MAX_TURNS && !gameState.isGameOver()) {
            resolveTurnLimitResult();
        }

        if (!gameState.isGameOver()) {
            gameState.switchCurrentPlayer();
        }
    }

    private void resolveTurnLimitResult() {
        int xScore = gameState.getPlayerState(PlayerId.X).getScore();
        int oScore = gameState.getPlayerState(PlayerId.O).getScore();

        if (xScore > oScore) {
            gameState.setWinner(PlayerId.X);
            return;
        }
        if (oScore > xScore) {
            gameState.setWinner(PlayerId.O);
            return;
        }

        gameState.setSuddenDeath(true);
    }

    private void ensureGameIsActive() {
        if (gameState.isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }
    }
}
