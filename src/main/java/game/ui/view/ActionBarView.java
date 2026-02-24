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
            Runnable onReset
    ) {
        Button sealButton = new Button("Seal");
        Button shiftButton = new Button("Shift");
        Button disruptButton = new Button("Disrupt");
        Button doublePlaceButton = new Button("Double Place");
        Button resetButton = new Button("Reset");

        sealButton.setOnAction(event -> onSeal.run());
        shiftButton.setOnAction(event -> onShift.run());
        disruptButton.setOnAction(event -> onDisrupt.run());
        doublePlaceButton.setOnAction(event -> onDoublePlace.run());
        resetButton.setOnAction(event -> onReset.run());

        this.node = new HBox(12, sealButton, shiftButton, disruptButton, doublePlaceButton, resetButton);
        this.node.getStyleClass().add("action-bar");
    }

    public HBox node() {
        return node;
    }
}
