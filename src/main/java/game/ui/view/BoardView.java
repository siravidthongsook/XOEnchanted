package game.ui.view;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;
import game.model.PlayerId;
import game.ui.SkillMode;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.KeyValue;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import java.util.function.BiConsumer;

public class BoardView {
    private final StackPane[][] cellContainers;
    private final Region[][] impactLayers;
    private final Label[][] pieceLabels;
    private final GridPane grid;
    private final Pane overlayPane;
    private final StackPane node;
    private final int cellSize;
    private static final Duration IMPACT_FLASH_DURATION = Duration.millis(260);
    private static final Duration FLOATING_TEXT_DURATION = Duration.millis(520);
    private static final Duration LINE_CLEAR_HIGHLIGHT_DURATION = Duration.millis(320);
    private java.util.List<game.model.Line> currentPendingLines;
    private Position lastHoveredPos;
    private final java.util.Set<Position> animatingPositions = new java.util.HashSet<>();

    public BoardView(int boardSize, int cellSize, BiConsumer<Integer, Integer> onCellClicked) {
        this.cellSize = cellSize;
        this.cellContainers = new StackPane[boardSize][boardSize];
        this.impactLayers = new Region[boardSize][boardSize];
        this.pieceLabels = new Label[boardSize][boardSize];
        this.grid = new GridPane();
        this.grid.setAlignment(javafx.geometry.Pos.CENTER);
        this.grid.setHgap(0); 
        this.grid.setVgap(0);
        
        this.overlayPane = new Pane();
        this.overlayPane.setMouseTransparent(true);
        this.overlayPane.setPickOnBounds(false);

        StackPane innerContainer = new StackPane(grid, overlayPane);
        innerContainer.getStyleClass().add("inner-board-container");
        innerContainer.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        this.node = new StackPane(innerContainer);
        this.node.getStyleClass().add("main-board-container");
        this.node.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int targetRow = row;
                int targetCol = col;

                StackPane cell = new StackPane();
                cell.getStyleClass().add("board-cell");
                
                // Solid border for better alignment, balanced with padding to keep content centered
                int right = (col == boardSize - 1) ? 0 : 2;
                int bottom = (row == boardSize - 1) ? 0 : 2;
                cell.setStyle(String.format("-fx-border-width: 0 %d %d 0; -fx-border-color: #333333; -fx-padding: %d 0 0 %d;", 
                              right, bottom, bottom, right));
                
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setAlignment(javafx.geometry.Pos.CENTER);

                Label pieceLabel = new Label();
                pieceLabel.getStyleClass().add("piece-label");
                int pieceFontSize = Math.max(40, (int) Math.round(cellSize * 0.64));
                pieceLabel.setStyle("-fx-font-size: " + pieceFontSize + "px;");
                pieceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                pieceLabel.setAlignment(javafx.geometry.Pos.CENTER);

                Region impactLayer = new Region();
                impactLayer.getStyleClass().add("cell-impact-layer");
                impactLayer.setMouseTransparent(true);
                impactLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                StackPane.setMargin(impactLayer, new Insets(2));
                
                cell.getChildren().addAll(impactLayer, pieceLabel);
                StackPane.setAlignment(pieceLabel, javafx.geometry.Pos.CENTER);
                
                cell.setOnMouseClicked(event -> onCellClicked.accept(targetRow, targetCol));
                cell.setOnMouseEntered(event -> handleMouseEntered(targetRow, targetCol));
                cell.setOnMouseExited(event -> handleMouseExited());

                cellContainers[row][col] = cell;
                impactLayers[row][col] = impactLayer;
                pieceLabels[row][col] = pieceLabel;
                grid.add(cell, col, row);
            }
        }
    }

    private void handleMouseEntered(int row, int col) {
        lastHoveredPos = new Position(row, col);
        updateHighlights();
    }

    private void handleMouseExited() {
        lastHoveredPos = null;
        updateHighlights();
    }

    private void updateHighlights() {
        // Clear all highlight classes first
        for (int row = 0; row < cellContainers.length; row++) {
            for (int col = 0; col < cellContainers[row].length; col++) {
                cellContainers[row][col].getStyleClass().removeAll("board-cell-highlight", "board-cell-line-hover");
            }
        }

        if (currentPendingLines == null || currentPendingLines.isEmpty()) return;

        // Apply cyan highlights for all pending lines
        for (game.model.Line line : currentPendingLines) {
            for (Position p : line.positions()) {
                cellContainers[p.row()][p.col()].getStyleClass().add("board-cell-highlight");
            }
        }

        // Apply yellow highlight for the hovered line
        if (lastHoveredPos != null) {
            for (game.model.Line line : currentPendingLines) {
                if (line.positions().contains(lastHoveredPos)) {
                    for (Position p : line.positions()) {
                        // Remove cyan and add yellow
                        cellContainers[p.row()][p.col()].getStyleClass().remove("board-cell-highlight");
                        cellContainers[p.row()][p.col()].getStyleClass().add("board-cell-line-hover");
                    }
                    break;
                }
            }
        }
    }

    public void animatePiecePlacement(Position position, PlayerId playerId) {
        animateCellImpact(position, playerId);
        Label label = pieceLabels[position.row()][position.col()];
        label.setText(playerId.toString());

        showFloatingText(position, "Placed", "fx-float-placed");
        
        ScaleTransition st = new ScaleTransition(Duration.millis(200), label);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    public void animateLineClear(game.model.Line line) {
        Position startPos = line.first();
        Position endPos = line.fourth();
        
        animatingPositions.addAll(line.positions());
        applyLineClearHighlight(line);
        showFloatingText(line.second(), "Line!", "fx-float-line");

        // Calculate center coordinates for the line
        double startX = startPos.col() * cellSize + cellSize / 2.0;
        double startY = startPos.row() * cellSize + cellSize / 2.0;
        double endX = endPos.col() * cellSize + cellSize / 2.0;
        double endY = endPos.row() * cellSize + cellSize / 2.0;

        javafx.scene.shape.Line strikeLine = new javafx.scene.shape.Line(startX, startY, startX, startY);
        strikeLine.getStyleClass().add("strike-line");
        overlayPane.getChildren().add(strikeLine);

        // Animate the line drawing
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(strikeLine.endXProperty(), startX),
                new KeyValue(strikeLine.endYProperty(), startY)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(strikeLine.endXProperty(), endX),
                new KeyValue(strikeLine.endYProperty(), endY)
            )
        );

        timeline.setOnFinished(e -> {
            // Fade out the pieces and then the strike line immediately after drawing
            SequentialTransition seq = new SequentialTransition();
            
            // Fade out pieces and line in parallel
            ParallelTransition fadeAll = new ParallelTransition();

            for (Position pos : line.positions()) {
                Label label = pieceLabels[pos.row()][pos.col()];
                FadeTransition ft = new FadeTransition(Duration.millis(300), label);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                fadeAll.getChildren().add(ft);
            }

            FadeTransition lineFade = new FadeTransition(Duration.millis(300), strikeLine);
            lineFade.setFromValue(1.0);
            lineFade.setToValue(0.0);
            fadeAll.getChildren().add(lineFade);
            
            seq.getChildren().add(fadeAll);

            seq.setOnFinished(ev -> {
                for (Position pos : line.positions()) {
                    Label label = pieceLabels[pos.row()][pos.col()];
                    label.setText("");
                    label.setOpacity(1.0);
                    animatingPositions.remove(pos);
                }
                overlayPane.getChildren().remove(strikeLine);
            });
            
            seq.play();
        });

        timeline.play();
    }

    public StackPane node() {
        return node;
    }

    private void animateCellImpact(Position position, PlayerId playerId) {
        Region impactLayer = impactLayers[position.row()][position.col()];
        String flashClass = playerId == PlayerId.X ? "cell-impact-x" : "cell-impact-o";
        impactLayer.getStyleClass().add(flashClass);

        PauseTransition removeFlash = new PauseTransition(IMPACT_FLASH_DURATION);
        removeFlash.setOnFinished(event -> impactLayer.getStyleClass().remove(flashClass));
        removeFlash.play();
    }

    private void showFloatingText(Position position, String text, String styleClass) {
        Label floating = new Label(text);
        floating.getStyleClass().addAll("fx-float-label", styleClass);
        floating.setManaged(false);

        double x = position.col() * cellSize + cellSize / 2.0;
        double y = position.row() * cellSize + cellSize * 0.45;
        floating.relocate(x, y);

        overlayPane.getChildren().add(floating);

        javafx.application.Platform.runLater(() -> {
            double width = floating.prefWidth(Region.USE_COMPUTED_SIZE);
            double height = floating.prefHeight(Region.USE_COMPUTED_SIZE);
            floating.relocate(x - width / 2.0, y - height / 2.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(90), floating);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            TranslateTransition rise = new TranslateTransition(FLOATING_TEXT_DURATION, floating);
            rise.setFromY(0.0);
            rise.setToY(-16.0);

            FadeTransition fadeOut = new FadeTransition(FLOATING_TEXT_DURATION, floating);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            ParallelTransition out = new ParallelTransition(rise, fadeOut);
            SequentialTransition sequence = new SequentialTransition(fadeIn, out);
            sequence.setOnFinished(event -> overlayPane.getChildren().remove(floating));
            sequence.play();
        });
    }

    private void applyLineClearHighlight(game.model.Line line) {
        for (Position pos : line.positions()) {
            StackPane cell = cellContainers[pos.row()][pos.col()];
            cell.getStyleClass().add("board-cell-line-clear");
        }

        PauseTransition removeHighlight = new PauseTransition(LINE_CLEAR_HIGHLIGHT_DURATION);
        removeHighlight.setOnFinished(event -> {
            for (Position pos : line.positions()) {
                cellContainers[pos.row()][pos.col()].getStyleClass().remove("board-cell-line-clear");
            }
        });
        removeHighlight.play();
    }

    public void render(GameState state, SkillMode mode, Position pendingFirstClick) {
        this.currentPendingLines = state.isWaitingForLineSelection() ? state.getPendingLines() : null;
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                CellType cellType = state.getCell(pos);

                boolean keepAnimatingLabel = cellType == CellType.EMPTY && animatingPositions.contains(pos);

                Label label = pieceLabels[row][col];
                if (!keepAnimatingLabel) {
                    label.setText(renderCell(cellType));
                    label.setTranslateX((cellType == CellType.O || cellType == CellType.SEALED) ? -1 : 0);

                    // Set piece color based on player
                    label.getStyleClass().removeAll("hud-value-x", "hud-value-o");
                    if (cellType == CellType.X) label.getStyleClass().add("hud-value-x");
                    if (cellType == CellType.O) label.getStyleClass().add("hud-value-o");
                }

                StackPane cell = cellContainers[row][col];
                cell.setDisable(state.isGameOver());

                // Clear state-specific CSS classes before reapplying them
                cell.getStyleClass().removeAll(
                        "board-cell-sealed",
                        "board-cell-selected",
                        "board-cell-skill-target",
                        "board-cell-move-source",
                        "board-cell-move-destination",
                        "board-cell-move-blocked",
                        "board-cell-move-frozen",
                        "board-cell-disrupt-target",
                        "board-cell-disrupt-immune",
                        "board-cell-double-place-first",
                        "board-cell-double-place-second",
                        "board-cell-double-place-blocked"
                );

                // Apply Sealed style
                if (cellType == CellType.SEALED) {
                    cell.getStyleClass().add("board-cell-sealed");
                }

                if (mode == SkillMode.SEAL && cellType == CellType.EMPTY) {
                    cell.getStyleClass().add("board-cell-skill-target");
                }

                if (mode == SkillMode.DISRUPT) {
                    CellType opponentCell = CellType.fromPlayer(state.getCurrentPlayer().opponent());
                    if (cellType == opponentCell && !state.isFrozen(pos)) {
                        cell.getStyleClass().addAll("board-cell-skill-target", "board-cell-disrupt-target");
                    } else if (cellType == opponentCell) {
                        cell.getStyleClass().add("board-cell-disrupt-immune");
                    }
                }

                if (mode == SkillMode.MOVE) {
                    if (pendingFirstClick == null) {
                        boolean selectableSource = cellType == CellType.fromPlayer(state.getCurrentPlayer())
                                && !state.isFrozen(pos)
                                && hasAnyEmptyCell(state);

                        if (selectableSource) {
                            cell.getStyleClass().addAll("board-cell-skill-target", "board-cell-move-source");
                        } else if (cellType == CellType.fromPlayer(state.getCurrentPlayer()) && state.isFrozen(pos)) {
                            cell.getStyleClass().add("board-cell-move-frozen");
                        } else if (cellType == CellType.fromPlayer(state.getCurrentPlayer())) {
                            cell.getStyleClass().add("board-cell-move-blocked");
                        }
                    } else {
                        if (pendingFirstClick.equals(pos)) {
                            cell.getStyleClass().add("board-cell-selected");
                        } else if (cellType == CellType.EMPTY) {
                            cell.getStyleClass().addAll("board-cell-skill-target", "board-cell-move-destination");
                        }
                    }
                }

                if (mode == SkillMode.DOUBLE_PLACE) {
                    if (pendingFirstClick == null) {
                        if (cellType == CellType.EMPTY && hasAnyValidDoublePlaceSecond(state, pos)) {
                            cell.getStyleClass().addAll("board-cell-skill-target", "board-cell-double-place-first");
                        } else if (cellType == CellType.EMPTY) {
                            cell.getStyleClass().add("board-cell-double-place-blocked");
                        }
                    } else {
                        if (pendingFirstClick.equals(pos)) {
                            cell.getStyleClass().add("board-cell-selected");
                        } else if (cellType == CellType.EMPTY && !isOrthogonallyAdjacent(pendingFirstClick, pos)) {
                            cell.getStyleClass().addAll("board-cell-skill-target", "board-cell-double-place-second");
                        } else if (cellType == CellType.EMPTY) {
                            cell.getStyleClass().add("board-cell-double-place-blocked");
                        }
                    }
                }

                // Apply Pending First Click style
                if (pendingFirstClick != null && pendingFirstClick.equals(pos)) {
                    cell.getStyleClass().add("board-cell-selected");
                }
            }
        }
        updateHighlights();
    }

    private String renderCell(CellType cellType) {
        return switch (cellType) {
            case EMPTY -> "";
            case X -> "X";
            case O -> "O";
            case SEALED -> "■";
        };
    }

    private boolean hasAnyEmptyCell(GameState state) {
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                if (state.getCell(new Position(row, col)) == CellType.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAnyValidDoublePlaceSecond(GameState state, Position first) {
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                Position second = new Position(row, col);
                if (second.equals(first)) {
                    continue;
                }
                if (state.getCell(second) != CellType.EMPTY) {
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
