package game.model;

import java.util.List;

/**
 * Represents a scoring line of 3 contiguous cells.
 */
public record ScoringLine(
    Position pos1,
    Position pos2,
    Position pos3,
    LineType type
) {
    /**
     * Get all positions in this line as a list.
     * @return list containing all 3 positions
     */
    public List<Position> getPositions() {
        return List.of(pos1, pos2, pos3);
    }
}
