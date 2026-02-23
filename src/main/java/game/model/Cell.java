package game.model;

/**
 * Represents the state of a single cell on the board.
 * A cell can be empty, contain a piece, or be sealed.
 */
public sealed interface Cell permits Cell.Empty, Cell.Piece, Cell.Sealed {
    
    /**
     * An empty cell that can be played on.
     */
    record Empty() implements Cell {}
    
    /**
     * A cell containing a player's piece.
     * @param owner the player who owns this piece
     * @param lastMovedTurn the turn number when this piece was last moved (for frozen logic)
     * @param frozen whether this piece is currently frozen
     */
    record Piece(PlayerId owner, int lastMovedTurn, boolean frozen) implements Cell {}
    
    /**
     * A cell that has been sealed and cannot be played on.
     * @param expiresAtTurn the turn number when this seal expires
     */
    record Sealed(int expiresAtTurn) implements Cell {}
}
