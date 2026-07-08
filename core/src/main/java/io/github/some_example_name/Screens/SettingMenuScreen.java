package io.github.some_example_name.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.controller.SettingsMenuController;


public class SettingMenuScreen extends AbstractScreen{

    private final SettingsMenuController controller;



    private Texture background;

    public SettingMenuScreen(SettingsMenuController controller) {
        this.controller = controller;
    }

    @Override
    public void show() {
        super.show();
        background = new Texture("h/Menu/vheart_save_Switch.png");
        rootTable.setBackground(
            new TextureRegionDrawable(
                new TextureRegion(background)
            )
        );
        Gdx.input.setInputProcessor(stage);

        // --- Generate font ---
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TrajanPro-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 48;
        BitmapFont font = generator.generateFont(param);


        // Smaller font for mute buttons
        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 36;
        BitmapFont smallFont = generator.generateFont(smallParam);  // You'll need to regenerate or reuse
        generator.dispose();


        // ============================================
        // CREATE PREMIUM SLIDER STYLE
        // ============================================

        int trackWidth = 400;
        int trackHeight = 28;
        Pixmap trackPixmap = new Pixmap(trackWidth, trackHeight, Pixmap.Format.RGBA8888);

        trackPixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 1));
        trackPixmap.fill();

        trackPixmap.setColor(Color.WHITE);
        trackPixmap.fillRectangle(8, 0, trackWidth - 16, trackHeight);
        trackPixmap.fillCircle(8, 8, 8);
        trackPixmap.fillCircle(trackWidth - 8, 8, 8);

        Texture trackTexture = new Texture(trackPixmap);
        trackPixmap.dispose();
        NinePatch trackNinePatch = new NinePatch(trackTexture, 8, 8, 8, 8);

        Pixmap filledPixmap = new Pixmap(trackWidth, trackHeight, Pixmap.Format.RGBA8888);

        filledPixmap.setColor(new Color(0.9f, 0.7f, 0.1f, 1));
        filledPixmap.fill();

        filledPixmap.setColor(Color.WHITE);
        filledPixmap.fillRectangle(8, 0, trackWidth - 16, trackHeight);
        filledPixmap.fillCircle(8, 8, 8);
        filledPixmap.fillCircle(trackWidth - 8, 8, 8);

        Texture filledTexture = new Texture(filledPixmap);
        filledPixmap.dispose();
        NinePatch filledNinePatch = new NinePatch(filledTexture, 8, 8, 8, 8);

        int knobSize = 24;

        Pixmap knobPixmap = new Pixmap(knobSize, knobSize, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(new Color(1f, 0.84f, 0f, 1));
        knobPixmap.fillCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        knobPixmap.setColor(new Color(0.7f, 0.55f, 0f, 1));
        knobPixmap.drawCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();

        Pixmap knobHoverPixmap = new Pixmap(knobSize, knobSize, Pixmap.Format.RGBA8888);
        knobHoverPixmap.setColor(new Color(1f, 0.92f, 0.3f, 1));
        knobHoverPixmap.fillCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        knobHoverPixmap.setColor(new Color(0.8f, 0.7f, 0.2f, 1));
        knobHoverPixmap.drawCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        Texture knobHoverTexture = new Texture(knobHoverPixmap);
        knobHoverPixmap.dispose();

        Pixmap knobDownPixmap = new Pixmap(knobSize, knobSize, Pixmap.Format.RGBA8888);
        knobDownPixmap.setColor(new Color(0.7f, 0.55f, 0f, 1));
        knobDownPixmap.fillCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        knobDownPixmap.setColor(new Color(0.5f, 0.35f, 0f, 1));
        knobDownPixmap.drawCircle(knobSize / 2, knobSize / 2, knobSize / 2 - 1);
        Texture knobDownTexture = new Texture(knobDownPixmap);
        knobDownPixmap.dispose();

        // --- Create slider style ---
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();

        sliderStyle.background = new NinePatchDrawable(trackNinePatch);
        sliderStyle.background.setMinHeight(16);
        sliderStyle.background.setLeftWidth(0);
        sliderStyle.background.setRightWidth(0);

        sliderStyle.knobBefore = new NinePatchDrawable(filledNinePatch);
        sliderStyle.knobBefore.setMinHeight(16);
        sliderStyle.knobBefore.setLeftWidth(0);
        sliderStyle.knobBefore.setRightWidth(0);

        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        sliderStyle.knob.setMinWidth(knobSize);
        sliderStyle.knob.setMinHeight(knobSize);

        sliderStyle.knobOver = new TextureRegionDrawable(new TextureRegion(knobHoverTexture));
        sliderStyle.knobOver.setMinWidth(knobSize);
        sliderStyle.knobOver.setMinHeight(knobSize);

        sliderStyle.knobDown = new TextureRegionDrawable(new TextureRegion(knobDownTexture));
        sliderStyle.knobDown.setMinWidth(knobSize);
        sliderStyle.knobDown.setMinHeight(knobSize);

        // ============================================
        // CREATE UI ELEMENTS
        // ============================================

        // --- Labels ---
        Label titleLabel = new Label("Settings", labelStyle);
        Label musicLabel = new Label("Music", labelStyle);
        Label sfxLabel = new Label("SFX", labelStyle);
        Label brightnessLabel = new Label("Brightness", labelStyle);

        Label musicValueLabel = new Label((int)(controller.getMusicVolume() * 100) + "%", labelStyle);
        Label sfxValueLabel = new Label((int)(controller.getSfxVolume() * 100) + "%", labelStyle);
        Label brightnessValueLabel = new Label((int)(controller.getBrightness() * 100) + "%", labelStyle);

        // --- Sliders ---
        Slider musicSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        musicSlider.setValue(controller.getMusicVolume());

        Slider sfxSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        sfxSlider.setValue(controller.getSfxVolume());

        Slider brightnessSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        brightnessSlider.setValue(controller.getBrightness());

        // --- Button styles ---
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.GOLD;
        buttonStyle.downFontColor = Color.GRAY;

        TextButton.TextButtonStyle muteButtonStyle = new TextButton.TextButtonStyle();
        muteButtonStyle.font = font;
        muteButtonStyle.fontColor = Color.WHITE;
        muteButtonStyle.overFontColor = Color.RED;
        muteButtonStyle.downFontColor = Color.GRAY;
        TextButton musicMuteButton = new TextButton(controller.isMusicMuted() ? "Unmute" : "Mute", muteButtonStyle);
        TextButton sfxMuteButton = new TextButton(controller.isSfxMuted() ? "Unmute" : "Mute", muteButtonStyle);
        // --- Slider listeners ---
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setMusicVolume(musicSlider.getValue());
                musicValueLabel.setText((int)(controller.getMusicVolume() * 100) + "%");
                updateMuteButtonText(musicMuteButton, controller.isMusicMuted());
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setSfxVolume(sfxSlider.getValue());
                sfxValueLabel.setText((int)(controller.getSfxVolume() * 100) + "%");
                updateMuteButtonText(sfxMuteButton, controller.isSfxMuted());
            }
        });

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setBrightness(brightnessSlider.getValue());
                brightnessValueLabel.setText((int)(controller.getBrightness() * 100) + "%");
            }
        });

        // --- Mute buttons ---

        musicMuteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.toggleMusicMute();
                updateMuteButtonText(musicMuteButton, controller.isMusicMuted());
                musicSlider.setValue(controller.getMusicVolume());
                musicValueLabel.setText((int)(controller.getMusicVolume() * 100) + "%");
            }
        });


        sfxMuteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.toggleSfxMute();
                updateMuteButtonText(sfxMuteButton, controller.isSfxMuted());
                sfxSlider.setValue(controller.getSfxVolume());
                sfxValueLabel.setText((int)(controller.getSfxVolume() * 100) + "%");
            }
        });

        // --- Reset buttons ---
        TextButton resetMusicButton = new TextButton("Reset", buttonStyle);
        resetMusicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.resetMusicVolume();
                musicSlider.setValue(controller.getMusicVolume());
                musicValueLabel.setText((int)(controller.getMusicVolume() * 100) + "%");
                updateMuteButtonText(musicMuteButton, controller.isMusicMuted());
            }
        });

        TextButton resetSfxButton = new TextButton("Reset", buttonStyle);
        resetSfxButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.resetSfxVolume();
                sfxSlider.setValue(controller.getSfxVolume());
                sfxValueLabel.setText((int)(controller.getSfxVolume() * 100) + "%");
                updateMuteButtonText(sfxMuteButton, controller.isSfxMuted());
            }
        });

        TextButton resetBrightnessButton = new TextButton("Reset", buttonStyle);
        resetBrightnessButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.resetBrightness();
                brightnessSlider.setValue(controller.getBrightness());
                brightnessValueLabel.setText((int)(controller.getBrightness() * 100) + "%");
            }
        });

        // --- Key bindings button ---
        TextButton keyBindingsButton = new TextButton("Key Bindings", buttonStyle);
        keyBindingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.openKeyBindings();
            }
        });

        // --- Back button ---
        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.goBack();
            }
        });

        // ============================================
        // LAYOUT
        // ============================================

        // ============================================
