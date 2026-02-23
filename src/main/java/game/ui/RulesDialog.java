package game.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

/**
 * Dialog displaying game rules.
 */
public class RulesDialog extends Dialog<Void> {
    
    public RulesDialog() {
        setTitle("Game Rules");
        setHeaderText("XO Enchanted 4×4 - How to Play");
        
        // TODO: Display game rules
        // Load from SPECIFICATION.md or create formatted text
        // Use tabs for different rule sections:
        // - Objective
        // - Energy System
        // - Skills
        // - Special Rules (Frozen, Overheat, Priority)
        
        TextArea rulesText = new TextArea();
        rulesText.setEditable(false);
        rulesText.setWrapText(true);
        rulesText.setText(getRulesText());
        
        ScrollPane scrollPane = new ScrollPane(rulesText);
        scrollPane.setFitToWidth(true);
        
        getDialogPane().setContent(scrollPane);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        // TODO: Set preferred size
    }
    
    private String getRulesText() {
        // TODO: Load comprehensive rules text
        return """
            OBJECTIVE:
            - First to 3 points wins
            - If 24 turns pass, highest score wins
            - If tied after 24 turns: Sudden Death
            
            SCORING:
            - Form 3-in-a-row (horizontal, vertical, or diagonal)
            - Choose 1 line if multiple form
            - Gain +1 point and +1 energy
            - Opponent gets Priority Turn
            
            ENERGY SYSTEM:
            - Start: First player 2, Second player 3
            - Gain +1 per turn (or +2 if Priority Turn)
            - Max: 5 energy
            - Overheat: 2 turns at max → reset to 3
            
            SKILLS:
            [Place] Free - Place 1 piece
            [Seal] 2 energy - Block cell for 1 turn
            [Shift] 2 energy - Move your piece to adjacent cell
            [Disrupt] 3 energy - Remove opponent piece (not frozen). Next turn: skip +1 gain
            [Double Place] 4 energy - Place 2 pieces (not adjacent)
            
            SPECIAL RULES:
            - Frozen: Piece not moved for 3 turns becomes frozen and immune to Disrupt
            - Priority Turn: Gain +2 energy (given when opponent scores)
            """;
    }
}
