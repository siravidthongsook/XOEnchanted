package game.ui;

import game.model.GameState;
import game.model.Position;
import game.model.Line;
import game.model.PlayerId;
import game.engine.GameEventListener;
import game.ui.view.ActionBarView;
import game.ui.view.BoardView;
import game.ui.view.HudView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class GameApplication extends Application implements GameEventListener {
    private static final int CELL_SIZE = 100;
    private static final String FONT_URL = "https://raw.githubusercontent.com/google/fonts/main/ofl/silkscreen/Silkscreen-Regular.ttf";

    public static void main(String[] args) {
        Application.launch(GameApplication.class, args);
    }

    private GameUiController controller;
    private BoardView boardView;
    private HudView hudView;

    @Override
    public void start(Stage stage) {
        // Load custom font from web
        Font.loadFont(FONT_URL, 12);
        this.controller = new GameUiController();
        this.controller.addEventListener(this);
        this.boardView = new BoardView(GameState.BOARD_SIZE, CELL_SIZE, this::handleCellClick);
        this.hudView = new HudView();
        ActionBarView actionBarView = new ActionBarView(
                this::onSealSelected,
                this::onShiftSelected,
                this::onDisruptSelected,
                this::onDoublePlaceSelected,
                this::onResetRequested
        );

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        root.setCenter(boardView.node());
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
            hudView.showStatus("Status: " + ex.getMessage());
        }
        refreshView();
    }

    private void onSealSelected() {
        controller.setMode(SkillMode.SEAL);
        hudView.showStatus("Status: TODO implement Seal target flow");
        refreshView();
    }

    private void onShiftSelected() {
        controller.setMode(SkillMode.SHIFT);
        hudView.showStatus("Status: TODO implement Shift two-click flow");
        refreshView();
    }

    private void onDisruptSelected() {
        controller.setMode(SkillMode.DISRUPT);
        hudView.showStatus("Status: TODO implement Disrupt target flow");
        refreshView();
    }

    private void onDoublePlaceSelected() {
        controller.setMode(SkillMode.DOUBLE_PLACE);
        hudView.showStatus("Status: TODO implement Double Place two-target flow");
        refreshView();
    }

    private void onResetRequested() {
        controller = new GameUiController();
        controller.addEventListener(this);
        hudView.showStatus("Status: new game started");
        refreshView();
    }

    private void refreshView() {
        GameState state = controller.state();

        boardView.render(state);
        hudView.render(state, controller.mode());
    }

    @Override
    public void onPiecePlaced(Position position, PlayerId playerId) {
        boardView.animatePiecePlacement(position, playerId);
    }

    @Override
    public void onLineCleared(Line line) {
        boardView.animateLineClear(line);
    }

    @Override
    public void onLineSelectionRequired(List<Line> lines) {
        hudView.showStatus("Multiple lines detected! Select a line to clear.");
    }
}
