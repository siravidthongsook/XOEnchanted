package game.ui.view;

import game.model.CellType;
import game.model.GameState;
import game.model.Position;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.function.BiConsumer;

import javafx.scene.layout.StackPane;

import java.util.function.BiConsumer;

public class BoardView {
    private final Button[][] cells;
    private final GridPane grid;
    private final StackPane node;

    public BoardView(int boardSize, int cellSize, BiConsumer<Integer, Integer> onCellClicked) {
        this.cells = new Button[boardSize][boardSize];
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

                Button cell = new Button();
                cell.getStyleClass().add("board-cell");
                
                // Style borders to form a grid (only right and bottom borders)
                int right = (col == boardSize - 1) ? 0 : 2;
                int bottom = (row == boardSize - 1) ? 0 : 2;
                cell.setStyle("-fx-border-width: 0 " + right + " " + bottom + " 0;");
                
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setOnAction(event -> onCellClicked.accept(targetRow, targetCol));

                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }
    }

    public StackPane node() {
        return node;
    }

    public void render(GameState state) {
        for (int row = 0; row < GameState.BOARD_SIZE; row++) {
            for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                CellType cellType = state.getCell(new Position(row, col));
                cells[row][col].setText(renderCell(cellType));
                cells[row][col].setDisable(state.isGameOver());
                
                cells[row][col].getStyleClass().remove("board-cell-sealed");
                if (cellType == CellType.SEALED) {
                    cells[row][col].getStyleClass().add("board-cell-sealed");
                }
            }
        }
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
