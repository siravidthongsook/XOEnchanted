package game.ui;

import game.model.GameState;
import game.model.Position;
import game.model.Line;
import game.model.PlayerId;
import game.model.PlayerState;
import game.engine.GameEngine;
import game.engine.GameEventListener;
import game.ui.view.ActionBarView;
import game.ui.view.BoardView;
import game.ui.view.HudView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameApplication extends Application implements GameEventListener {
    private static final int BASE_CELL_SIZE = 100;
    private static final int MAX_ENERGY = 5;
    private static final int DOUBLE_PLACE_COST = 4;
    private static final int OVERHEAT_PENALTY_DROP_TO = 3;
    private static final int OVERHEAT_MAX_CONSECUTIVE_TURNS = 2;
    private static final String FONT_URL = "https://raw.githubusercontent.com/google/fonts/main/ofl/silkscreen/Silkscreen-Regular.ttf";
    private static final int PLACE_SFX_VARIANTS = 4;
    private static final int CROSS_SFX_VARIANTS = 3;

    public static void main(String[] args) {
        Application.launch(GameApplication.class, args);
    }

    private GameUiController controller;
    private BoardView boardView;
    private HudView hudView;
    private ActionBarView actionBarView;
    private final List<AudioClip> placeSfxClips = new ArrayList<>();
    private final List<AudioClip> crossSfxClips = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        int cellSize = resolveCellSize(GameState.BOARD_SIZE);
        int legendOffsetY = resolveLegendOffsetY(GameState.BOARD_SIZE, cellSize);

        // Load custom font from web
        Font.loadFont(FONT_URL, 12);
        loadPlaceSoundEffects();
        loadCrossSoundEffects();
        warmUpSoundEffects();
        this.controller = new GameUiController();
        this.controller.addEventListener(this);
        this.boardView = new BoardView(GameState.BOARD_SIZE, cellSize, this::handleCellClick);
        this.hudView = new HudView();
        this.actionBarView = new ActionBarView(
                this::onHowToPlayRequested,
                this::onSealSelected,
                this::onMoveSelected,
                this::onDisruptSelected,
                this::onDoublePlaceSelected,
                this::onResetRequested
        );

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        VBox centerColumn = new VBox(2, hudView.logNode(), boardView.node());
        centerColumn.setAlignment(Pos.CENTER);

        StackPane centerStack = new StackPane(centerColumn, hudView.legendNode());
        centerStack.setAlignment(Pos.CENTER);
        StackPane.setAlignment(hudView.legendNode(), Pos.CENTER);
        hudView.legendNode().setTranslateY(legendOffsetY);

        root.setCenter(centerStack);
        root.setRight(hudView.node());
        root.setBottom(actionBarView.node());

        refreshView();

        Scene scene = new Scene(root, 1100, 800);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("XO Enchanted " + GameState.BOARD_SIZE + "x" + GameState.BOARD_SIZE);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleCellClick(int row, int col) {
        try {
            controller.onCellClicked(row, col);
            if (controller.mode() == SkillMode.PLACE) {
                hudView.showPlacedAt(row, col);
            }
        } catch (RuntimeException ex) {
            hudView.showStatus(ex.getMessage());
        }
        refreshView();
    }

    private void onSealSelected() {
        toggleSkillMode(SkillMode.SEAL, "Seal: select an empty cell (cost 2 energy)");
        refreshView();
    }

    private void onMoveSelected() {
        toggleSkillMode(SkillMode.MOVE, "Move: select one of your pieces, then any highlighted empty cell (cost 1 energy)");
        refreshView();
    }

    private void onDisruptSelected() {
        toggleSkillMode(SkillMode.DISRUPT, "Disrupt: select a highlighted opponent piece (cost 3 energy)");
        refreshView();
    }

    private void onDoublePlaceSelected() {
        toggleSkillMode(SkillMode.DOUBLE_PLACE, "Double Place: select first empty cell, then a non-adjacent empty cell (cost 4 energy)");
        refreshView();
    }

    private void toggleSkillMode(SkillMode skillMode, String selectedStatus) {
        if (controller.mode() == skillMode) {
            controller.setMode(SkillMode.PLACE);
            hudView.showStatus("Standard Placement");
            return;
        }
        controller.setMode(skillMode);
        hudView.showStatus(selectedStatus);
    }

    private void onResetRequested() {
        controller = new GameUiController();
        controller.addEventListener(this);
        hudView.showStatus("New game started");
        refreshView();
    }

    private void onHowToPlayRequested() {
        Alert helpDialog = new Alert(Alert.AlertType.INFORMATION);
        helpDialog.setTitle("How To Play");
        helpDialog.setHeaderText(null);
        helpDialog.setGraphic(null);
        String helpText =
                "XO ENCHANTED " + GameState.BOARD_SIZE + "X" + GameState.BOARD_SIZE + "\n\n"
                        + "Objective\n"
                        + "- Reach " + GameEngine.getWinScore() + " points before your opponent.\n"
                        + "- If nobody reaches " + GameEngine.getWinScore() + " by turn " + GameEngine.getMaxTurns() + ", higher score wins.\n"
                        + "- Same score at turn " + GameEngine.getMaxTurns() + " triggers sudden death.\n\n"
                        + "Core Rules\n"
                        + "- Place one piece on an empty cell on your turn.\n"
                        + "- A line is 4 in a row: horizontal, vertical, or diagonal.\n"
                        + "- Each cleared line gives 1 point.\n"
                        + "- If your move creates multiple lines, you must choose one to clear.\n"
                        + "- Clearing a line gives the opponent Priority on their next turn (+2 start energy).\n\n"
                        + "Energy\n"
                        + "- Normal turn start: +1 energy.\n"
                        + "- Priority turn start: +2 energy instead.\n"
                        + "- Spend energy to use skills listed below.\n\n"
                        + "Frozen Rule\n"
                        + "- A piece that stays inactive for 3 of its owner's turns becomes Frozen.\n"
                        + "- Frozen lasts for 1 owner turn, then the piece thaws automatically.\n"
                        + "- Frozen pieces cannot be moved and are immune to Disrupt.\n\n"
                        + "Skills\n"
                        + "- Seal (2): block an empty cell for one opponent placement.\n"
                        + "- Move (1): move one of your pieces to an empty cell.\n"
                        + "- Disrupt (3): remove one opponent piece.\n"
                        + "- Double Place (4): place two pieces in one turn on non-adjacent cells.\n\n"
                        + "Basic Advice\n"
                        + "- Build two threats at once (row + column, or row + diagonal).\n"
                        + "- Save energy to swing tempo with Move or Double Place.\n"
                        + "- Use Seal to deny key fourth cells, not random empty cells.\n"
                        + "- If the opponent has Priority next turn, defend first.";

        Label helpLabel = new Label(helpText);
        helpLabel.setWrapText(true);
        helpLabel.setMaxWidth(Double.MAX_VALUE);
        helpLabel.getStyleClass().add("help-tutorial-label");

        ScrollPane helpScrollPane = new ScrollPane(helpLabel);
        helpScrollPane.setFitToWidth(true);
        helpScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        helpScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        helpScrollPane.setPrefViewportHeight(420);
        helpScrollPane.getStyleClass().add("help-scroll-pane");
        helpDialog.getDialogPane().setContent(helpScrollPane);

        helpDialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        helpDialog.getDialogPane().getStyleClass().add("root");
        helpDialog.getDialogPane().setPrefWidth(760);
        helpDialog.getDialogPane().setPrefHeight(520);
        helpDialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        helpDialog.getButtonTypes().setAll(ButtonType.OK);
        helpDialog.showAndWait();
    }

    private void refreshView() {
        GameState state = controller.state();

        boardView.render(state, controller.mode(), controller.getPendingFirstClick());
        hudView.render(state, controller.mode());
        PlayerState currentPlayerState = state.getPlayerState(state.getCurrentPlayer());
        int currentEnergy = currentPlayerState.getEnergy();
        boolean canUseDoublePlace = projectedEnergyAtTurnStart(state, currentPlayerState) >= DOUBLE_PLACE_COST;
        actionBarView.render(controller.mode(), state.isGameOver(), currentEnergy, canUseDoublePlace);

        if (state.isGameOver() || state.isSuddenDeath()) {
            hudView.hideLegend();
            return;
        }

        if (controller.mode() == SkillMode.SEAL) {
            hudView.showStatus("Seal: select an empty cell (cost 2 energy)");
            hudView.showSealLegend();
        } else if (controller.mode() == SkillMode.MOVE) {
            PlayerId currentPlayer = state.getCurrentPlayer();
            if (controller.getPendingFirstClick() == null) {
                hudView.showStatus("Move: select your " + currentPlayer + " piece");
            } else {
                hudView.showStatus("Move: select any highlighted empty cell");
            }
            hudView.showMoveLegend();
        } else if (controller.mode() == SkillMode.DISRUPT) {
            hudView.showStatus("Disrupt: select a highlighted opponent piece");
            hudView.showDisruptLegend();
        } else if (controller.mode() == SkillMode.DOUBLE_PLACE) {
            if (controller.getPendingFirstClick() == null) {
                hudView.showStatus("Double Place: select first empty cell");
            } else {
                hudView.showStatus("Double Place: select highlighted second empty cell (not orthogonally adjacent)");
            }
            hudView.showDoublePlaceLegend();
        } else if (controller.mode() == SkillMode.PLACE) {
            hudView.showStatus("Standard Placement");
            hudView.hideLegend();
        }
    }

    @Override
    public void onPiecePlaced(Position position, PlayerId playerId) {
        playRandomPlaceSoundEffect();
        boardView.animatePiecePlacement(position, playerId);
    }

    private void loadPlaceSoundEffects() {
        for (int i = 1; i <= PLACE_SFX_VARIANTS; i++) {
            String path = "/SFX/Place/Place" + i + ".wav";
            java.net.URL resource = getClass().getResource(path);
            if (resource != null) {
                placeSfxClips.add(new AudioClip(resource.toExternalForm()));
            }
        }
    }

    private void playRandomPlaceSoundEffect() {
        if (placeSfxClips.isEmpty()) {
            return;
        }
        AudioClip clip = placeSfxClips.get(random.nextInt(placeSfxClips.size()));
        clip.play();
    }

    private void warmUpSoundEffects() {
        warmUpClipList(placeSfxClips);
        warmUpClipList(crossSfxClips);
    }

    private void warmUpClipList(List<AudioClip> clips) {
        for (AudioClip clip : clips) {
            clip.play(0.0);
            clip.stop();
        }
    }

    private void loadCrossSoundEffects() {
        for (int i = 1; i <= CROSS_SFX_VARIANTS; i++) {
            String path = "/SFX/Cross/cross" + i + ".wav";
            java.net.URL resource = getClass().getResource(path);
            if (resource != null) {
                crossSfxClips.add(new AudioClip(resource.toExternalForm()));
            }
        }
    }

    private void playRandomCrossSoundEffect() {
        if (crossSfxClips.isEmpty()) {
            return;
        }
        AudioClip clip = crossSfxClips.get(random.nextInt(crossSfxClips.size()));
        clip.play();
    }

    @Override
    public void onLineCleared(Line line) {
        playRandomCrossSoundEffect();
        boardView.animateLineClear(line);
    }

    @Override
    public void onLineSelectionRequired(List<Line> lines) {
        hudView.showStatus("Multiple lines detected! Select a line to clear.");
    }

    @Override
    public void onTurnEnded(PlayerId nextPlayer) {
        // 1. Reset the interaction mode back to standard placement for the new player
        controller.setMode(SkillMode.PLACE);

        // 2. Announce the turn change in the HUD
        hudView.showStatus("Turn ended! It is now " + nextPlayer + "'s turn.");

        // 3. Refresh the UI to update energy counters, active player highlights, and expired visual effects
        refreshView();
    }

    private int projectedEnergyAtTurnStart(GameState state, PlayerState playerState) {
        int projected = playerState.getEnergy();
        if (state.isTurnStarted()) {
            return projected;
        }

        if (playerState.getOverheatCounter() >= OVERHEAT_MAX_CONSECUTIVE_TURNS && projected > OVERHEAT_PENALTY_DROP_TO) {
            projected = OVERHEAT_PENALTY_DROP_TO;
        }

        if (playerState.isSkipNextTurnEnergyGain()) {
            return projected;
        }

        int gain = playerState.isPriorityTurn() ? 2 : 1;
        return Math.min(MAX_ENERGY, projected + gain);
    }

    private static int resolveCellSize(int boardSize) {
        if (boardSize <= 4) {
            return BASE_CELL_SIZE;
        }
        if (boardSize == 5) {
            return 84;
        }
        return Math.max(64, 420 / boardSize);
    }

    private static int resolveLegendOffsetY(int boardSize, int cellSize) {
        return (boardSize * cellSize) / 2 + 48;
    }
}
