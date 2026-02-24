package game.ui.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ActionBarView {
    private final VBox node;

    public ActionBarView(
            Runnable onSeal,
            Runnable onShift,
            Runnable onDisrupt,
            Runnable onDoublePlace,
            Runnable onReset,
            Runnable onInfo // เพิ่ม Action สำหรับปุ่ม INFO
    ) {
        Label titleLabel = new Label("XO ENCHANTED");
        titleLabel.getStyleClass().add("header-label");

        // ระบุ Energy ลงบนปุ่มให้ชัดเจน
        Button infoButton = new Button("SKILL INFO (?)");
        Button sealButton = new Button("SEAL (2E)");
        Button shiftButton = new Button("SHIFT (2E)");
        Button disruptButton = new Button("DISRUPT (3E)");
        Button doublePlaceButton = new Button("DOUBLE (4E)");
        Button resetButton = new Button("RESET GAME");

        infoButton.setOnAction(event -> onInfo.run());
        sealButton.setOnAction(event -> onSeal.run());
        shiftButton.setOnAction(event -> onShift.run());
        disruptButton.setOnAction(event -> onDisrupt.run());
        doublePlaceButton.setOnAction(event -> onDoublePlace.run());
        resetButton.setOnAction(event -> onReset.run());

        // ไฮไลต์ปุ่ม INFO ให้ดูแตกต่างเล็กน้อย
        infoButton.setStyle("-fx-border-color: #39FF14; -fx-text-fill: #39FF14;");

        VBox buttonBox = new VBox(20, infoButton, sealButton, shiftButton, disruptButton, doublePlaceButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getStyleClass().add("action-button-box");

        this.node = new VBox(40, titleLabel, buttonBox);
        this.node.getStyleClass().add("right-panel-container");
        this.node.setAlignment(Pos.TOP_CENTER);
    }

    public VBox node() {
        return node;
    }
}