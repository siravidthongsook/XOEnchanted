package game.model;

import java.util.List;

/**
 * Represents a four-in-a-row line of board positions.
 */
public record Line(Position first, Position second, Position third, Position fourth) {
    public List<Position> positions() {
        return List.of(first, second, third, fourth);
    }
}
