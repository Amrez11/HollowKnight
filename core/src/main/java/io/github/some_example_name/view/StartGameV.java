package io.github.some_example_name.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.some_example_name.controller.StartGameC;
import io.github.some_example_name.model.enums.MenuTypes;

import javax.swing.event.ChangeEvent;

public class StartGameV extends ScreenAdapter implements IMenuV   {


    @Override
    public MenuTypes getMenuType() {
        return null;
    }


    private final Game game;
    private final StartGameC controller;
    private Stage stage;
    public StartGameV(Game game) {
            this.game = game;
            this.controller = new StartGameC(game);  // controller for navigation logic
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin ;

        // ----- VIEW part: building the UI -----
        TextButton playButton = new TextButton("Play", skin);
        playButton.setPosition(200, 150);
        stage.addActor(playButton);
        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.setPosition(200, 80);
        stage.addActor(settingsButton);

        // ----- INPUT part: attach listeners, delegate to controller -----
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.startGame();   // controller knows how to switch screen
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.openSettings(); // controller handles screen change
            }
        });
        }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        }
    }
}
