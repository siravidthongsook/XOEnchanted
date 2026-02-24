package game.engine;

import game.model.Line;
import game.model.PlayerId;
import game.model.Position;

public interface GameEventListener {
    void onPiecePlaced(Position position, PlayerId playerId);
    void onLineCleared(Line line);
    void onLineSelectionRequired(java.util.List<Line> lines);
}
