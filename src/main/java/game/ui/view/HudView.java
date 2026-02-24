package game.ui.view;

import game.model.GameState;
import game.model.PlayerId;
import game.ui.SkillMode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HudView {
    private final BorderPane node;
    private final HBox scoreXBox;
    private final HBox energyXBox;
    private final HBox scoreOBox;
    private final HBox energyOBox;
    private final Label statusLabel;
    private final Label modeLabel;

    // เพิ่ม Label สำหรับบอกเทิร์นและผู้เล่น
    private final Label currentPlayerLabel;
    private final Label turnLabel;

    public HudView() {
        Label xIcon = new Label();
        xIcon.getStyleClass().addAll("piece-x", "top-icon");
        scoreXBox = new HBox(4);
        energyXBox = new HBox(4);
        VBox xBars = new VBox(6, scoreXBox, energyXBox);
        xBars.setAlignment(Pos.CENTER_LEFT);
        HBox xPlayerStats = new HBox(15, xIcon, xBars);
        xPlayerStats.setAlignment(Pos.CENTER_LEFT);

        Label oIcon = new Label();
        oIcon.getStyleClass().addAll("piece-o", "top-icon");
        scoreOBox = new HBox(4);
        energyOBox = new HBox(4);
        VBox oBars = new VBox(6, scoreOBox, energyOBox);
        oBars.setAlignment(Pos.CENTER_RIGHT);
        HBox oPlayerStats = new HBox(15, oBars, oIcon);
        oPlayerStats.setAlignment(Pos.CENTER_RIGHT);

        // Center Info
        modeLabel = new Label("MODE: PLACE");
        modeLabel.getStyleClass().add("hud-value");

        currentPlayerLabel = new Label("PLAYER: X");
        currentPlayerLabel.getStyleClass().add("hud-value-x");

        turnLabel = new Label("TURN: 0 / 24");
        turnLabel.getStyleClass().add("hud-label-small");

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        VBox centerBox = new VBox(5, modeLabel, currentPlayerLabel, turnLabel, statusLabel);
        centerBox.setAlignment(Pos.CENTER);

        node = new BorderPane();
        node.setLeft(xPlayerStats);
        node.setCenter(centerBox);
        node.setRight(oPlayerStats);
        node.getStyleClass().add("top-hud-container");
    }

    public BorderPane node() { return node; }
    public void showStatus(String status) { statusLabel.setText(status); }

    public void render(GameState state, SkillMode mode) {
        modeLabel.setText("MODE: " + mode.name());

        // อัปเดตผู้เล่นปัจจุบัน
        currentPlayerLabel.setText("CURRENT PLAYER: " + state.getCurrentPlayer());
        currentPlayerLabel.getStyleClass().removeAll("hud-value-x", "hud-value-o");
        currentPlayerLabel.getStyleClass().add(state.getCurrentPlayer() == PlayerId.X ? "hud-value-x" : "hud-value-o");

        // อัปเดตจำนวนเทิร์น
        turnLabel.setText("TURN: " + state.getTotalTurnCount() + " / 24");

        int maxScore = 5;
        int maxEnergy = 10;
        updateBars(scoreXBox, state.getPlayerState(PlayerId.X).getScore(), maxScore);
        updateBars(energyXBox, state.getPlayerState(PlayerId.X).getEnergy(), maxEnergy);
        updateBars(scoreOBox, state.getPlayerState(PlayerId.O).getScore(), maxScore);
        updateBars(energyOBox, state.getPlayerState(PlayerId.O).getEnergy(), maxEnergy);

        if (state.isGameOver()) {
            statusLabel.setText("GAME OVER!");
        } else if (state.isSuddenDeath()) {
            statusLabel.setText("SUDDEN DEATH ACTIVE!");
            statusLabel.setStyle("-fx-text-fill: orange;");
        } else {
            statusLabel.setText(""); // ล้างข้อความถ้าไม่ได้จบเกม
        }
    }

    private void updateBars(HBox container, int currentVal, int maxVal) {
        container.getChildren().clear();
        int displayVal = Math.min(currentVal, maxVal);
        for (int i = 0; i < maxVal; i++) {
            Region block = new Region();
            block.getStyleClass().add("stat-block");
            if (i < displayVal) block.getStyleClass().add("stat-block-filled");
            container.getChildren().add(block);
        }
    }
}