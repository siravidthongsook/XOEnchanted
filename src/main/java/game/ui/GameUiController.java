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
                if (state().getPlayerState(state().getCurrentPlayer()).getEnergy() < 2) {
                    throw new IllegalArgumentException("Seal requires at least 2 energy.");
                }
                if (state().getCell(clickedPos) != game.model.CellType.EMPTY) {
                    throw new IllegalArgumentException("Seal target must be an empty cell.");
                }
                engine.useSealSkill(clickedPos);
                setMode(SkillMode.PLACE);
                break;

            case DISRUPT:
                if (state().getCell(clickedPos) != game.model.CellType.fromPlayer(state().getCurrentPlayer().opponent())) {
                    throw new IllegalArgumentException("You must select an opponent piece to disrupt.");
                }
                if (state().isFrozen(clickedPos)) {
                    throw new IllegalArgumentException("That piece is frozen and cannot be disrupted.");
                }
                engine.useDisruptSkill(clickedPos);
                setMode(SkillMode.PLACE);
                break;

            case MOVE:
                if (pendingFirstClick == null) {
                    // PRE-VALIDATE: Must click your own piece to move.
                    if (state().getCell(clickedPos) != game.model.CellType.fromPlayer(state().getCurrentPlayer())) {
                        throw new IllegalArgumentException("You must select your own piece to move.");
                    }
                    if (state().isFrozen(clickedPos)) {
                        throw new IllegalArgumentException("That piece is frozen and cannot be moved.");
                    }
                    if (!hasAnyEmptyCell()) {
                        throw new IllegalArgumentException("No empty destination cells are available for Move.");
                    }
                    pendingFirstClick = clickedPos;
                } else if (pendingFirstClick.equals(clickedPos)) {
                    pendingFirstClick = null;
                } else {
                    try {
                        engine.useMoveSkill(pendingFirstClick, clickedPos);
                        setMode(SkillMode.PLACE);
                    } finally {
                        pendingFirstClick = null;
                    }
                }
                break;

            case DOUBLE_PLACE:
                if (pendingFirstClick == null) {
                    if (state().getCell(clickedPos) != game.model.CellType.EMPTY) {
                        throw new IllegalArgumentException("Double Place targets must be empty cells.");
                    }
                    if (!hasValidDoublePlaceSecondTarget(clickedPos)) {
                        throw new IllegalArgumentException("That cell has no valid second target. Choose another first cell.");
                    }
                    pendingFirstClick = clickedPos;
                } else if (pendingFirstClick.equals(clickedPos)) {
                    pendingFirstClick = null;
                } else {
                    if (state().getCell(clickedPos) != game.model.CellType.EMPTY) {
                        throw new IllegalArgumentException("Double Place second target must be an empty cell.");
                    }
                    if (isOrthogonallyAdjacent(pendingFirstClick, clickedPos)) {
                        throw new IllegalArgumentException("Double Place targets cannot be orthogonally adjacent.");
                    }
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

    private boolean hasAnyEmptyCell() {
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                Position position = new Position(row, col);
                if (state().getCell(position) == game.model.CellType.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasValidDoublePlaceSecondTarget(Position first) {
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                Position second = new Position(row, col);
                if (second.equals(first)) {
                    continue;
                }
                if (state().getCell(second) != game.model.CellType.EMPTY) {
                    continue;
                }
                if (!isOrthogonallyAdjacent(first, second)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOrthogonallyAdjacent(Position first, Position second) {
        int rowDiff = Math.abs(first.row() - second.row());
        int colDiff = Math.abs(first.col() - second.col());
        return rowDiff + colDiff == 1;
    }
}
