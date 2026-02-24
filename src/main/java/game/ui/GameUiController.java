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
    private Position firstSelection; // เพิ่มตัวแปรจำค่าคลิกแรก
    private final List<GameEventListener> listeners = new java.util.ArrayList<>();

    public GameUiController() {
        this.engine = new GameEngine();
        this.engine.setEventListener(this);
        this.mode = SkillMode.PLACE;
        this.firstSelection = null;
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

    public GameState state() { return engine.getGameState(); }
    public SkillMode mode() { return mode; }

    public void setMode(SkillMode mode) {
        this.mode = Objects.requireNonNull(mode, "mode cannot be null");
        this.firstSelection = null; // รีเซ็ตการคลิกทุกครั้งที่เปลี่ยนโหมด
    }

    public void resetGame() {
        throw new UnsupportedOperationException("TODO: implement GameUiController.resetGame");
    }

    public void onCellClicked(int row, int col) {
        if (state().isWaitingForLineSelection()) {
            handleLineSelection(row, col);
            return;
        }

        Position clickedPos = new Position(row, col);

        if (mode == SkillMode.PLACE) {
            engine.playPlacementTurn(clickedPos);
        } else if (mode == SkillMode.SEAL) {
            engine.useSealSkill(clickedPos);
            setMode(SkillMode.PLACE);
        } else if (mode == SkillMode.DISRUPT) {
            engine.useDisruptSkill(clickedPos);
            setMode(SkillMode.PLACE);
        } else if (mode == SkillMode.SHIFT) {
            if (firstSelection == null) {
                firstSelection = clickedPos; // จำตำแหน่งแรก
                // คุณสามารถใส่โค้ดให้ UI แสดงแสงไฮไลท์ที่ช่องแรกได้ถ้าต้องการ
            } else {
                engine.useShiftSkill(firstSelection, clickedPos);
                setMode(SkillMode.PLACE);
            }
        } else if (mode == SkillMode.DOUBLE_PLACE) {
            if (firstSelection == null) {
                firstSelection = clickedPos; // จำตำแหน่งแรก
            } else {
                engine.useDoublePlaceSkill(firstSelection, clickedPos);
                setMode(SkillMode.PLACE);
            }
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
}