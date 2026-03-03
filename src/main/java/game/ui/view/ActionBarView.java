package game.ui.view;

import game.ui.SkillMode;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActionBarView {
    private static final int SEAL_COST = 2;
    private static final int MOVE_COST = 1;
    private static final int DISRUPT_COST = 3;
    private static final int DOUBLE_PLACE_COST = 4;

    private final HBox node;
    private final Button howToPlayButton;
    private final Button sealButton;
    private final Button moveButton;
    private final Button disruptButton;
    private final Button doublePlaceButton;
    private final Button resetGameButton;

    public ActionBarView(
            Runnable onHowToPlay,
            Runnable onSeal,
            Runnable onMove,
            Runnable onDisrupt,
            Runnable onDoublePlace,
            Runnable onReset
    ) {
        this.howToPlayButton = new Button("?");
        this.howToPlayButton.getStyleClass().add("help-button");
        this.sealButton = new Button("Seal (" + SEAL_COST + ")");
        this.moveButton = new Button("Move (" + MOVE_COST + ")");
        this.disruptButton = new Button("Disrupt (" + DISRUPT_COST + ")");
        this.doublePlaceButton = new Button("Double Place (" + DOUBLE_PLACE_COST + ")");
        this.resetGameButton = new Button("Reset Game");

        howToPlayButton.setOnAction(event -> onHowToPlay.run());
        sealButton.setOnAction(event -> onSeal.run());
        moveButton.setOnAction(event -> onMove.run());
        disruptButton.setOnAction(event -> onDisrupt.run());
        doublePlaceButton.setOnAction(event -> onDoublePlace.run());
        resetGameButton.setOnAction(event -> onReset.run());

        this.node = new HBox(12, howToPlayButton, sealButton, moveButton, disruptButton, doublePlaceButton, resetGameButton);
        this.node.getStyleClass().add("action-bar");
    }

    public void render(SkillMode mode, boolean gameOver, int currentEnergy, boolean canUseDoublePlace) {
        setSkillSelected(sealButton, mode == SkillMode.SEAL);
        setSkillSelected(moveButton, mode == SkillMode.MOVE);
        setSkillSelected(disruptButton, mode == SkillMode.DISRUPT);
        setSkillSelected(doublePlaceButton, mode == SkillMode.DOUBLE_PLACE);

        sealButton.setDisable(currentEnergy < SEAL_COST);
        moveButton.setDisable(currentEnergy < MOVE_COST);
        disruptButton.setDisable(currentEnergy < DISRUPT_COST);
        doublePlaceButton.setDisable(!canUseDoublePlace);

        setButtonVisible(sealButton, !gameOver);
        setButtonVisible(moveButton, !gameOver);
        setButtonVisible(disruptButton, !gameOver);
        setButtonVisible(doublePlaceButton, !gameOver);
        setButtonVisible(howToPlayButton, true);
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
