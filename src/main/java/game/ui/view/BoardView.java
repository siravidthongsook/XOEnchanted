package game.ui.view;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;
import game.model.Line;
import game.model.PlayerId;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.util.function.BiConsumer;

public class BoardView {
    private final StackPane[][] cellContainers;
    private final Label[][] pieceLabels;
    private final GridPane grid;
    private final StackPane node;
    private java.util.List<Line> currentPendingLines;
    private Position lastHoveredPos;

    public BoardView(int boardSize, int cellSize, BiConsumer<Integer, Integer> onCellClicked) {
        this.cellContainers = new StackPane[boardSize][boardSize];
        this.pieceLabels = new Label[boardSize][boardSize];
        this.grid = new GridPane();
        this.grid.setAlignment(javafx.geometry.Pos.CENTER);
        this.grid.setHgap(0); 
        this.grid.setVgap(0);
        
        StackPane innerContainer = new StackPane(grid);
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
                
                int right = (col == boardSize - 1) ? 0 : 2;
                int bottom = (row == boardSize - 1) ? 0 : 2;
                cell.setStyle("-fx-border-width: 0 " + right + " " + bottom + " 0;");
                
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setOnMouseClicked(event -> onCellClicked.accept(targetRow, targetCol));
                
                cell.setOnMouseEntered(event -> handleMouseEntered(targetRow, targetCol));
                cell.setOnMouseExited(event -> handleMouseExited());

                Label pieceLabel = new Label();
                pieceLabel.getStyleClass().add("piece-label");
                cell.getChildren().add(pieceLabel);

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
        for (Line line : currentPendingLines) {
            for (Position p : line.positions()) {
                cellContainers[p.row()][p.col()].getStyleClass().add("board-cell-highlight");
            }
        }

        // Apply yellow highlight for the hovered line
        if (lastHoveredPos != null) {
            for (Line line : currentPendingLines) {
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

    public void animateLineClear(Line line) {
        SequentialTransition seq = new SequentialTransition();
        for (Position pos : line.positions()) {
            Label label = pieceLabels[pos.row()][pos.col()];
            
            FadeTransition ft = new FadeTransition(Duration.millis(300), label);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                label.setText("");
                label.setOpacity(1.0);
            });
            seq.getChildren().add(ft);
        }
        seq.play();
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
                pieceLabels[row][col].setText(renderCell(cellType));
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
