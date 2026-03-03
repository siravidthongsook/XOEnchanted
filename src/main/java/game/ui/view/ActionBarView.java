package game.ui.view;

import game.ui.SkillMode;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActionBarView {
    private final HBox node;
    private final Button sealButton;
    private final Button shiftButton;
    private final Button disruptButton;
    private final Button doublePlaceButton;
    private final Button resetGameButton;

    public ActionBarView(
            Runnable onSeal,
            Runnable onShift,
            Runnable onDisrupt,
            Runnable onDoublePlace,
            Runnable onReset
    ) {
        this.sealButton = new Button("Seal");
        this.shiftButton = new Button("Shift");
        this.disruptButton = new Button("Disrupt");
        this.doublePlaceButton = new Button("Double Place");
        this.resetGameButton = new Button("Reset Game");

        sealButton.setOnAction(event -> onSeal.run());
        shiftButton.setOnAction(event -> onShift.run());
        disruptButton.setOnAction(event -> onDisrupt.run());
        doublePlaceButton.setOnAction(event -> onDoublePlace.run());
        resetGameButton.setOnAction(event -> onReset.run());

        this.node = new HBox(12, sealButton, shiftButton, disruptButton, doublePlaceButton, resetGameButton);
        this.node.getStyleClass().add("action-bar");
    }

    public void render(SkillMode mode, boolean gameOver) {
        setSkillSelected(sealButton, mode == SkillMode.SEAL);
        setSkillSelected(shiftButton, mode == SkillMode.SHIFT);
        setSkillSelected(disruptButton, mode == SkillMode.DISRUPT);
        setSkillSelected(doublePlaceButton, mode == SkillMode.DOUBLE_PLACE);

        setButtonVisible(sealButton, !gameOver);
        setButtonVisible(shiftButton, !gameOver);
        setButtonVisible(disruptButton, !gameOver);
        setButtonVisible(doublePlaceButton, !gameOver);
        setButtonVisible(resetGameButton, true);
    }

    private void setSkillSelected(Button button, boolean selected) {
        button.getStyleClass().remove("skill-selected");
        if (selected) {
            button.getStyleClass().add("skill-selected");
        }
    }

    private void setButtonVisible(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }

    public HBox node() {
        return node;
    }
}
