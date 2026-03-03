package game.ui.view;

import game.model.GameState;
import game.model.PlayerId;
import game.ui.SkillMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class HudView {
    private static final class LegendSegment {
        private final String text;
        private final String styleClass;

        private LegendSegment(String text, String styleClass) {
            this.text = text;
            this.styleClass = styleClass;
        }
    }

    private final Label currentPlayerLabel;
    private final Label turnLabel;
    private final Label scoreXLabel;
    private final Label scoreOLabel;
    private final Label energyXLabel;
    private final Label energyOLabel;
    private final Label statusLabel;
    private final TextFlow legendFlow;
    private final Label modeLabel;
    private final VBox node;
    private final VBox logNode;
    private final VBox legendNode;

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
        this.statusLabel.getStyleClass().add("top-status-label");
        this.statusLabel.setWrapText(true);
        this.statusLabel.setMaxWidth(700);
        this.statusLabel.setAlignment(Pos.CENTER);
        this.statusLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        this.statusLabel.setTranslateY(-30);

        this.logNode = new VBox(statusLabel);
        this.logNode.getStyleClass().add("top-log-container");
        this.logNode.setPadding(new Insets(0, 0, 10, 0));
        this.logNode.setAlignment(Pos.CENTER);

        this.legendFlow = new TextFlow();
        this.legendFlow.setTextAlignment(TextAlignment.CENTER);
        this.legendFlow.setMaxWidth(700);

        this.legendNode = new VBox(legendFlow);
        this.legendNode.getStyleClass().add("bottom-legend-container");
        this.legendNode.setAlignment(Pos.CENTER);
        this.legendNode.setPadding(new Insets(10, 0, 0, 0));
        this.legendNode.setVisible(false);
        this.legendNode.setMouseTransparent(true);

        showDisruptLegend();

        this.node = new VBox(20,
                titleLabel,
                new VBox(5, createLabel("GAME INFO", "hud-label-small"), modeLabel, currentPlayerLabel, turnLabel),
                xStats,
                oStats
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

    public VBox logNode() {
        return logNode;
    }

    public VBox legendNode() {
        return legendNode;
    }

    public void showStatus(String status) {
        statusLabel.setText(status);
    }

    public void showPlacedAt(int row, int col) {
        statusLabel.setText("Placed at (" + row + ", " + col + ")");
    }

    public void hideLegend() {
        legendNode.setVisible(false);
    }

    public void showSealLegend() {
        setLegendContent(
                segment("Seal Legend - ", "bottom-legend-label"),
                segment("Light", "legend-cyan"),
                segment(": selectable empty cell", "bottom-legend-label")
        );
        legendNode.setVisible(true);
    }

    public void showMoveLegend() {
        setLegendContent(
                segment("Move Legend - ", "bottom-legend-label"),
                segment("Cyan", "legend-cyan"),
                segment(": movable source, ", "bottom-legend-label"),
                segment("Green", "legend-green"),
                segment(": valid destination, ", "bottom-legend-label"),
                segment("Orange", "legend-orange"),
                segment(": frozen source, ", "bottom-legend-label"),
                segment("Dim", "legend-muted"),
                segment(": no valid destination", "bottom-legend-label")
        );
        legendNode.setVisible(true);
    }

    public void showDisruptLegend() {
        setLegendContent(
                segment("Disrupt Legend - ", "bottom-legend-label"),
                segment("Red", "legend-red"),
                segment(": valid target, ", "bottom-legend-label"),
                segment("Orange", "legend-orange"),
                segment(": frozen (immune)", "bottom-legend-label")
        );
        legendNode.setVisible(true);
    }

    public void showDoublePlaceLegend() {
        setLegendContent(
                segment("Double Place Legend - ", "bottom-legend-label"),
                segment("Blue", "legend-blue"),
                segment(": first placement options, ", "bottom-legend-label"),
                segment("Green", "legend-green"),
                segment(": valid second placement, ", "bottom-legend-label"),
                segment("Dim", "legend-muted"),
                segment(": blocked empty cell", "bottom-legend-label")
        );
        legendNode.setVisible(true);
    }

    private LegendSegment segment(String text, String styleClass) {
        return new LegendSegment(text, styleClass);
    }

    private void setLegendContent(LegendSegment... segments) {
        legendFlow.getChildren().clear();
        for (LegendSegment segment : segments) {
            Text textNode = new Text(segment.text);
            textNode.getStyleClass().add(segment.styleClass);
            legendFlow.getChildren().add(textNode);
        }
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

        statusLabel.setStyle("-fx-text-fill: #E0E0E0;");
        if (state.isGameOver()) {
            if (state.getWinner() != null) {
                statusLabel.setText("GAME OVER! WINNER: " + state.getWinner() + " (RESET GAME to play again)");
            } else {
                statusLabel.setText("GAME OVER! RESULT: TIE (RESET GAME to play again)");
            }
            statusLabel.setStyle("-fx-text-fill: yellow;");
        } else if (state.isSuddenDeath()) {
            statusLabel.setText("TIE AFTER TURN LIMIT! SUDDEN DEATH ACTIVE");
            statusLabel.setStyle("-fx-text-fill: orange;");
        }
    }

    private String renderModeLabel(SkillMode mode) {
        return switch (mode) {
            case PLACE -> "PLACE";
            case SEAL -> "SEAL";
            case MOVE -> "MOVE";
            case DISRUPT -> "DISRUPT";
            case DOUBLE_PLACE -> "DOUBLE PLACE";
        };
    }
}
