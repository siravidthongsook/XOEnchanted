package game.ui;

import game.model.PlayerId;
import game.model.PlayerState;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * Panel displaying information about a player.
 * Shows: player name, score, energy, status flags.
 */
public class PlayerInfoPanel extends VBox {
    
    private final PlayerId playerId;
    private final Label nameLabel;
    private final Label scoreLabel;
    private final Label energyLabel;
    private final ProgressBar energyBar;
    private final Label statusLabel;

    public PlayerInfoPanel(PlayerId playerId) {
        this.playerId = playerId;
        this.nameLabel = new Label("Player " + playerId);
        this.scoreLabel = new Label("Score: 0");
        this.energyLabel = new Label("Energy: 0/5");
        this.energyBar = new ProgressBar(0);
        this.statusLabel = new Label("");
        
        getChildren().addAll(nameLabel, scoreLabel, energyLabel, energyBar, statusLabel);
        getStyleClass().add("player-info-panel");
        
        // TODO: Apply styling, set spacing
    }

    /**
     * Update panel to reflect current player state.
     */
    public void updatePlayerState(PlayerState state) {
        // TODO: Update labels and progress bar
        // - Score label
        // - Energy label and progress bar (value 0-1)
        // - Status label (show priority turn, disrupt penalty, overheat warning)
    }

    /**
     * Highlight this panel when it's this player's turn.
     */
    public void setActive(boolean active) {
        // TODO: Add/remove active style class for visual indication
        if (active) {
            getStyleClass().add("active-player");
        } else {
            getStyleClass().remove("active-player");
        }
    }
}
