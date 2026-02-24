package game.ui.view;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;
import game.model.PlayerId;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import java.util.function.BiConsumer;

public class BoardView {
    private final StackPane[][] cellContainers;
    private final Label[][] pieceLabels;
    private final GridPane grid;
    private final Pane overlayPane;
    private final StackPane node;
    private final int cellSize;
    private java.util.List<game.model.Line> currentPendingLines;
    private Position lastHoveredPos;
    private final java.util.Set<Position> animatingPositions = new java.util.HashSet<>();

    public BoardView(int boardSize, int cellSize, BiConsumer<Integer, Integer> onCellClicked) {
        this.cellSize = cellSize;
        this.cellContainers = new StackPane[boardSize][boardSize];
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
                pieceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                pieceLabel.setAlignment(javafx.geometry.Pos.CENTER);
                
                cell.getChildren().add(pieceLabel);
                StackPane.setAlignment(pieceLabel, javafx.geometry.Pos.CENTER);
                
                cell.setOnMouseClicked(event -> onCellClicked.accept(targetRow, targetCol));
                cell.setOnMouseEntered(event -> handleMouseEntered(targetRow, targetCol));
                cell.setOnMouseExited(event -> handleMouseExited());

                cellContainers[row][col] = cell;
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
        Label label = pieceLabels[position.row()][position.col()];
        label.setText(playerId.toString());
        
        ScaleTransition st = new ScaleTransition(Duration.millis(200), label);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    public void animateLineClear(game.model.Line line) {
        Position startPos = line.first();
        Position endPos = line.third();
        
        animatingPositions.addAll(line.positions());

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
            javafx.animation.ParallelTransition fadeAll = new javafx.animation.ParallelTransition();

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

    public void render(GameState state) {
        this.currentPendingLines = state.isWaitingForLineSelection() ? state.getPendingLines() : null;
        
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                CellType cellType = state.getCell(pos);

                if (cellType == CellType.EMPTY && animatingPositions.contains(pos)) continue;
                
                Label label = pieceLabels[row][col];
                label.setText(renderCell(cellType));
                
                // Set piece color based on player
                label.getStyleClass().removeAll("hud-value-x", "hud-value-o");
                if (cellType == CellType.X) label.getStyleClass().add("hud-value-x");
                if (cellType == CellType.O) label.getStyleClass().add("hud-value-o");

                cellContainers[row][col].setDisable(state.isGameOver());
                
                cellContainers[row][col].getStyleClass().removeAll("board-cell-sealed");
                if (cellType == CellType.SEALED) {
                    cellContainers[row][col].getStyleClass().add("board-cell-sealed");
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
}
