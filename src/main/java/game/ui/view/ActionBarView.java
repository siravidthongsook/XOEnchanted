package game.ui.view;

import game.ui.SkillMode;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActionBarView {
    private static final int SEAL_COST = 2;
    private static final int SHIFT_COST = 2;
    private static final int DISRUPT_COST = 3;
    private static final int DOUBLE_PLACE_COST = 4;

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
        this.sealButton = new Button("Seal (" + SEAL_COST + ")");
        this.shiftButton = new Button("Shift (" + SHIFT_COST + ")");
        this.disruptButton = new Button("Disrupt (" + DISRUPT_COST + ")");
        this.doublePlaceButton = new Button("Double Place (" + DOUBLE_PLACE_COST + ")");
        this.resetGameButton = new Button("Reset Game");

        sealButton.setOnAction(event -> onSeal.run());
        shiftButton.setOnAction(event -> onShift.run());
        disruptButton.setOnAction(event -> onDisrupt.run());
        doublePlaceButton.setOnAction(event -> onDoublePlace.run());
        resetGameButton.setOnAction(event -> onReset.run());

        this.node = new HBox(12, sealButton, shiftButton, disruptButton, doublePlaceButton, resetGameButton);
        this.node.getStyleClass().add("action-bar");
    }

    public void render(SkillMode mode, boolean gameOver, int currentEnergy) {
        setSkillSelected(sealButton, mode == SkillMode.SEAL);
        setSkillSelected(shiftButton, mode == SkillMode.SHIFT);
        setSkillSelected(disruptButton, mode == SkillMode.DISRUPT);
        setSkillSelected(doublePlaceButton, mode == SkillMode.DOUBLE_PLACE);

        sealButton.setDisable(currentEnergy < SEAL_COST);
        shiftButton.setDisable(currentEnergy < SHIFT_COST);
        disruptButton.setDisable(currentEnergy < DISRUPT_COST);
        doublePlaceButton.setDisable(currentEnergy < DOUBLE_PLACE_COST);

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
