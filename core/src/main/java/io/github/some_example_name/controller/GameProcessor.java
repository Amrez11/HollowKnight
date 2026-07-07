package io.github.some_example_name.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.Screens.MainMenuScreen;
import io.github.some_example_name.model.Game;
import io.github.some_example_name.model.costumActors.PauseModel;

/**
 * Key bindings
 * ────────────────────────────────────────
 *  D      – move right
 *  A      – move left
 *  SPACE  – jump
 *  W      – dash
 *  E      – Focus (hold to heal)
 *  X      – Attack (nail swing)
 *  Q      – Vengeful Spirit (one-shot)
 *  R      – Howling Wraiths (one-shot)
 *  ESCAPE – pause
 * ────────────────────────────────────────
 */
public class GameProcessor implements InputProcessor {

    private final Game game;

    public GameProcessor(Game game) {
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE -> {
                PauseModel pauseModel = new PauseModel() {
                    @Override public void onResume() { this.hide(); }
                    @Override public void onExit()   { UiManager.setScreen(new MainMenuScreen()); }
                };
                pauseModel.show();
            }
            case Input.Keys.D     -> game.getPlayer().setMovingRight(true);
            case Input.Keys.A     -> game.getPlayer().setMovingLeft(true);
            case Input.Keys.SPACE -> game.getPlayer().setJumpPressed(true);
            case Input.Keys.W     -> game.getPlayer().setDashPressed(true);
            case Input.Keys.E     -> game.getPlayer().setFocus(true);
            case Input.Keys.X     -> game.getPlayer().setAttackPressed(true);
            case Input.Keys.Q     -> game.getPlayer().setVengefulSpirit(true);
            case Input.Keys.R     -> game.getPlayer().setHowlingWraith(true);
            case Input.Keys.J     ->game.getPlayer().setOnBoss(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.D     -> game.getPlayer().setMovingRight(false);
            case Input.Keys.A     -> game.getPlayer().setMovingLeft(false);
            case Input.Keys.SPACE -> game.getPlayer().setJumpPressed(false);
            case Input.Keys.W     -> game.getPlayer().setDashPressed(false);
            case Input.Keys.E     -> game.getPlayer().setFocus(false);
            case Input.Keys.X     -> game.getPlayer().setAttackPressed(false);
            case Input.Keys.J     ->game.getPlayer().setOnBoss(false);
            // Q, R: one-shot, no keyUp needed
        }
        return false;
    }

    @Override public boolean keyTyped(char c)                                              { return false; }
    @Override public boolean touchDown(int x, int y, int pointer, int button)             { return false; }
    @Override public boolean touchUp(int x, int y, int pointer, int button)               { return false; }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button)        { return false; }
    @Override public boolean touchDragged(int x, int y, int pointer)                      { return false; }
    @Override public boolean mouseMoved(int x, int y)                                     { return false; }
    @Override public boolean scrolled(float amountX, float amountY)                       { return false; }
}
