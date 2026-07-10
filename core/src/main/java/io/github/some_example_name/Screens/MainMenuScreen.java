package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.Manager.UiManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.controller.SettingsMenuController;
import io.github.some_example_name.model.costumActors.GuideModal;
import io.github.some_example_name.model.costumActors.SettingsModal;


public class MainMenuScreen extends AbstractScreen   {
    private Texture background;


    @Override
    public void show() {
        super.show();


        Stack stack =new  Stack();


        background = new Texture("h/Menu/vheart_save_Switch.png");
        rootTable.setBackground(
            new TextureRegionDrawable(
                new TextureRegion(background)
            )
        );
        Table exitButtenWrapper=new Table();
        exitButtenWrapper.bottom().left().pad(10);
        TextButton exitBtn=new TextButton("Exit",textButtonStyle);
        exitButtenWrapper.add(exitBtn).width(100);
        stack.add(exitButtenWrapper);

        Table settingsButtenWrapper =new Table();
        settingsButtenWrapper.top().right().pad(30f,0f,0f,120f);
        TextButton settingsBtn=new TextButton("settings",textButtonStyle);
        settingsButtenWrapper.add(settingsBtn).width(100);
        stack.add(settingsButtenWrapper);

        Table playButtenWrapper=new Table();
        playButtenWrapper.bottom().center().pad(50);
        playButtenWrapper.defaults().width(100).space(10);
        TextButton startGameBtn=new TextButton("play",textButtonStyle);
        TextButton Guide=new TextButton("guide",textButtonStyle);
        TextButton achievements=new TextButton("achievements",textButtonStyle);
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
        settingsBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new SettingsModal(new SettingsMenuController()).show();
            }
        });
        Guide.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new GuideModal().show();
            }
        });
        achievements.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new AchievementsScreen());
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
