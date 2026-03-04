package game.model;

/**
 * Enumerates the possible contents of a board cell.
 */
public enum CellType {
    EMPTY,
    X,
    O,
    SEALED;

    public static CellType fromPlayer(PlayerId playerId) {
        return playerId == PlayerId.X ? X : O;
    }
}
