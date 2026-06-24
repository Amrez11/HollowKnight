package io.github.some_example_name.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.Screens.MainMenuScreen;
import io.github.some_example_name.model.Game;
import io.github.some_example_name.model.costumActors.Modal;
import io.github.some_example_name.model.costumActors.PauseModel;

public class GameProcessor implements InputProcessor {

    private final Game game;

    public GameProcessor(Game game) {
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.ESCAPE -> {
                PauseModel pauseModel=new PauseModel(){
                    @Override
                    public void onResume() {
                        this.hide();
                    }

                    @Override
                    public void onExit() {
                        UiManager.setScreen(new MainMenuScreen());
                    }
                };
                pauseModel.show();

            }
            case Input.Keys.D -> game.getPlayer().setMovingRight(true);
            case Input.Keys.A -> game.getPlayer().setMovingLeft(true);
            case Input.Keys.SPACE -> game.getPlayer().setJump(true);


        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Input.Keys.D -> game.getPlayer().setMovingRight(false);
            case Input.Keys.A -> game.getPlayer().setMovingLeft(false);
            case Input.Keys.SPACE -> game.getPlayer().setJump(false);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
