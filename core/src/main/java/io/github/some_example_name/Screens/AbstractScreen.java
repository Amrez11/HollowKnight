package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.model.costumActors.BrightnessOverlay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static javax.swing.text.StyleConstants.Background;

public abstract  class AbstractScreen  implements Screen {
    protected Stage stage;
    protected Table rootTable;
    private Stack modalStack;
    private Stack toastStack;
    private Stack mainStack;
    protected  Skin skin;
    protected Label.LabelStyle labelStyle;
    protected TextButton.TextButtonStyle textButtonStyle;

    // ── Custom cursor (drawn, not OS hardware cursor) ───────────────────────
    // [FIXED] Gdx.graphics.newCursor()/setCursor() swapping the OS pointer
    // turned out unreliable — several GPU drivers/OSes cap or just ignore
    // custom hardware cursors with no error at all, which is why the earlier
    // fix (shrinking the image) still didn't show anything for some setups.
    // Drawing our own sprite at the mouse position instead always works,
    // since it's just a normal texture draw — no OS/driver cursor support
    // needed. Lives here, not in GameScreen, so every screen gets it for free.
    private static SpriteBatch cursorBatch;
    private static OrthographicCamera cursorCamera;

    private void drawCustomCursor() {
        Texture tex = GameAssetManager.cursorTexture;
        if (tex == null) return;

        if (cursorBatch == null) {
            cursorBatch  = new SpriteBatch();
            cursorCamera = new OrthographicCamera();
        }
        cursorCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cursorCamera.update();

        float size = 32f; // on-screen cursor size in pixels, independent of the source art's resolution
        float x = Gdx.input.getX();
        float y = Gdx.graphics.getHeight() - Gdx.input.getY(); // input Y is top-down, draw Y is bottom-up

        cursorBatch.setProjectionMatrix(cursorCamera.combined);
        cursorBatch.begin();
        cursorBatch.draw(tex, x, y - size, size, size);
        cursorBatch.end();
    }

    @Override
    public void show() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(1f);
        stage = new Stage(viewport);
        skin=GameAssetManager.skin;
        textButtonStyle=GameAssetManager.textButtonStyle;
        labelStyle=GameAssetManager.labelStyle;


        mainStack=new Stack();
        modalStack=new Stack();
        toastStack=new Stack();
        rootTable=new Table();



        mainStack.setFillParent(true);


        mainStack.add(rootTable);
        mainStack.add(new BrightnessOverlay());
        mainStack.add(modalStack);
        mainStack.add(toastStack);


        stage.addActor(mainStack);
        Gdx.input.setInputProcessor(stage);
    }
    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();
        drawCustomCursor();
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

    public Stack getToastStack() {
        return toastStack;
    }


}
