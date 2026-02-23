package game.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

/**
 * Dialog for adjusting game settings.
 */
public class SettingsDialog extends Dialog<Void> {
    
    private final Slider sfxVolumeSlider;
    private final Slider musicVolumeSlider;

    public SettingsDialog() {
        setTitle("Settings");
        setHeaderText("Game Settings");
        
        this.sfxVolumeSlider = new Slider(0, 1, 0.7);
        this.musicVolumeSlider = new Slider(0, 1, 0.5);
        
        // TODO: Create settings UI
        // - Volume sliders for SFX and Music
        // - Theme selection (if implemented)
        // - Animation speed toggle
        // - Apply button to save settings
        
        GridPane grid = new GridPane();
        grid.add(new Label("SFX Volume:"), 0, 0);
        grid.add(sfxVolumeSlider, 1, 0);
        grid.add(new Label("Music Volume:"), 0, 1);
        grid.add(musicVolumeSlider, 1, 1);
        
        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // TODO: Wire up sliders to SoundManager
    }
}
