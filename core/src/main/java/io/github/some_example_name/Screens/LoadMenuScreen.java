package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Manager.UiManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.SaveInfo.GameSave;
import io.github.some_example_name.model.Game;
import io.github.some_example_name.model.costumActors.SaveCard;

public class LoadMenuScreen extends AbstractScreen {
    private SpriteBatch batch;
    private Texture background;

    @Override
    public void show() {
        super.show();
        Stack stack=new Stack();




        background = new Texture("h/Menu/vheart_save_Switch.png");
        rootTable.setBackground(
            new TextureRegionDrawable(
                new TextureRegion(background)
            )
        );

        Table saveListWrapper=new Table();
        Table saveList=new Table();

        for (int i=0;i<4;i++){
            SaveCard saveCard=new SaveCard(new GameSave("save",5));
            saveList.add(saveCard).growX().row();
        }

        saveListWrapper.add(saveList).size( 300,200);
        stack.add(saveListWrapper);

        Table backBtnWrapper=new Table();
        backBtnWrapper.bottom().left().pad(10);
        TextButton backBtn=new TextButton("back",textButtonStyle);
        backBtnWrapper.add(backBtn).width(100);
        stack.add(backBtnWrapper);


        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                UiManager.setScreen(new MainMenuScreen());
            }
        });
        rootTable.add(stack).grow();
        stage.setDebugAll(true);

    }

    @Override
    public void render(float delta) {


        stage.act(delta);
        stage.draw();
    }
}
