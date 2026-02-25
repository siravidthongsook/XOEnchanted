package game.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActionBarView {
    private final HBox node;

    public ActionBarView(
            Runnable onSeal,
            Runnable onShift,
            Runnable onDisrupt,
            Runnable onDoublePlace,
            Runnable onReset,
            Runnable onResetSkill
    ) {
        Button sealButton = new Button("Seal");
        Button shiftButton = new Button("Shift");
        Button disruptButton = new Button("Disrupt");
        Button doublePlaceButton = new Button("Double Place");
        Button resetSkill = new Button("Reset Skill");
        Button resetGameButton = new Button("Reset Game");

        sealButton.setOnAction(event -> onSeal.run());
        shiftButton.setOnAction(event -> onShift.run());
        disruptButton.setOnAction(event -> onDisrupt.run());
        doublePlaceButton.setOnAction(event -> onDoublePlace.run());
        resetGameButton.setOnAction(event -> onReset.run());
        resetSkill.setOnAction(event -> onResetSkill.run());

        this.node = new HBox(12, sealButton, shiftButton, disruptButton, doublePlaceButton, resetSkill, resetGameButton);
        this.node.getStyleClass().add("action-bar");
    }

    public HBox node() {
        return node;
    }
}
