package game.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX application entry point for XO Enchanted.
 */
public class XOEnchantedApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // TODO: Implement application startup
        // 1. Set window title
        // 2. Create and show main menu scene
        // 3. Set window size
        // 4. Show stage
        
        primaryStage.setTitle("XO Enchanted 4x4");
        // primaryStage.setScene(new MainMenuScene(...));
        // primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
