package game.ui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Manages game sound effects and background music.
 */
public class SoundManager {
    
    private static SoundManager instance;
    
    // Sound effects
    private AudioClip placeSound;
    private AudioClip scoreSound;
    private AudioClip shiftSound;
    private AudioClip disruptSound;
    private AudioClip sealSound;
    private AudioClip errorSound;
    private AudioClip gameOverSound;
    
    // Background music
    private MediaPlayer bgMusicPlayer;
    
    // Volume settings
    private double sfxVolume = 0.7;
    private double musicVolume = 0.5;

    private SoundManager() {
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Load all sound files.
     */
    private void loadSounds() {
        // TODO: Load sound files from resources
        // placeSound = new AudioClip(getClass().getResource("/sounds/place.wav").toString());
        // etc.
    }

    /**
     * Play sound effect for piece placement.
     */
    public void playPlaceSound() {
        // TODO: Play sound at sfxVolume
        // if (placeSound != null) {
        //     placeSound.play(sfxVolume);
        // }
    }

    /**
     * Play sound effect for scoring.
     */
    public void playScoreSound() {
        // TODO: Play score sound
    }

    /**
     * Play sound effect for shift action.
     */
    public void playShiftSound() {
        // TODO: Play shift sound
    }

    /**
     * Play sound effect for disrupt action.
     */
    public void playDisruptSound() {
        // TODO: Play disrupt sound
    }

    /**
     * Play sound effect for seal action.
     */
    public void playSealSound() {
        // TODO: Play seal sound
    }

    /**
     * Play sound effect for invalid action.
     */
    public void playErrorSound() {
        // TODO: Play error sound
    }

    /**
     * Play sound effect for game over.
     */
    public void playGameOverSound() {
        // TODO: Play game over sound
    }

    /**
     * Start background music loop.
     */
    public void startBackgroundMusic() {
        // TODO: Start looping background music
        // if (bgMusicPlayer != null) {
        //     bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        //     bgMusicPlayer.setVolume(musicVolume);
        //     bgMusicPlayer.play();
        // }
    }

    /**
     * Stop background music.
     */
    public void stopBackgroundMusic() {
        // TODO: Stop music
        // if (bgMusicPlayer != null) {
        //     bgMusicPlayer.stop();
        // }
    }

    /**
     * Set sound effects volume (0.0 to 1.0).
     */
    public void setSfxVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));
    }

    /**
     * Set music volume (0.0 to 1.0).
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (bgMusicPlayer != null) {
            bgMusicPlayer.setVolume(musicVolume);
        }
    }
}
