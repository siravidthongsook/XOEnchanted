package game.engine;

import game.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Board interface using a 2D array.
 */
public class BoardImpl implements Board {
    
    private final Cell[][] cells;

    /**
     * Create an empty board.
     */
    public BoardImpl() {
        this.cells = new Cell[Position.BOARD_SIZE][Position.BOARD_SIZE];
        // Initialize all cells as empty
        for (int row = 0; row < Position.BOARD_SIZE; row++) {
            for (int col = 0; col < Position.BOARD_SIZE; col++) {
                cells[row][col] = new Cell.Empty();
            }
        }
    }

    /**
     * Create a board from existing cell array (for copying).
     */
    private BoardImpl(Cell[][] cells) {
        this.cells = cells;
    }

    @Override
    public Cell getCell(Position pos) {
        // TODO: Implement cell retrieval
        // Validate position first, then return cell
        return null;
    }

    @Override
    public List<Position> getPieces(PlayerId player) {
        // TODO: Implement piece finding
        // Scan entire board for cells containing pieces owned by player
        return new ArrayList<>();
    }

    @Override
    public boolean isEmpty(Position pos) {
        // TODO: Implement empty check
        // Return true if position is valid and cell is Empty
        return false;
    }

    @Override
    public boolean isSealed(Position pos) {
        // TODO: Implement sealed check
        // Return true if position contains Sealed cell
        return false;
    }

    @Override
    public List<ScoringLine> detectScoringLines(PlayerId player) {
        // TODO: Implement scoring line detection (CRITICAL!)
        // Must detect all 24 possible lines:
        // - 8 horizontal (2 per row, 4 rows)
        // - 8 vertical (2 per column, 4 columns)
        // - 8 diagonal (4 main diagonals + 4 anti-diagonals)
        // For each potential 3-cell line, check if all 3 are pieces owned by player
        return new ArrayList<>();
    }

    @Override
    public Board withCell(Position pos, Cell newCell) {
        // TODO: Implement immutable cell update
        // 1. Copy current cells array
        // 2. Update the specific position
        // 3. Return new BoardImpl with updated array
        return null;
    }

    @Override
    public Board withCellsRemoved(List<Position> positions) {
        // TODO: Implement cell removal
        // 1. Copy current cells array
        // 2. Set all positions in list to Empty
        // 3. Return new BoardImpl
        return null;
    }

    @Override
    public Board copy() {
        // TODO: Implement deep copy
        // Create new 2D array and copy all cells
        return null;
    }

    /**
     * Check all horizontal lines for 3-in-a-row.
     */
    private List<ScoringLine> checkHorizontalLines(PlayerId player) {
        // TODO: Check all horizontal 3-cell sequences
        // For each row, check positions: (r,0-1-2), (r,1-2-3)
        return new ArrayList<>();
    }

    /**
     * Check all vertical lines for 3-in-a-row.
     */
    private List<ScoringLine> checkVerticalLines(PlayerId player) {
        // TODO: Check all vertical 3-cell sequences
        // For each column, check positions: (0-1-2,c), (1-2-3,c)
        return new ArrayList<>();
    }

    /**
     * Check all diagonal lines for 3-in-a-row.
     */
    private List<ScoringLine> checkDiagonalLines(PlayerId player) {
        // TODO: Check all diagonal 3-cell sequences
        // Main diagonals (top-left to bottom-right)
        // Anti-diagonals (top-right to bottom-left)
        // There are 8 total diagonal lines on a 4x4 grid
        return new ArrayList<>();
    }
}
