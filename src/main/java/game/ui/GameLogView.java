package game.ui;

import javafx.scene.control.TextArea;

/**
 * Text area displaying game move history and events.
 */
public class GameLogView extends TextArea {
    
    public GameLogView() {
        setEditable(false);
        setWrapText(true);
        setPrefRowCount(10);
        getStyleClass().add("game-log");
        
        // TODO: Style as needed
    }

    /**
     * Add a log entry.
     */
    public void addEntry(String message) {
        // TODO: Append message with timestamp or turn number
        appendText(message + "\n");
    }

    /**
     * Clear all log entries.
     */
    public void clearLog() {
        clear();
    }
}
