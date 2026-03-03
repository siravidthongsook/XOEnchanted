package game.ui;

import game.model.GameState;
import game.model.Position;
import game.model.Line;
import game.model.PlayerId;
import game.model.PlayerState;
import game.engine.GameEventListener;
import game.ui.view.ActionBarView;
import game.ui.view.BoardView;
import game.ui.view.HudView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
    private static final int CELL_SIZE = 100;
    private static final int LEGEND_OFFSET_Y = 250;
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
        // Load custom font from web
        Font.loadFont(FONT_URL, 12);
        loadPlaceSoundEffects();
        loadCrossSoundEffects();
        this.controller = new GameUiController();
        this.controller.addEventListener(this);
        this.boardView = new BoardView(GameState.BOARD_SIZE, CELL_SIZE, this::handleCellClick);
        this.hudView = new HudView();
        this.actionBarView = new ActionBarView(
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
        hudView.legendNode().setTranslateY(LEGEND_OFFSET_Y);

        root.setCenter(centerStack);
        root.setRight(hudView.node());
        root.setBottom(actionBarView.node());

        refreshView();

        Scene scene = new Scene(root, 1100, 800);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("XO Enchanted 4x4");
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

    private void refreshView() {
        GameState state = controller.state();

        boardView.render(state, controller.mode(), controller.getPendingFirstClick());
        hudView.render(state, controller.mode());
        PlayerState currentPlayerState = state.getPlayerState(state.getCurrentPlayer());
        int currentEnergy = currentPlayerState.getEnergy();
        boolean canUseDoublePlace = projectedEnergyAtTurnStart(state, currentPlayerState) >= DOUBLE_PLACE_COST;
        actionBarView.render(controller.mode(), state.isGameOver(), currentEnergy, canUseDoublePlace);

        if (state.isGameOver() || state.isSuddenDeath()) {
            hudView.showLegend(false);
            return;
        }

        if (controller.mode() == SkillMode.SEAL) {
            hudView.showStatus("Seal: select an empty cell (cost 2 energy)");
            hudView.showLegend(false);
        } else if (controller.mode() == SkillMode.MOVE) {
            PlayerId currentPlayer = state.getCurrentPlayer();
            if (controller.getPendingFirstClick() == null) {
                hudView.showStatus("Move: select your " + currentPlayer + " piece");
            } else {
                hudView.showStatus("Move: select any highlighted empty cell");
            }
            hudView.showLegend(false);
        } else if (controller.mode() == SkillMode.DISRUPT) {
            hudView.showStatus("Disrupt: select a highlighted opponent piece");
            hudView.showLegend(true);
        } else if (controller.mode() == SkillMode.DOUBLE_PLACE) {
            if (controller.getPendingFirstClick() == null) {
                hudView.showStatus("Double Place: select first empty cell");
            } else {
                hudView.showStatus("Double Place: select highlighted second empty cell (not orthogonally adjacent)");
            }
            hudView.showLegend(false);
        } else if (controller.mode() == SkillMode.PLACE) {
            hudView.showStatus("Standard Placement");
            hudView.showLegend(false);
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
}
