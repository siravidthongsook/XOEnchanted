package game.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the main menu screen.
 * Displays: New Game, Load Game, Rules, Settings, Exit buttons.
 */
public class MainMenuController {
    
    private final Stage stage;
    private Scene scene;

    public MainMenuController(Stage stage) {
        this.stage = stage;
        initializeScene();
    }

    /**
     * Initialize the main menu scene with all UI components.
     */
    private void initializeScene() {
        // TODO: Implement main menu UI
        // Create:
        // - Title label
        // - New Game button -> navigate to game screen
        // - Load Game button -> show file chooser
        // - Rules button -> show rules dialog
        // - Settings button -> show settings dialog
        // - Exit button -> close application
        // Use VBox or BorderPane for layout
    }

    /**
     * Handle New Game button click.
     */
    private void handleNewGame() {
        // TODO: Navigate to game screen
        // Create GameController and switch scene
    }

    /**
     * Handle Rules button click.
     */
    private void handleRules() {
        // TODO: Show rules dialog
        // Display game rules in a dialog or new scene
    }

    /**
     * Handle Settings button click.
     */
    private void handleSettings() {
        // TODO: Show settings dialog
        // Allow adjusting volume, theme, etc.
    }

    /**
     * Handle Exit button click.
     */
    private void handleExit() {
        // TODO: Close application
        // stage.close();
    }

    public Scene getScene() {
        return scene;
    }
}
