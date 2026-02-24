package game.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    public static final int BOARD_SIZE = 4;

    private final CellType[][] board;
    private final Map<PlayerId, PlayerState> playerStates;
    private final Map<Position, Integer> sealExpirations; // เก็บข้อมูลเทิร์นที่ช่องจะปลดล็อก

    private PlayerId currentPlayer;
    private int totalTurnCount;
    private boolean suddenDeath;
    private boolean gameOver;
    private PlayerId winner;
    private boolean waitingForLineSelection;
    private java.util.List<Line> pendingLines;

    public GameState() {
        this.board = new CellType[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = CellType.EMPTY;
            }
        }
        this.playerStates = new EnumMap<>(PlayerId.class);
        this.playerStates.put(PlayerId.X, new PlayerState());
        this.playerStates.put(PlayerId.O, new PlayerState());
        this.sealExpirations = new HashMap<>(); // เริ่มต้น Map

        this.currentPlayer = PlayerId.X;
        this.totalTurnCount = 0;
        this.suddenDeath = false;
        this.gameOver = false;
        this.winner = null;
    }

    public PlayerId getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTotalTurnCount() {
        return totalTurnCount;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public PlayerId getWinner() {
        return winner;
    }

    public boolean isWaitingForLineSelection() {
        return waitingForLineSelection;
    }

    public void setWaitingForLineSelection(boolean waitingForLineSelection) {
        this.waitingForLineSelection = waitingForLineSelection;
    }

    public java.util.List<Line> getPendingLines() {
        return pendingLines;
    }

    public void setPendingLines(java.util.List<Line> pendingLines) {
        this.pendingLines = pendingLines;
    }

    public PlayerState getPlayerState(PlayerId playerId) {
        return playerStates.get(playerId);
    }

    public CellType getCell(Position position) {
        validateInsideBoard(position);
        return board[position.row()][position.col()];
    }

    public void placePiece(Position position, PlayerId playerId) {
        validateInsideBoard(position);
        if (board[position.row()][position.col()] != CellType.EMPTY) {
            throw new IllegalStateException("Cell is not empty");
        }
        board[position.row()][position.col()] = CellType.fromPlayer(playerId);
    }

    public void clearCell(Position position) {
        validateInsideBoard(position);
        board[position.row()][position.col()] = CellType.EMPTY;
    }

    // --- ระบบจัดการ Seal ---
    public Map<Position, Integer> getSealExpirations() {
        return sealExpirations;
    }

    public void sealCell(Position position, int expiryTurn) {
        validateInsideBoard(position);
        board[position.row()][position.col()] = CellType.SEALED;
        sealExpirations.put(position, expiryTurn);
    }

    public void unsealCell(Position position) {
        validateInsideBoard(position);
        if (board[position.row()][position.col()] == CellType.SEALED) {
            board[position.row()][position.col()] = CellType.EMPTY;
        }
        sealExpirations.remove(position);
    }
    // ----------------------

    public boolean isInsideBoard(Position position) {
        return position.row() >= 0
                && position.row() < BOARD_SIZE
                && position.col() >= 0
                && position.col() < BOARD_SIZE;
    }

    public void incrementTurn() {
        totalTurnCount++;
    }

    public void switchCurrentPlayer() {
        currentPlayer = currentPlayer.opponent();
    }

    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public void setWinner(PlayerId winner) {
        this.winner = winner;
        this.gameOver = winner != null;
    }

    private void validateInsideBoard(Position position) {
        if (!isInsideBoard(position)) {
            throw new IllegalArgumentException("Position is out of board bounds");
        }
    }
}