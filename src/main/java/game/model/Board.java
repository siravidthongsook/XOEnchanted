package game.model;

import java.util.List;

/**
 * Interface for the game board.
 * Provides methods to query and manipulate board state.
 */
public interface Board {
    
    /**
     * Get the cell at the specified position.
     * @param pos the position to query
     * @return the cell at that position
     * @throws IllegalArgumentException if position is invalid
     */
    Cell getCell(Position pos);

    /**
     * Get all positions containing pieces owned by the specified player.
     * @param player the player whose pieces to find
     * @return list of positions containing player's pieces
     */
    List<Position> getPieces(PlayerId player);

    /**
     * Check if the specified position is valid and empty.
     * @param pos the position to check
     * @return true if position is within bounds and contains an empty cell
     */
    boolean isEmpty(Position pos);

    /**
     * Check if the specified position is sealed.
     * @param pos the position to check
     * @return true if position contains a sealed cell
     */
    boolean isSealed(Position pos);

    /**
     * Detect all scoring lines (3-in-a-row) for the specified player.
     * @param player the player to check for scoring lines
     * @return list of all scoring lines found (may be empty)
     */
    List<ScoringLine> detectScoringLines(PlayerId player);

    /**
     * Create a new board with the cell at the specified position updated.
     * @param pos the position to update
     * @param newCell the new cell value
     * @return new board instance with the update applied
     */
    Board withCell(Position pos, Cell newCell);

    /**
     * Create a new board with cells at specified positions removed (set to empty).
     * @param positions the positions to clear
     * @return new board instance with cells removed
     */
    Board withCellsRemoved(List<Position> positions);

    /**
     * Create a deep copy of this board.
     * @return new board instance with same state
     */
    Board copy();
}
