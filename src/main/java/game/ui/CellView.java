package game.ui;

import game.model.Cell;
import game.model.PlayerId;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Visual representation of a single cell on the board.
 */
public class CellView extends StackPane {
    
    private final Label contentLabel;
    private Cell currentCell;

    public CellView() {
        this.contentLabel = new Label();
        this.currentCell = new Cell.Empty();
        
        getChildren().add(contentLabel);
        getStyleClass().add("cell-view");
        
        // TODO: Set preferred size, styling
        // setPrefSize(80, 80);
    }

    /**
     * Update this cell view to display the given cell state.
     */
    public void updateCell(Cell cell) {
        // TODO: Update visual representation based on cell type
        // - Empty: clear label, default style
        // - Piece: show "X" or "O", add player style, show frozen indicator
        // - Sealed: show "SEAL" icon, add sealed style
        this.currentCell = cell;
    }

    /**
     * Get the current cell state.
     */
    public Cell getCurrentCell() {
        return currentCell;
    }

    /**
     * Apply visual effect for piece placement.
     */
    public void playPlaceAnimation() {
        // TODO: Add scale or fade animation when piece is placed
    }

    /**
     * Apply visual effect for piece removal.
     */
    public void playRemoveAnimation() {
        // TODO: Add fade out animation when piece is removed
    }
}
