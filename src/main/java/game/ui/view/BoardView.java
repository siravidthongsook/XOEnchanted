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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
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

        // ตอนนี้เรากำหนด background image ไว้ที่ main-board-container ผ่าน CSS
        this.node = new StackPane(innerContainer);
        this.node.getStyleClass().add("main-board-container");
        this.node.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int targetRow = row;
                int targetCol = col;

                StackPane cell = new StackPane();
                cell.getStyleClass().add("board-cell");

                // นำการเซ็ตขอบแบบ Hardcode ออก เพื่อใช้พื้นหลังที่ไม่มีขอบรบกวนภาพ
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
        for (int row = 0; row < cellContainers.length; row++) {
            for (int col = 0; col < cellContainers[row].length; col++) {
                cellContainers[row][col].getStyleClass().removeAll("board-cell-highlight", "board-cell-line-hover");
            }
        }

        if (currentPendingLines == null || currentPendingLines.isEmpty()) return;

        for (game.model.Line line : currentPendingLines) {
            for (Position p : line.positions()) {
                cellContainers[p.row()][p.col()].getStyleClass().add("board-cell-highlight");
            }
        }

        if (lastHoveredPos != null) {
            for (game.model.Line line : currentPendingLines) {
                if (line.positions().contains(lastHoveredPos)) {
                    for (Position p : line.positions()) {
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

        // ลบข้อความ และเพิ่มคลาสรูปภาพแทน
        label.setText("");
        label.getStyleClass().removeAll("piece-x", "piece-o");
        label.getStyleClass().add(playerId == PlayerId.X ? "piece-x" : "piece-o");

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

        double startX = startPos.col() * cellSize + cellSize / 2.0;
        double startY = startPos.row() * cellSize + cellSize / 2.0;
        double endX = endPos.col() * cellSize + cellSize / 2.0;
        double endY = endPos.row() * cellSize + cellSize / 2.0;

        javafx.scene.shape.Line strikeLine = new javafx.scene.shape.Line(startX, startY, startX, startY);
        strikeLine.getStyleClass().add("strike-line");
        overlayPane.getChildren().add(strikeLine);

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
            SequentialTransition seq = new SequentialTransition();
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
                    // อย่าลืมลบคลาสรูปภาพออกเมื่อโดนเคลียร์
                    label.getStyleClass().removeAll("piece-x", "piece-o");
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

                // รีเซ็ตคลาสและข้อความ
                label.getStyleClass().removeAll("piece-x", "piece-o", "hud-value-x", "hud-value-o");
                label.setText("");

                if (cellType == CellType.X) {
                    label.getStyleClass().add("piece-x");
                } else if (cellType == CellType.O) {
                    label.getStyleClass().add("piece-o");
                } else if (cellType == CellType.SEALED) {
                    label.setText("■"); // ให้คงสัญลักษณ์บล็อกไว้ หรือจะใส่รูปภาพบล็อกในอนาคตก็ได้
                    label.setStyle("-fx-text-fill: #555555;");
                }

                cellContainers[row][col].setDisable(state.isGameOver());

                cellContainers[row][col].getStyleClass().removeAll("board-cell-sealed");
                if (cellType == CellType.SEALED) {
                    cellContainers[row][col].getStyleClass().add("board-cell-sealed");
                }
            }
        }
        updateHighlights();
    }
}