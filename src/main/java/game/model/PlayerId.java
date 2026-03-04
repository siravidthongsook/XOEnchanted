package game.model;

/**
 * Identifies a player and provides access to the opponent.
 */
public enum PlayerId {
    X,
    O;

    public PlayerId opponent() {
        return this == X ? O : X;
    }
}
