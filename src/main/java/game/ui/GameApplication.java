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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class GameApplication extends Application implements GameEventListener {
    private static final int CELL_SIZE = 93;
    private static final String FONT_URL = "https://raw.githubusercontent.com/google/fonts/main/ofl/silkscreen/Silkscreen-Regular.ttf";

    public static void main(String[] args) {
        Application.launch(GameApplication.class, args);
    }

    private GameUiController controller;
    private BoardView boardView;
    private HudView hudView;
    private ActionBarView actionBarView;

    // Popup Overlays
    private VBox infoOverlay;
    private VBox gameOverOverlay;
    private Label winnerLabel;

    @Override
    public void start(Stage stage) {
        Font.loadFont(FONT_URL, 12);
        this.controller = new GameUiController();
        this.controller.addEventListener(this);
        this.boardView = new BoardView(GameState.BOARD_SIZE, CELL_SIZE, this::handleCellClick);
        this.hudView = new HudView();

        this.actionBarView = new ActionBarView(
                this::onSealSelected,
                this::onShiftSelected,
                this::onDisruptSelected,
                this::onDoublePlaceSelected,
                this::onResetRequested,
                this::showInfoOverlay // ส่ง Action เข้าไป
        );

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20, 40, 40, 40));
        mainLayout.setTop(hudView.node());
        mainLayout.setCenter(boardView.node());
        mainLayout.setRight(actionBarView.node());

        // สร้าง Popup Overlays
        createInfoOverlay();
        createGameOverOverlay();

        // ครอบทุกอย่างด้วย StackPane
        StackPane root = new StackPane(mainLayout, infoOverlay, gameOverOverlay);

        refreshView();

        Scene scene = new Scene(root, 1100, 800);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("XO Enchanted 4x4");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void createInfoOverlay() {
        // กล่องเนื้อหาด้านใน
        VBox popupBox = new VBox(15);
        popupBox.getStyleClass().add("overlay-container");

        Label title = new Label("--- SKILL INFO ---");
        title.getStyleClass().add("overlay-title");

        Label text1 = new Label("SEAL (2E): ปิดช่องว่าง 1 ช่อง (วาง/ขยับเข้าไม่ได้) มีผล 1 เทิร์น");
        Label text2 = new Label("SHIFT (2E): เลื่อนหมากตัวเอง 1 ตัว ไปช่องว่างติดกัน (บน/ล่าง/ซ้าย/ขวา)");
        Label text3 = new Label("DISRUPT (3E): ลบหมากศัตรู 1 ตัว (Penalty: เทิร์นหน้าคุณจะไม่ได้ Energy)");
        Label text4 = new Label("DOUBLE PLACE (4E): วาง 2 ช่องพร้อมกัน (ห้ามวางติดกันแบบแนบชิด)");

        text1.getStyleClass().add("overlay-text");
        text2.getStyleClass().add("overlay-text");
        text3.getStyleClass().add("overlay-text");
        text4.getStyleClass().add("overlay-text");

        Button closeBtn = new Button("CLOSE");
        closeBtn.setOnAction(e -> infoOverlay.setVisible(false));

        popupBox.getChildren().addAll(title, text1, text2, text3, text4, closeBtn);

        // สร้างกรอบฉากหลังสีดำโปร่งแสงมาครอบเต็มจอ เพื่อกันการคลิกทะลุ
        infoOverlay = new VBox(popupBox);
        infoOverlay.setAlignment(Pos.CENTER);
        infoOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        infoOverlay.setVisible(false);
    }

    private void createGameOverOverlay() {
        // กล่องเนื้อหาด้านใน
        VBox popupBox = new VBox(20);
        popupBox.getStyleClass().add("overlay-container");

        Label title = new Label("GAME OVER!");
        title.getStyleClass().add("overlay-title");

        winnerLabel = new Label("WINNER: ");
        winnerLabel.getStyleClass().add("overlay-title");
        winnerLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: yellow;");

        Button restartBtn = new Button("PLAY AGAIN");
        restartBtn.setOnAction(e -> {
            gameOverOverlay.setVisible(false);
            onResetRequested();
        });

        popupBox.getChildren().addAll(title, winnerLabel, restartBtn);

        // สร้างกรอบฉากหลังสีดำโปร่งแสงมาครอบเต็มจอ เพื่อบล็อกเกมทั้งหมดตอนจบ
        gameOverOverlay = new VBox(popupBox);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        gameOverOverlay.setVisible(false);
    }

    private void showInfoOverlay() {
        infoOverlay.setVisible(true);
    }

    private void handleCellClick(int row, int col) {
        try {
            controller.onCellClicked(row, col);
        } catch (RuntimeException ex) {
            hudView.showStatus(ex.getMessage());
        }
        refreshView();
    }

    private void onSealSelected() { controller.setMode(SkillMode.SEAL); refreshView(); }
    private void onShiftSelected() { controller.setMode(SkillMode.SHIFT); refreshView(); }
    private void onDisruptSelected() { controller.setMode(SkillMode.DISRUPT); refreshView(); }
    private void onDoublePlaceSelected() { controller.setMode(SkillMode.DOUBLE_PLACE); refreshView(); }

    private void onResetRequested() {
        controller = new GameUiController();
        controller.addEventListener(this);
        gameOverOverlay.setVisible(false);
        refreshView();
    }

    private void refreshView() {
        GameState state = controller.state();
        boardView.render(state);
        hudView.render(state, controller.mode());

        // ตรวจสอบสถานะจบเกม
        if (state.isGameOver()) {
            gameOverOverlay.setVisible(true);
            winnerLabel.setText("WINNER: PLAYER " + state.getWinner());
        }
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