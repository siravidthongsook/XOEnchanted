package game.ui.view;

import game.model.GameState;
import game.model.PlayerId;
import game.ui.SkillMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HudView {
    private final Label currentPlayerLabel;
    private final Label turnLabel;
    private final Label scoreXLabel;
    private final Label scoreOLabel;
    private final Label energyXLabel;
    private final Label energyOLabel;
    private final Label statusLabel;
    private final Label modeLabel;
    private final VBox node;

    public HudView() {
        Label titleLabel = new Label("XO ENCHANTED");
        titleLabel.getStyleClass().add("header-label");

        this.modeLabel = createLabel("Mode: PLACE", "hud-value");
        this.currentPlayerLabel = createLabel("Player: X", "hud-value-x");
        this.turnLabel = createLabel("Turn: 1", "hud-label-small");

        // Player X Stats
        VBox xStats = createPlayerStatBox("PLAYER X", "hud-value-x");
        this.scoreXLabel = createLabel("Score: 0", "hud-value-x");
        this.energyXLabel = createLabel("Energy: 0", "hud-value-x");
        xStats.getChildren().addAll(scoreXLabel, energyXLabel);

        // Player O Stats
        VBox oStats = createPlayerStatBox("PLAYER O", "hud-value-o");
        this.scoreOLabel = createLabel("Score: 0", "hud-value-o");
        this.energyOLabel = createLabel("Energy: 0", "hud-value-o");
        oStats.getChildren().addAll(scoreOLabel, energyOLabel);

        this.statusLabel = createLabel("", "hud-label-small");
        this.statusLabel.setWrapText(true);
        this.statusLabel.setMaxWidth(200);

        this.node = new VBox(20,
                titleLabel,
                new VBox(5, createLabel("GAME INFO", "hud-label-small"), modeLabel, currentPlayerLabel, turnLabel),
                xStats,
                oStats,
                new VBox(5, createLabel("LOG", "hud-label-small"), statusLabel)
        );
        this.node.getStyleClass().add("hud-container");
        this.node.setPadding(new Insets(30));
        this.node.setMinWidth(280);
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private VBox createPlayerStatBox(String title, String titleStyleClass) {
        Label l = new Label(title);
        l.getStyleClass().add("hud-label-small");
        l.setStyle("-fx-font-size: 8px;");
        VBox box = new VBox(8, l);
        box.setStyle("-fx-border-color: #333333; -fx-border-width: 0 0 0 2; -fx-padding: 0 0 0 10;");
        return box;
    }

    public VBox node() {
        return node;
    }

    public void showStatus(String status) {
        statusLabel.setText(status);
    }

    public void showPlacedAt(int row, int col) {
        statusLabel.setText("Placed at (" + row + ", " + col + ")");
    }

    public void render(GameState state, SkillMode mode) {
        modeLabel.setText(renderModeLabel(mode));
        PlayerId current = state.getCurrentPlayer();
        currentPlayerLabel.setText("Player: " + current);
        currentPlayerLabel.getStyleClass().removeAll("hud-value-x", "hud-value-o");
        currentPlayerLabel.getStyleClass().add(current == PlayerId.X ? "hud-value-x" : "hud-value-o");

        turnLabel.setText("Turn: " + state.getTotalTurnCount());

        scoreXLabel.setText("Score: " + state.getPlayerState(PlayerId.X).getScore());
        energyXLabel.setText("Energy: " + state.getPlayerState(PlayerId.X).getEnergy());

        scoreOLabel.setText("Score: " + state.getPlayerState(PlayerId.O).getScore());
        energyOLabel.setText("Energy: " + state.getPlayerState(PlayerId.O).getEnergy());

        if (state.isGameOver()) {
            statusLabel.setText("GAME OVER! WINNER: " + state.getWinner());
            statusLabel.setStyle("-fx-text-fill: yellow;");
        } else if (state.isSuddenDeath()) {
            statusLabel.setText("SUDDEN DEATH ACTIVE!");
            statusLabel.setStyle("-fx-text-fill: orange;");
        }
    }

    private String renderModeLabel(SkillMode mode) {
        return switch (mode) {
            case PLACE -> "PLACE";
            case SEAL -> "SEAL";
            case SHIFT -> "SHIFT";
            case DISRUPT -> "DISRUPT";
            case DOUBLE_PLACE -> "DOUBLE PLACE";
        };
    }
}