// LAYOUT
// ============================================

        Table table = new Table();
        table.setFillParent(true);
        table.center();

// Title
        table.add(titleLabel).padBottom(50).colspan(5).row();

// ---- MUSIC ROW ----
// Label
        table.add(musicLabel).padRight(20);
// Slider
        table.add(musicSlider).width(400).height(40).padRight(20);
// Value %
        table.add(musicValueLabel).width(80).padRight(30);
// Mute button
        table.add(musicMuteButton).width(130).height(55).padRight(65);
// Reset button
        table.add(resetMusicButton).width(110).height(55).row();

        table.row().padTop(35);

// ---- SFX ROW ----
        table.add(sfxLabel).padRight(20);
        table.add(sfxSlider).width(430).height(40).padRight(20);
        table.add(sfxValueLabel).width(80).padRight(30);
        table.add(sfxMuteButton).width(130).height(55).padRight(65);
        table.add(resetSfxButton).width(110).height(55).row();

        table.row().padTop(35);

// ---- BRIGHTNESS ROW ----
        table.add(brightnessLabel).padRight(20);
        table.add(brightnessSlider).width(400).height(40).padRight(20);
        table.add(brightnessValueLabel).width(80).padRight(30);
        table.add().width(130).padRight(15);  // Empty cell to align with mute column
        table.add(resetBrightnessButton).width(110).height(55).row();

        table.row().padTop(60);

// ---- KEY BINDINGS BUTTON ----
        table.add(keyBindingsButton).width(400).height(70).colspan(5).row();

        table.row().padTop(25);

// ---- BACK BUTTON ----
        table.add(backButton).width(300).height(70).colspan(5);

        stage.addActor(table);
    }

    private void updateMuteButtonText(TextButton button, boolean isMuted) {
        button.setText(isMuted ? "Unmute" : "Mute");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);;


        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();

        background.dispose();
    }
}
