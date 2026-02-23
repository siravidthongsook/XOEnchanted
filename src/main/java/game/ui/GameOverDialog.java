package game.ui;

import game.model.GameResult;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Dialog displayed when game ends.
 */
public class GameOverDialog extends Alert {
    
    public GameOverDialog(GameResult result) {
        super(AlertType.INFORMATION);
        setTitle("Game Over");
        
        // TODO: Set header and content based on result type
        // - If Victory: show winner and victory type
        // - Include final scores
        // - Add "Play Again" and "Main Menu" buttons
    }
}
