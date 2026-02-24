package game.ui.view;

import game.model.GameState;
import game.model.PlayerId;
import game.ui.SkillMode;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HudView {
    private final Label currentPlayerLabel;
    private final Label turnLabel;
    private final Label scoreLabel;
    private final Label energyLabel;
    private final Label statusLabel;
    private final Label modeLabel;
    private final VBox node;

    public HudView() {
        this.currentPlayerLabel = new Label();
        this.turnLabel = new Label();
        this.scoreLabel = new Label();
        this.energyLabel = new Label();
        this.statusLabel = new Label();
        this.modeLabel = new Label("Mode: PLACE");

        Label titleLabel = new Label("XO ENCHANTED 4x4");
        titleLabel.getStyleClass().add("header-label");

        this.node = new VBox(15,
                titleLabel,
                modeLabel,
                currentPlayerLabel,
                turnLabel,
                scoreLabel,
                energyLabel,
                statusLabel
        );
        this.node.getStyleClass().add("hud-container");
        this.node.setMinWidth(250);
    }

    public VBox node() {
        return node;
    }

    public void showStatus(String status) {
        statusLabel.setText(status);
    }

    public void showPlacedAt(int row, int col) {
        statusLabel.setText("Status: placed at (" + row + ", " + col + ")");
    }

    public void render(GameState state, SkillMode mode) {
        modeLabel.setText(renderModeLabel(mode));
        currentPlayerLabel.setText("Current player: " + state.getCurrentPlayer());
        turnLabel.setText("Turn count: " + state.getTotalTurnCount());
        scoreLabel.setText(
                "Score X/O: "
                        + state.getPlayerState(PlayerId.X).getScore()
                        + " / "
                        + state.getPlayerState(PlayerId.O).getScore()
        );
        energyLabel.setText(
                "Energy X/O: "
                        + state.getPlayerState(PlayerId.X).getEnergy()
                        + " / "
                        + state.getPlayerState(PlayerId.O).getEnergy()
        );

        if (state.isGameOver()) {
            statusLabel.setText("Status: game over, winner = " + state.getWinner());
        } else if (state.isSuddenDeath()) {
            statusLabel.setText("Status: sudden death active");
        }
    }

    private String renderModeLabel(SkillMode mode) {
        return switch (mode) {
            case PLACE -> "Mode: PLACE";
            case SEAL -> "Mode: SEAL (TODO)";
            case SHIFT -> "Mode: SHIFT (TODO)";
            case DISRUPT -> "Mode: DISRUPT (TODO)";
            case DOUBLE_PLACE -> "Mode: DOUBLE_PLACE (TODO)";
        };
    }
}
