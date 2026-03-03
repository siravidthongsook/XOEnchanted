package game.engine;

import game.model.CellType;
import game.model.GameState;
import game.model.Line;
import game.model.PlayerId;
import game.model.Position;

import java.util.ArrayList;
import java.util.List;

public final class LineDetector {
    private static final List<Line> ALL_LINES = precomputeLines();

    private LineDetector() {
    }

    public static List<Line> detectLines(GameState state, PlayerId playerId) {
        CellType targetCell = CellType.fromPlayer(playerId);
        List<Line> result = new ArrayList<>();

        for (Line line : ALL_LINES) {
            boolean allMatch = line.positions().stream()
                    .allMatch(position -> state.getCell(position) == targetCell);
            if (allMatch) {
                result.add(line);
            }
        }

        return result;
    }

    public static void resolveSelectedLine(GameState state, Line line) {
        for (Position position : line.positions()) {
            state.clearCell(position);
        }
    }

    static List<Line> allLines() {
        return ALL_LINES;
    }

    private static List<Line> precomputeLines() {
        List<Line> lines = new ArrayList<>();
        int n = GameState.BOARD_SIZE;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col <= n - 4; col++) {
                lines.add(new Line(
                        new Position(row, col),
                        new Position(row, col + 1),
                        new Position(row, col + 2),
                        new Position(row, col + 3)
                ));
            }
        }

        for (int col = 0; col < n; col++) {
            for (int row = 0; row <= n - 4; row++) {
                lines.add(new Line(
                        new Position(row, col),
                        new Position(row + 1, col),
                        new Position(row + 2, col),
                        new Position(row + 3, col)
                ));
            }
        }

        for (int row = 0; row <= n - 4; row++) {
            for (int col = 0; col <= n - 4; col++) {
                lines.add(new Line(
                        new Position(row, col),
                        new Position(row + 1, col + 1),
                        new Position(row + 2, col + 2),
                        new Position(row + 3, col + 3)
                ));
            }
        }

        for (int row = 0; row <= n - 4; row++) {
            for (int col = 3; col < n; col++) {
                lines.add(new Line(
                        new Position(row, col),
                        new Position(row + 1, col - 1),
                        new Position(row + 2, col - 2),
                        new Position(row + 3, col - 3)
                ));
            }
        }

        return List.copyOf(lines);
    }
}
