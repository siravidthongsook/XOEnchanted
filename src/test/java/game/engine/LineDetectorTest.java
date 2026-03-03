package game.engine;

import game.model.CellType;
import game.model.GameState;
import game.model.Line;
import game.model.PlayerId;
import game.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineDetectorTest {

    @Test
    void detectLinesFindsHorizontalVerticalAndDiagonalLinesForPlayer() {
        GameState state = new GameState();

        state.setCell(new Position(0, 0), CellType.X);
        state.setCell(new Position(0, 1), CellType.X);
        state.setCell(new Position(0, 2), CellType.X);

        state.setCell(new Position(1, 3), CellType.X);
        state.setCell(new Position(2, 3), CellType.X);
        state.setCell(new Position(3, 3), CellType.X);

        state.setCell(new Position(1, 0), CellType.X);
        state.setCell(new Position(2, 1), CellType.X);
        state.setCell(new Position(3, 2), CellType.X);

        List<Line> lines = LineDetector.detectLines(state, PlayerId.X);

        assertEquals(3, lines.size());
        assertTrue(lines.contains(new Line(new Position(0, 0), new Position(0, 1), new Position(0, 2))));
        assertTrue(lines.contains(new Line(new Position(1, 3), new Position(2, 3), new Position(3, 3))));
        assertTrue(lines.contains(new Line(new Position(1, 0), new Position(2, 1), new Position(3, 2))));
    }

    @Test
    void detectLinesOnlyReturnsLinesForRequestedPlayer() {
        GameState state = new GameState();

        state.setCell(new Position(0, 0), CellType.X);
        state.setCell(new Position(0, 1), CellType.X);
        state.setCell(new Position(0, 2), CellType.X);

        state.setCell(new Position(2, 0), CellType.O);
        state.setCell(new Position(2, 1), CellType.O);
        state.setCell(new Position(2, 2), CellType.O);

        assertEquals(1, LineDetector.detectLines(state, PlayerId.X).size());
        assertEquals(1, LineDetector.detectLines(state, PlayerId.O).size());
    }

    @Test
    void resolveSelectedLineClearsAllCellsInTheLine() {
        GameState state = new GameState();
        Line line = new Line(new Position(1, 0), new Position(1, 1), new Position(1, 2));

        state.setCell(new Position(1, 0), CellType.O);
        state.setCell(new Position(1, 1), CellType.O);
        state.setCell(new Position(1, 2), CellType.O);
        state.setCell(new Position(0, 0), CellType.X);

        LineDetector.resolveSelectedLine(state, line);

        assertEquals(CellType.EMPTY, state.getCell(new Position(1, 0)));
        assertEquals(CellType.EMPTY, state.getCell(new Position(1, 1)));
        assertEquals(CellType.EMPTY, state.getCell(new Position(1, 2)));
        assertEquals(CellType.X, state.getCell(new Position(0, 0)));
    }
}
