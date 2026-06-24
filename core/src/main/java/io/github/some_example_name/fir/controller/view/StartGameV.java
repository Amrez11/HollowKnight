package io.github.some_example_name.fir.controller.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.fir.controller.StartGameC;
import io.github.some_example_name.fir.controller.model.enums.MenuTypes;
import io.github.some_example_name.fir.controller.utility.BrightnessOverlay;

public class StartGameV extends ScreenAdapter implements IMenuV {
    private final Game game;
    private final StartGameC controller;

    private Stage stage;
    private Texture background;
    private SpriteBatch batch;
    private Viewport viewport;

    public StartGameV(Game game) {
        this.game = game;
        this.controller = new StartGameC(game);
    }

    @Override
    public MenuTypes getMenuType() {
        return null;
    }

    @Override
    public void show() {
        viewport = new FitViewport(1920, 1080);
        viewport.apply();

        batch = new SpriteBatch();
        background = new Texture("h/Menu/vheart_save_Switch.png");

        // Generate custom font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TrajanPro-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        BitmapFont trajanFont = generator.generateFont(parameter);
        generator.dispose();

        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Create a text-only button style (no background images)
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = trajanFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.GOLD;
        buttonStyle.downFontColor = Color.GRAY;

        // Layout with Table
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        TextButton playButton = new TextButton("Play", buttonStyle);
        playButton.getLabel().setFontScale(1.5f);  // Just scale the text!
        TextButton settingsButton = new TextButton("Settings", buttonStyle);
        TextButton achievementButton = new TextButton("Achievements", buttonStyle);
        TextButton quitButten = new TextButton("Quit Game", buttonStyle);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.startGame();
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.openSettings();
            }
        });
        achievementButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.openSettings();
            }
        });
        quitButten.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.openSettings();
            }
        });

        table.add(playButton).width(600).height(180).padBottom(30).row();
        table.add(settingsButton).padBottom(30).row();
        table.add(achievementButton).padBottom(30).row();
        table.add(quitButten);
        stage.addActor(table);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
        BrightnessOverlay.apply(batch, viewport);  // ← ADD THIS LINE
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        background.dispose();
    }
}
