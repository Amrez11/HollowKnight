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
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.controller.SettingsMenuController;

public class SettingsModal extends Modal {

    private static final Texture PANEL_BG = new Texture("ui/stone-tablet-bg.png");
    private static Slider.SliderStyle sliderStyle;

    private static Slider.SliderStyle sliderStyle() {
        if (sliderStyle != null) return sliderStyle;

        int trackWidth = 350;
        int trackHeight = 16;
        int radius = trackHeight / 2; // 8px radius

        // 1. Empty Track (Dark Stone Gray)
        Pixmap trackPixmap = new Pixmap(trackWidth, trackHeight, Pixmap.Format.RGBA8888);
        trackPixmap.setColor(new Color(0.2f, 0.2f, 0.22f, 0.8f));
        trackPixmap.fillRectangle(radius, 0, trackWidth - (radius * 2), trackHeight);
        trackPixmap.fillCircle(radius, radius, radius);
        trackPixmap.fillCircle(trackWidth - radius, radius, radius);

        NinePatch trackPatch = new NinePatch(new Texture(trackPixmap), radius, radius, 0, 0);
        NinePatchDrawable trackDrawable = new NinePatchDrawable(trackPatch);

        // THE FIX: Force the boundaries to 0 so the knob can reach the absolute edge
        trackDrawable.setLeftWidth(0);
        trackDrawable.setRightWidth(0);
        trackDrawable.setMinHeight(trackHeight);

        trackPixmap.dispose();

        // 2. Filled Track (Glowing Cyan/Blue)
        Pixmap filledPixmap = new Pixmap(trackWidth, trackHeight, Pixmap.Format.RGBA8888);
        filledPixmap.setColor(new Color(0.2f, 0.7f, 1f, 1f));
        filledPixmap.fillRectangle(radius, 0, trackWidth - (radius * 2), trackHeight);
        filledPixmap.fillCircle(radius, radius, radius);
        filledPixmap.fillCircle(trackWidth - radius, radius, radius);

        NinePatch filledPatch = new NinePatch(new Texture(filledPixmap), radius, radius, 0, 0);
        NinePatchDrawable filledDrawable = new NinePatchDrawable(filledPatch);

        // THE FIX: Apply the same zero-width boundaries to the filled portion
        filledDrawable.setLeftWidth(0);
        filledDrawable.setRightWidth(0);
        filledDrawable.setMinHeight(trackHeight);

        filledPixmap.dispose();

        // 3. Knob (White/Light Blue)
        int knobSize = 20;
        Pixmap knobPixmap = new Pixmap(knobSize, knobSize, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(new Color(0.9f, 0.95f, 1f, 1f));
        knobPixmap.fillCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        knobPixmap.setColor(new Color(0.4f, 0.6f, 0.8f, 1f));
        knobPixmap.drawCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();

        // 4. Assemble the Style
        Slider.SliderStyle style = new Slider.SliderStyle();

        style.background = trackDrawable;
        style.knobBefore = filledDrawable;

        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(knobTexture));
        knobDrawable.setMinWidth(knobSize);
        knobDrawable.setMinHeight(knobSize);

        style.knob = knobDrawable;
        style.knobOver = knobDrawable;
        style.knobDown = knobDrawable;

        sliderStyle = style;
        return sliderStyle;
    }

    private final SettingsMenuController controller;

    public SettingsModal(SettingsMenuController controller) {
        super();
        this.controller = controller;
        setTouchable(Touchable.enabled);
        background(new TextureRegionDrawable(new TextureRegion(PANEL_BG)));
        pad(40);

        Label title = new Label("SETTINGS", GameAssetManager.labelStyle);
        title.setAlignment(Align.center);

        // UI Components
        Label musicValue = smallLabel(pct(controller.getMusicVolume()));
        Label sfxValue = smallLabel(pct(controller.getSfxVolume()));
        Label brightnessValue = smallLabel(pct(controller.getBrightness()));

        Slider musicSlider = createSlider(controller.getMusicVolume());
        Slider sfxSlider = createSlider(controller.getSfxVolume());
        Slider brightnessSlider = createSlider(controller.getBrightness());

        TextButton musicMuteBtn = smallButton(controller.isMusicMuted() ? "Unmute" : "Mute");
        TextButton sfxMuteBtn = smallButton(controller.isSfxMuted() ? "Unmute" : "Mute");
        TextButton musicResetBtn = smallButton("Reset");
        TextButton sfxResetBtn = smallButton("Reset");
        TextButton brightnessResetBtn = smallButton("Reset");
        TextButton languageBtn = smallButton(controller.getLanguageName());
        TextButton backButton = smallButton("Back");

        // Listeners
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

        // --- LAYOUT ---
        defaults().space(15).padBottom(5);

        add(title).colspan(4).padBottom(30).center().row();

        addRow("MUSIC", musicSlider, musicValue, musicMuteBtn, musicResetBtn);
        addRow("SFX", sfxSlider, sfxValue, sfxMuteBtn, sfxResetBtn);
        addRow("BRIGHTNESS", brightnessSlider, brightnessValue, null, brightnessResetBtn);

        // Language Row
        add(smallLabel("LANGUAGE")).left().padRight(20);
        add(languageBtn).width(180).height(45).left().colspan(3);
        row();

        add(backButton).colspan(4).width(200).height(50).padTop(30).center();
    }

    private void addRow(String labelText, Slider slider, Label valLabel, TextButton btn1, TextButton btn2) {
        add(smallLabel(labelText)).left().padRight(20);
        add(slider).width(280).height(24).padRight(15);
        add(valLabel).width(50).left();

        Table btnTable = new Table();
        btnTable.defaults().space(10);
        if (btn1 != null) btnTable.add(btn1).width(90).height(40);
        if (btn2 != null) btnTable.add(btn2).width(90).height(40);
        add(btnTable).right().row();
    }

    private Slider createSlider(float initialValue) {
        Slider slider = new Slider(0f, 1f, 0.01f, false, sliderStyle());
        slider.setValue(initialValue);
        return slider;
    }

    private static Label smallLabel(String text) {
        Label label = new Label(text, GameAssetManager.labelStyle);
        label.setFontScale(0.5f);
        return label;
    }

    private TextButton smallButton(String text) {
        TextButton button = new TextButton(text, textButtonStyle); // Uses your existing textButtonStyle
        button.getLabel().setFontScale(0.45f);
        return button;
    }

    private static String pct(float value) {
        return (int) (value * 100) + "%";
    }
}
