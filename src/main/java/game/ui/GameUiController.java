package game.ui;

import game.engine.GameEngine;
import game.engine.GameEventListener;
import game.model.GameState;
import game.model.Position;
import game.model.Line;
import game.model.PlayerId;

import java.util.Objects;
import java.util.List;

public class GameUiController implements GameEventListener {
    private final GameEngine engine;
    private SkillMode mode;
    private final List<GameEventListener> listeners = new java.util.ArrayList<>();

    public GameUiController() {
        this.engine = new GameEngine();
        this.engine.setEventListener(this);
        this.mode = SkillMode.PLACE;
    }

    public void addEventListener(GameEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onPiecePlaced(Position position, PlayerId playerId) {
        listeners.forEach(l -> l.onPiecePlaced(position, playerId));
    }

    @Override
    public void onLineCleared(Line line) {
        listeners.forEach(l -> l.onLineCleared(line));
    }

    @Override
    public void onLineSelectionRequired(List<Line> lines) {
        listeners.forEach(l -> l.onLineSelectionRequired(lines));
    }

    public GameState state() {
        return engine.getGameState();
    }

    public SkillMode mode() {
        return mode;
    }

    public void setMode(SkillMode mode) {
        this.mode = Objects.requireNonNull(mode, "mode cannot be null");
    }

    public void resetGame() {
        // TODO: keep selected mode while resetting if preferred by UX.
        throw new UnsupportedOperationException("TODO: implement GameUiController.resetGame");
    }

    public void onCellClicked(int row, int col) {
        if (state().isWaitingForLineSelection()) {
            handleLineSelection(row, col);
            return;
        }

        if (mode == SkillMode.PLACE) {
            engine.playPlacementTurn(new Position(row, col));
            return;
        }

        // TODO: implement selection flow for each skill mode.
        throw new UnsupportedOperationException("TODO: implement skill mode click flow");
    }

    private void handleLineSelection(int row, int col) {
        Position clickedPos = new Position(row, col);
        for (game.model.Line line : state().getPendingLines()) {
            if (line.positions().contains(clickedPos)) {
                engine.selectLine(line);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid selection. Click on a highlighted cell.");
    }
}
