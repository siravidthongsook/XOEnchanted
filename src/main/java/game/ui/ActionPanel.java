package game.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Panel containing action buttons for player skills.
 */
public class ActionPanel extends HBox {
    
    private final Button placeButton;
    private final Button shiftButton;
    private final Button disruptButton;
    private final Button sealButton;
    private final Button doublePlaceButton;
    private final Button undoButton;

    public ActionPanel() {
        this.placeButton = new Button("Place (Free)");
        this.shiftButton = new Button("Shift (2)");
        this.disruptButton = new Button("Disrupt (3)");
        this.sealButton = new Button("Seal (2)");
        this.doublePlaceButton = new Button("Double Place (4)");
        this.undoButton = new Button("Undo");
        
        getChildren().addAll(
            placeButton, shiftButton, disruptButton,
            sealButton, doublePlaceButton, undoButton
        );
        
        getStyleClass().add("action-panel");
        setSpacing(10);
        
        // TODO: Set up button handlers
    }

    /**
     * Set button click handlers.
     */
    public void setActionHandlers(ActionHandler handler) {
        // TODO: Wire button clicks to handler methods
        // placeButton.setOnAction(e -> handler.onPlaceAction());
    }

    /**
     * Enable/disable buttons based on available energy and game state.
     */
    public void updateButtonStates(int currentEnergy) {
        // TODO: Enable buttons only if player has enough energy
        // shiftButton.setDisable(currentEnergy < 2);
        // disruptButton.setDisable(currentEnergy < 3);
        // etc.
    }

    public interface ActionHandler {
        void onPlaceAction();
        void onShiftAction();
        void onDisruptAction();
        void onSealAction();
        void onDoublePlaceAction();
        void onUndoAction();
    }
}
