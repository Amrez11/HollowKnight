package io.github.some_example_name.controller;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.Screens.MainMenuScreen;
import io.github.some_example_name.fir.controller.view.StartGameV;
import io.github.some_example_name.fir.controller.view.KeyBindingMenuV;

public class SettingsMenuController {


    private Preferences prefs;
    private float musicVolume;
    private float sfxVolume;
    private float brightness;
    private boolean musicMuted;
    private boolean sfxMuted;
    private float preMuteMusicVolume;
    private float preMuteSfxVolume;
    private static SettingsMenuController instance;

    public SettingsMenuController() {
        prefs = Gdx.app.getPreferences("HollowKnightSettings");
        musicVolume = prefs.getFloat("musicVolume", 0.5f);
        sfxVolume = prefs.getFloat("sfxVolume", 0.5f);
        brightness = prefs.getFloat("brightness", 0.8f);
        musicMuted = prefs.getBoolean("musicMuted", false);
        sfxMuted = prefs.getBoolean("sfxMuted", false);
        preMuteMusicVolume = prefs.getFloat("preMuteMusicVolume", 0.5f);
        preMuteSfxVolume = prefs.getFloat("preMuteSfxVolume", 0.5f);
        instance = this;
    }

    // --- Music ---
    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (volume > 0) {
            musicMuted = false;
        }
        prefs.putFloat("musicVolume", volume);
        prefs.putBoolean("musicMuted", musicMuted);
        prefs.flush();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void resetMusicVolume() {
        this.musicVolume = 0.5f;
        this.musicMuted = false;
        prefs.putFloat("musicVolume", 0.5f);
        prefs.putBoolean("musicMuted", false);
        prefs.flush();
    }

    public void toggleMusicMute() {
        if (musicMuted) {
            // Unmute - restore previous volume
            musicMuted = false;
            musicVolume = preMuteMusicVolume;
        } else {
            // Mute - save current and set to 0
            musicMuted = true;
            preMuteMusicVolume = musicVolume;
            musicVolume = 0;
        }
        prefs.putBoolean("musicMuted", musicMuted);
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putFloat("preMuteMusicVolume", preMuteMusicVolume);
        prefs.flush();
    }

    public boolean isMusicMuted() {
        return musicMuted;
    }

    // --- SFX ---
    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
        if (volume > 0) {
            sfxMuted = false;
        }
        prefs.putFloat("sfxVolume", volume);
        prefs.putBoolean("sfxMuted", sfxMuted);
        prefs.flush();
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void resetSfxVolume() {
        this.sfxVolume = 0.5f;
        this.sfxMuted = false;
        prefs.putFloat("sfxVolume", 0.5f);
        prefs.putBoolean("sfxMuted", false);
        prefs.flush();
    }

    public void toggleSfxMute() {
        if (sfxMuted) {
            sfxMuted = false;
            sfxVolume = preMuteSfxVolume;
        } else {
            sfxMuted = true;
            preMuteSfxVolume = sfxVolume;
            sfxVolume = 0;
        }
        prefs.putBoolean("sfxMuted", sfxMuted);
        prefs.putFloat("sfxVolume", sfxVolume);
        prefs.putFloat("preMuteSfxVolume", preMuteSfxVolume);
        prefs.flush();
    }

    public boolean isSfxMuted() {
        return sfxMuted;
    }

    // --- Brightness ---
    public void setBrightness(float brightness) {
        this.brightness = brightness;
        prefs.putFloat("brightness", brightness);
        prefs.flush();
    }

    public float getBrightness() {
        return brightness;
    }

    public void resetBrightness() {
        this.brightness = 0.8f;
        prefs.putFloat("brightness", 0.8f);
        prefs.flush();
    }

    // --- Navigation ---
    public void openKeyBindings() {

    }

    public void goBack() {
        UiManager.setScreen(new MainMenuScreen());
    }

    public static SettingsMenuController getInstance() {
        return instance;
    }
}
