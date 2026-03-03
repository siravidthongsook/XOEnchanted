package game.model;

import java.util.List;

public record Line(Position first, Position second, Position third, Position fourth) {
    public List<Position> positions() {
        return List.of(first, second, third, fourth);
    }
}
