package game.engine;

import game.engine.action.SealAction;
import game.model.CellType;
import game.model.GameState;
import game.model.Line;
import game.model.PlayerId;
import game.model.PlayerState;
import game.model.Position;

import java.util.List;

public class GameEngine {
    private static final int WIN_SCORE = 3;
    private static final int MAX_TURNS = 24;

    private final GameState gameState;
    private GameEventListener eventListener;
    private final SealLifecycleService sealLifecycleService;

    public GameEngine() {
        this.gameState = new GameState();
        this.sealLifecycleService = new SealLifecycleService();
    }

    public void setEventListener(GameEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void playPlacementTurn(Position position) {
        ensureGameIsActive();
        if (gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Waiting for line selection");
        }

        // เช็กก่อนให้พลังงาน เพื่อป้องกันผู้เล่นกดช่องผิดแล้วได้ Energy ฟรี
        if (gameState.getCell(position) != CellType.EMPTY) {
            throw new IllegalArgumentException("Target cell is not empty");
        }

        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        startTurnGainEnergy(actorState);
        gameState.placePiece(position, actor);
        if (eventListener != null) {
            eventListener.onPiecePlaced(position, actor);
        }

        boolean scored = resolveScoring(actor, actorState);

        if (!gameState.isWaitingForLineSelection()) {
            endTurn(actor, scored);
        }
    }

    public void useSealSkill(Position position) {
        ensureGameIsActive();
        if (gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Waiting for line selection");
        }

        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        // เช็กว่าถ้าบวก Energy ต้นเทิร์นแล้ว จะพอใช้สกิลไหม
        int projectedGain = actorState.isPriorityTurn() ? 2 : 1;
        int projectedEnergy = Math.min(actorState.getEnergy() + projectedGain, 10);

        if (gameState.getCell(position) != CellType.EMPTY) {
            throw new IllegalArgumentException("Target cell must be empty to seal");
        }
        if (projectedEnergy < 2) {
            throw new IllegalStateException("Not enough energy to use Seal (Costs 2)");
        }

        startTurnGainEnergy(actorState); // ยืนยันเริ่มเทิร์นและรับพลังงาน

        SealAction action = new SealAction(position);
        action.apply(gameState);

        endTurn(actor, false); // สกิลไม่ทำให้เกิดการเรียงเส้นโดยตรง
    }

    public void selectLine(Line line) {
        if (!gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Not waiting for line selection");
        }
        if (!gameState.getPendingLines().contains(line)) {
            throw new IllegalArgumentException("Line not in pending lines");
        }

        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        applyScoring(actor, actorState, line);
        gameState.setWaitingForLineSelection(false);
        gameState.setPendingLines(null);

        endTurn(actor, true);
    }

    public void useShiftSkill(Position from, Position to) {
        ensureGameIsActive();
        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        int projectedGain = actorState.isPriorityTurn() ? 2 : 1;
        if (actorState.getEnergy() + projectedGain < 2) {
            throw new IllegalStateException("Not enough energy for Shift");
        }

        startTurnGainEnergy(actorState);

        game.engine.action.ShiftAction action = new game.engine.action.ShiftAction(from, to);
        if (!action.validate(gameState)) {
            throw new IllegalArgumentException("Invalid Shift move");
        }
        action.apply(gameState);

        // ย้ายแล้วอาจจะเรียงกันได้ ต้องตรวจคะแนน
        boolean scored = resolveScoring(actor, actorState);
        if (!gameState.isWaitingForLineSelection()) endTurn(actor, scored);
    }

    public void useDisruptSkill(Position position) {
        ensureGameIsActive();
        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        int projectedGain = actorState.isPriorityTurn() ? 2 : 1;
        if (actorState.getEnergy() + projectedGain < 3) {
            throw new IllegalStateException("Not enough energy for Disrupt");
        }

        startTurnGainEnergy(actorState);

        game.engine.action.DisruptAction action = new game.engine.action.DisruptAction(position);
        if (!action.validate(gameState)) {
            throw new IllegalArgumentException("Invalid Disrupt target");
        }
        action.apply(gameState);

        // Disrupt แค่ทำลายหมาก ไม่ได้ทำให้เรียงเส้น จึงไม่ต้องตรวจคะแนน
        endTurn(actor, false);
    }

    public void useDoublePlaceSkill(Position first, Position second) {
        ensureGameIsActive();
        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);

        int projectedGain = actorState.isPriorityTurn() ? 2 : 1;
        if (actorState.getEnergy() + projectedGain < 4) {
            throw new IllegalStateException("Not enough energy for Double Place");
        }

        startTurnGainEnergy(actorState);

        game.engine.action.DoublePlaceAction action = new game.engine.action.DoublePlaceAction(first, second);
        if (!action.validate(gameState)) {
            throw new IllegalArgumentException("Invalid Double Place (Cannot be adjacent)");
        }
        action.apply(gameState);

        // วางหมาก 2 ตัว อาจเกิดเส้นได้
        boolean scored = resolveScoring(actor, actorState);
        if (!gameState.isWaitingForLineSelection()) endTurn(actor, scored);
    }

    private void startTurnGainEnergy(PlayerState actorState) {
        int gain = actorState.consumeTurnStartGain();
        actorState.gainEnergy(gain);
    }

    private boolean resolveScoring(PlayerId actor, PlayerState actorState) {
        List<Line> lines = LineDetector.detectLines(gameState, actor);
        if (lines.isEmpty()) {
            return false;
        }

        if (lines.size() > 1) {
            gameState.setWaitingForLineSelection(true);
            gameState.setPendingLines(lines);
            if (eventListener != null) {
                eventListener.onLineSelectionRequired(lines);
            }
            return false;
        }

        applyScoring(actor, actorState, lines.get(0));
        return true;
    }

    private void applyScoring(PlayerId actor, PlayerState actorState, Line selectedLine) {
        actorState.addScore(1);
        LineDetector.resolveSelectedLine(gameState, selectedLine);
        if (eventListener != null) {
            eventListener.onLineCleared(selectedLine);
        }
        actorState.gainEnergy(1);
        gameState.getPlayerState(actor.opponent()).setPriorityTurn(true);
    }

    private void endTurn(PlayerId actor, boolean scored) {
        // อัปเดตการหมดอายุของสกิล Seal
        sealLifecycleService.updateAtEndOfTurn(gameState);

        if (gameState.isSuddenDeath() && scored) {
            gameState.setWinner(actor);
            return;
        }

        if (gameState.getPlayerState(actor).getScore() >= WIN_SCORE) {
            gameState.setWinner(actor);
            return;
        }

        gameState.incrementTurn();
        if (gameState.getTotalTurnCount() >= MAX_TURNS && !gameState.isGameOver()) {
            resolveTurnLimitResult();
        }

        if (!gameState.isGameOver()) {
            gameState.switchCurrentPlayer();
        }
    }

    private void resolveTurnLimitResult() {
        int xScore = gameState.getPlayerState(PlayerId.X).getScore();
        int oScore = gameState.getPlayerState(PlayerId.O).getScore();

        if (xScore > oScore) {
            gameState.setWinner(PlayerId.X);
            return;
        }
        if (oScore > xScore) {
            gameState.setWinner(PlayerId.O);
            return;
        }

        gameState.setSuddenDeath(true);
    }

    private void ensureGameIsActive() {
        if (gameState.isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }
    }
}