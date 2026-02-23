package game.ui;

import game.model.Board;
import game.model.Position;
import javafx.scene.layout.GridPane;

/**
 * Visual representation of the 4x4 game board.
 * Displays cells and handles click interactions.
 */
public class BoardView extends GridPane {
    
    private final CellView[][] cellViews;

    public BoardView() {
        this.cellViews = new CellView[Position.BOARD_SIZE][Position.BOARD_SIZE];
        initializeCells();
    }

    /**
     * Initialize all cell views in the grid.
     */
    private void initializeCells() {
        // TODO: Create 16 CellView objects and add to GridPane
        // For each position (row, col):
        //   - Create CellView
        //   - Add to GridPane at (col, row)
        //   - Set up click handler
    }

    /**
     * Update the board view to match the given board state.
     */
    public void updateBoard(Board board) {
        // TODO: Update each CellView with corresponding cell from board
        // For each position, call cellViews[row][col].updateCell(board.getCell(pos))
    }

    /**
     * Highlight a specific cell (for selection or scoring line display).
     */
    public void highlightCell(Position pos, String styleClass) {
        // TODO: Add CSS style class to cell for visual feedback
    }

    /**
     * Clear all cell highlights.
     */
    public void clearHighlights() {
        // TODO: Remove highlight styles from all cells
    }

    /**
     * Set click handler for cell selection.
     */
    public void setCellClickHandler(CellClickHandler handler) {
        // TODO: Set handler for each cell
    }

    @FunctionalInterface
    public interface CellClickHandler {
        void onCellClicked(Position position);
    }
}
