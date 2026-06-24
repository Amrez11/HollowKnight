package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import io.github.some_example_name.Manager.UiManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends AbstractScreen   {


    @Override
    public void show() {
        super.show();


        Stack stack =new  Stack();



        Table exitButtenWrapper=new Table();
        exitButtenWrapper.bottom().left().pad(10);
        TextButton exitBtn=new TextButton("Exit",skin);
        exitButtenWrapper.add(exitBtn).width(100);
        stack.add(exitButtenWrapper);

        Table settingsButtenWrapper =new Table();
        settingsButtenWrapper.top().right().pad(10);
        TextButton settingsBtn=new TextButton("settings",skin);
        settingsButtenWrapper.add(settingsBtn).width(100);
        stack.add(settingsButtenWrapper);

        Table playButtenWrapper=new Table();
        playButtenWrapper.bottom().center().pad(50);
        playButtenWrapper.defaults().width(100).space(10);
        TextButton startGameBtn=new TextButton("play",skin);
        TextButton Guide=new TextButton("guide",skin);
        TextButton achievements=new TextButton("achievements",skin);
        playButtenWrapper.add(startGameBtn).width(100).row();
        playButtenWrapper.add(Guide).width(100).row();
        playButtenWrapper.add(achievements).width(100).row();

        stack.add(playButtenWrapper);

        rootTable.add(stack).grow();

        startGameBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new LoadMenuScreen());
            }
        });

        exitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
