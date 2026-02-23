package game.ui;

import game.model.ScoringLine;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Dialog for selecting which scoring line to clear when multiple lines are formed.
 */
public class LineSelectionDialog extends Dialog<ScoringLine> {
    
    public LineSelectionDialog(List<ScoringLine> availableLines) {
        setTitle("Choose Scoring Line");
        setHeaderText("Multiple scoring lines detected! Choose one to clear:");
        
        // TODO: Create radio buttons for each line
        // - Display line positions and type
        // - Use ToggleGroup to ensure single selection
        // - Add OK and Cancel buttons
        // - Set result converter to return selected line
    }
}
