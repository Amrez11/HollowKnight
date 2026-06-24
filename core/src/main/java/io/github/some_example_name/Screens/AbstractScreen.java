package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.Manager.GameAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static javax.swing.text.StyleConstants.Background;

public abstract  class AbstractScreen  implements Screen {
    protected Stage stage;
    protected Table rootTable;
    private Stack modalStack;
    private Stack toastStack;
    private Stack mainStack;
    protected  Skin skin;
    private Texture backgroundTexture;
    @Override
    public void show() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(1f);
        stage = new Stage(viewport);
        skin=GameAssetManager.skin;

        mainStack=new Stack();
        modalStack=new Stack();
        toastStack=new Stack();
        rootTable=new Table();



        mainStack.setFillParent(true);

        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);


        stage.addActor(mainStack);
        Gdx.input.setInputProcessor(stage);
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
     public Stack getModalStack(){
        return modalStack;
    }


}
