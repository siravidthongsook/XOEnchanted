package game.engine;

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
    private final SealLifecycleService sealLifecycleService = new SealLifecycleService();
    private final FrozenRuleService frozenRuleService = new FrozenRuleService();
    private final OverheatRuleService overheatRuleService = new OverheatRuleService();

    public GameEngine() {
        this.gameState = new GameState();
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

        gameState.setTurnEnded(false);

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

    public void useSealSkill(Position position) {
        ensureGameIsActive();
        if (gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Cannot use skills while waiting for line selection");
        }

        // 1. Create the action
        game.engine.action.SealAction sealAction = new game.engine.action.SealAction(position);

        // 2. Validate the action
        if (!sealAction.validate(gameState)) {
            throw new IllegalArgumentException("Invalid Seal action: Check energy and target cell.");
        }

        // 3. Apply the action (spends energy, marks board, tracks expiry)
        sealAction.apply(gameState);

        // Optional: If you want your UI to play an animation when a skill is used,
        // you could add a method to your GameEventListener here, like:
        // if (eventListener != null) {
        //     eventListener.onSealSkillUsed(position, gameState.getCurrentPlayer());
        // }

        /* * Note on Turn Ending:
         * If using a skill costs your entire turn, you should call:
         * endTurn(gameState.getCurrentPlayer(), false);
         * * If skills are "free actions" used before placing a piece,
         * just leave it as is. The turn will end when they call playPlacementTurn().
         */
    }

        public void useShiftSkill(Position from, Position to) {
            ensureGameIsActive();
            if (gameState.isWaitingForLineSelection()) {
                throw new IllegalStateException("Cannot use skills while waiting for line selection");
            }

            game.engine.action.ShiftAction shiftAction = new game.engine.action.ShiftAction(from, to);

            if (!shiftAction.validate(gameState)) {
                throw new IllegalArgumentException("Invalid Shift action.");
            }

            shiftAction.apply(gameState);

            // Optional Event Listener trigger
            // if (eventListener != null) {
            //     eventListener.onPieceShifted(from, to, gameState.getCurrentPlayer());
            // }
        }


    public void useDisruptSkill(Position position) {
        ensureGameIsActive();
        if (gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Cannot use skills while waiting for line selection");
        }

        game.engine.action.DisruptAction disruptAction = new game.engine.action.DisruptAction(position);

        if (!disruptAction.validate(gameState)) {
            throw new IllegalArgumentException("Invalid Disrupt action.");
        }

        disruptAction.apply(gameState);
    }

    public void useDoublePlaceSkill(Position first, Position second) {
        ensureGameIsActive();
        if (gameState.isWaitingForLineSelection()) {
            throw new IllegalStateException("Cannot use skills while waiting for line selection");
        }

        // 1. Start turn logic (gain energy, etc.)
        PlayerId actor = gameState.getCurrentPlayer();
        PlayerState actorState = gameState.getPlayerState(actor);
        startTurnGainEnergy(actorState);

        // 2. Execute the Action
        game.engine.action.DoublePlaceAction doublePlaceAction =
                new game.engine.action.DoublePlaceAction(first, second);

        if (!doublePlaceAction.validate(gameState)) {
            throw new IllegalArgumentException("Invalid Double Place action.");
        }

        doublePlaceAction.apply(gameState);

        // Optional: Trigger events for UI
        if (eventListener != null) {
            eventListener.onPiecePlaced(first, actor);
            eventListener.onPiecePlaced(second, actor);
        }

        // 3. Resolve Scoring (since pieces were added to the board)
        boolean scored = resolveScoring(actor, actorState);

        // 4. End the turn (unless we are waiting for the player to select a line to clear)
        if (!gameState.isWaitingForLineSelection()) {
            endTurn(actor, scored);
        }
    }

    private void startTurnGainEnergy(PlayerState actorState) {
        if (gameState.isTurnStarted()) return;
        gameState.setTurnStarted(true);

        overheatRuleService.applyPreGainAdjustment(gameState, gameState.getCurrentPlayer());

        if (actorState.isSkipNextTurnEnergyGain()) {
            actorState.setSkipNextTurnEnergyGain(false); // Clear the penalty for the future
            return; // Skip the energy gain entirely this turn
        }

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
            return false; // Turn hasn't "finished" scoring yet
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
        if (gameState.isSuddenDeath() && scored) {
            gameState.setWinner(actor);
            return;
        }

        if (gameState.getPlayerState(actor).getScore() >= WIN_SCORE) {
            gameState.setWinner(actor);
            return;
        }

        sealLifecycleService.updateAtEndOfTurn(gameState);
        frozenRuleService.updateAtEndOfTurn(gameState);
        overheatRuleService.updateTrackerAtEndOfTurn(gameState, actor);

        gameState.incrementTurn();
        gameState.setTurnStarted(false);

        if (gameState.getTotalTurnCount() >= MAX_TURNS && !gameState.isGameOver()) {
            resolveTurnLimitResult();
        }

        if (!gameState.isGameOver()) {
            gameState.switchCurrentPlayer();
        }

        gameState.setTurnEnded(true);

        if (eventListener != null) {
            eventListener.onTurnEnded(gameState.getCurrentPlayer());
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
