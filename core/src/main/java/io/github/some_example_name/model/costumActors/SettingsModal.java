package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.controller.SettingsMenuController;

/**
 * Settings panel shown as an overlay (Modal) instead of a full Screen.
 * That means it can be opened from the pause menu mid-game OR from the
 * main menu, and in both cases pressing Back just removes the overlay and
 * drops you back exactly where you were — no screen swap, no losing the
 * paused game underneath.
 *
 * Music/SFX/Brightness all read & write through the SettingsMenuController
 * passed in, so changes (in particular brightness) are visible immediately
 * to whatever is reading that same controller instance (see GameScreen).
 */
public class SettingsModal extends Modal {

    private static final Texture PANEL_BG = new Texture("ui/stone-tablet-bg.png");

    // --- Cached slider visuals (built once, reused for every instance) ---
    private static Slider.SliderStyle sliderStyle;

    private static Slider.SliderStyle sliderStyle() {
        if (sliderStyle != null) return sliderStyle;

        int trackWidth = 360;
        int trackHeight = 22;

        Pixmap trackPixmap = new Pixmap(trackWidth, trackHeight, Pixmap.Format.RGBA8888);
        trackPixmap.setColor(new Color(0.15f, 0.15f, 0.18f, 1));
        trackPixmap.fill();
        trackPixmap.setColor(new Color(0.4f, 0.4f, 0.45f, 1));
        trackPixmap.fillRectangle(6, 0, trackWidth - 12, trackHeight);
        trackPixmap.fillCircle(6, trackHeight / 2, 6);
        trackPixmap.fillCircle(trackWidth - 6, trackHeight / 2, 6);
        NinePatch trackPatch = new NinePatch(new Texture(trackPixmap), 6, 6, 6, 6);
        trackPixmap.dispose();

        Pixmap filledPixmap = new Pixmap(trackWidth, trackHeight, Pixmap.Format.RGBA8888);
        filledPixmap.setColor(new Color(0.85f, 0.65f, 0.1f, 1));
        filledPixmap.fill();
        filledPixmap.setColor(new Color(1f, 0.84f, 0.2f, 1));
        filledPixmap.fillRectangle(6, 0, trackWidth - 12, trackHeight);
        filledPixmap.fillCircle(6, trackHeight / 2, 6);
        filledPixmap.fillCircle(trackWidth - 6, trackHeight / 2, 6);
        NinePatch filledPatch = new NinePatch(new Texture(filledPixmap), 6, 6, 6, 6);
        filledPixmap.dispose();

        int knobSize = 22;
        Pixmap knobPixmap = new Pixmap(knobSize, knobSize, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(new Color(1f, 0.9f, 0.4f, 1));
        knobPixmap.fillCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        knobPixmap.setColor(new Color(0.6f, 0.45f, 0f, 1));
        knobPixmap.drawCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();

        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = new NinePatchDrawable(trackPatch);
        style.background.setMinHeight(trackHeight);
        style.knobBefore = new NinePatchDrawable(filledPatch);
        style.knobBefore.setMinHeight(trackHeight);
        style.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        style.knob.setMinWidth(knobSize);
        style.knob.setMinHeight(knobSize);
        style.knobOver = style.knob;
        style.knobDown = style.knob;

        sliderStyle = style;
        return sliderStyle;
    }

    private final SettingsMenuController controller;

    public SettingsModal(SettingsMenuController controller) {
        super();
        this.controller = controller;
        setTouchable(Touchable.enabled);
        background(new TextureRegionDrawable(new TextureRegion(PANEL_BG)));
        pad(30);

        Label title = new Label("Settings", GameAssetManager.labelStyle);

        Label musicLabel = smallLabel("Music");
        Label sfxLabel = smallLabel("SFX");
        Label brightnessLabel = smallLabel("Brightness");
        Label languageLabel = smallLabel("Language");

        Label musicValue = smallLabel(pct(controller.getMusicVolume()));
        Label sfxValue = smallLabel(pct(controller.getSfxVolume()));
        Label brightnessValue = smallLabel(pct(controller.getBrightness()));

        Slider musicSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle());
        musicSlider.setValue(controller.getMusicVolume());

        Slider sfxSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle());
        sfxSlider.setValue(controller.getSfxVolume());

