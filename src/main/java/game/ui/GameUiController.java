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
    private GameEngine engine;
    private SkillMode mode;
    private final List<GameEventListener> listeners = new java.util.ArrayList<>();

    // Tracks the first click for skills that require two targets
    private Position pendingFirstClick = null;

    public GameUiController() {
        initEngine();
        this.mode = SkillMode.PLACE;
    }

    private void initEngine() {
        this.engine = new GameEngine();
        this.engine.setEventListener(this);
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

    // Add the missing forwarder for the turn end event
    @Override
    public void onTurnEnded(PlayerId nextPlayer) {
        // Reset the mode back to default placement when a turn ends
        setMode(SkillMode.PLACE);
        listeners.forEach(l -> l.onTurnEnded(nextPlayer));
    }

    public GameState state() {
        return engine.getGameState();
    }

    public SkillMode mode() {
        return mode;
    }

    public void setMode(SkillMode mode) {
        this.mode = Objects.requireNonNull(mode, "mode cannot be null");
        this.pendingFirstClick = null; // Clear memory when changing modes
    }

    public void resetGame() {
        // Re-initialize the engine and reset UI states
        initEngine();
        setMode(SkillMode.PLACE);
    }

    public void onCellClicked(int row, int col) {
        if (state().isWaitingForLineSelection()) {
            handleLineSelection(row, col);
            return;
        }

        Position clickedPos = new Position(row, col);

        switch (mode) {
            case PLACE:
                engine.playPlacementTurn(clickedPos);
                break;

            case SEAL:
                engine.useSealSkill(clickedPos);
                setMode(SkillMode.PLACE);
                break;

            case DISRUPT:
                engine.useDisruptSkill(clickedPos);
                setMode(SkillMode.PLACE);
                break;

            case SHIFT:
                if (pendingFirstClick == null) {
                    // PRE-VALIDATE: Must click your own piece to shift!
                    if (state().getCell(clickedPos) != game.model.CellType.fromPlayer(state().getCurrentPlayer())) {
                        throw new IllegalArgumentException("You must select your own piece to shift.");
                    }
                    pendingFirstClick = clickedPos;
                } else if (pendingFirstClick.equals(clickedPos)) {
                    pendingFirstClick = null;
                } else {
                    try {
                        engine.useShiftSkill(pendingFirstClick, clickedPos);
                        setMode(SkillMode.PLACE);
                    } finally {
                        pendingFirstClick = null;
                    }
                }
                break;

            case DOUBLE_PLACE:
                if (pendingFirstClick == null) {
                    // PRE-VALIDATE: Double Place requires an empty cell!
                    if (state().getCell(clickedPos) != game.model.CellType.EMPTY) {
                        throw new IllegalArgumentException("Double Place targets must be empty cells.");
                    }
                    pendingFirstClick = clickedPos;
                } else if (pendingFirstClick.equals(clickedPos)) {
                    pendingFirstClick = null;
                } else {
                    try {
                        engine.useDoublePlaceSkill(pendingFirstClick, clickedPos);
                        setMode(SkillMode.PLACE);
                    } finally {
                        pendingFirstClick = null;
                    }
                }
                break;
        }
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

    public Position getPendingFirstClick() {
        return pendingFirstClick;
    }
}