        Slider brightnessSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle());
        brightnessSlider.setValue(controller.getBrightness());

        TextButton musicMuteBtn = smallButton(controller.isMusicMuted() ? "Unmute" : "Mute");
        TextButton sfxMuteBtn = smallButton(controller.isSfxMuted() ? "Unmute" : "Mute");
        TextButton musicResetBtn = smallButton("Reset");
        TextButton sfxResetBtn = smallButton("Reset");
        TextButton brightnessResetBtn = smallButton("Reset");
        TextButton languageBtn = smallButton(controller.getLanguageName());
        TextButton backButton = smallButton("Back");

        musicSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.setMusicVolume(musicSlider.getValue());
                musicValue.setText(pct(controller.getMusicVolume()));
                musicMuteBtn.setText(controller.isMusicMuted() ? "Unmute" : "Mute");
            }
        });
        sfxSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.setSfxVolume(sfxSlider.getValue());
                sfxValue.setText(pct(controller.getSfxVolume()));
                sfxMuteBtn.setText(controller.isSfxMuted() ? "Unmute" : "Mute");
            }
        });
        brightnessSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.setBrightness(brightnessSlider.getValue());
                brightnessValue.setText(pct(controller.getBrightness()));
            }
        });

        musicMuteBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.toggleMusicMute();
                musicMuteBtn.setText(controller.isMusicMuted() ? "Unmute" : "Mute");
                musicSlider.setValue(controller.getMusicVolume());
                musicValue.setText(pct(controller.getMusicVolume()));
            }
        });
        sfxMuteBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.toggleSfxMute();
                sfxMuteBtn.setText(controller.isSfxMuted() ? "Unmute" : "Mute");
                sfxSlider.setValue(controller.getSfxVolume());
                sfxValue.setText(pct(controller.getSfxVolume()));
            }
        });
        musicResetBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.resetMusicVolume();
                musicSlider.setValue(controller.getMusicVolume());
                musicValue.setText(pct(controller.getMusicVolume()));
                musicMuteBtn.setText(controller.isMusicMuted() ? "Unmute" : "Mute");
            }
        });
        sfxResetBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.resetSfxVolume();
                sfxSlider.setValue(controller.getSfxVolume());
                sfxValue.setText(pct(controller.getSfxVolume()));
                sfxMuteBtn.setText(controller.isSfxMuted() ? "Unmute" : "Mute");
            }
        });
        brightnessResetBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.resetBrightness();
                brightnessSlider.setValue(controller.getBrightness());
                brightnessValue.setText(pct(controller.getBrightness()));
            }
        });
        // Cosmetic only — cycles the label, doesn't retranslate anything yet.
        languageBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                languageBtn.setText(controller.cycleLanguage());
            }
        });
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        defaults().space(14);
        add(title).colspan(4).padBottom(20).row();

        add(musicLabel).left().width(140);
        add(musicSlider).width(300).height(28);
        add(musicValue).width(60);
        Table musicBtns = new Table();
        musicBtns.defaults().space(8);
        musicBtns.add(musicMuteBtn).width(100).height(48);
        musicBtns.add(musicResetBtn).width(90).height(48);
        add(musicBtns).row();

        add(sfxLabel).left().width(140);
        add(sfxSlider).width(300).height(28);
        add(sfxValue).width(60);
        Table sfxBtns = new Table();
        sfxBtns.defaults().space(8);
        sfxBtns.add(sfxMuteBtn).width(100).height(48);
        sfxBtns.add(sfxResetBtn).width(90).height(48);
        add(sfxBtns).row();

        add(brightnessLabel).left().width(140);
        add(brightnessSlider).width(300).height(28);
        add(brightnessValue).width(60);
        add(brightnessResetBtn).width(90).height(48).row();

        add(languageLabel).left().width(140);
        add(languageBtn).width(180).height(48).colspan(2).left();
        add().row();

        add(backButton).colspan(4).width(220).height(56).padTop(20);
    }

    private static Label smallLabel(String text) {
        Label label = new Label(text, GameAssetManager.labelStyle);
        label.setFontScale(0.5f);
        return label;
    }

    private TextButton smallButton(String text) {
        TextButton button = new TextButton(text, textButtonStyle);
        button.getLabel().setFontScale(0.45f);
        return button;
    }

    private static String pct(float value) {
        return (int) (value * 100) + "%";
    }
}
