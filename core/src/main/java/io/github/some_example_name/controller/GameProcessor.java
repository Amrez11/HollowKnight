package io.github.some_example_name.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Manager.SaveManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.Screens.MainMenuScreen;
import io.github.some_example_name.model.Game;
import io.github.some_example_name.model.costumActors.CharmMenuModal;
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
 *  I      – inventory (charms)
 *  F      – interact / talk (Zote)
 *  ENTER  – advance dialogue
 *  ESCAPE – pause
 * ────────────────────────────────────────
 */
public class GameProcessor implements InputProcessor {

    private final Game game;
    private final String slotId;
    private CharmMenuModal charmMenu;

    public GameProcessor(Game game, String slotId) {
        this.game = game;
        this.slotId = slotId;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.F -> {
                if (game.isNearZote() && !game.isZoteDialogueActive()) {
                    game.interactWithZote();
                }
            }
            case Input.Keys.ENTER -> {
                if (game.isZoteDialogueActive()) {
                    game.advanceZoteDialogue();
                }
            }
            case Input.Keys.ESCAPE -> {
                if (game.isZoteDialogueActive()) break; // don't pause mid-conversation
                game.setPaused(true);                                    // ← new
                PauseModel pauseModel = new PauseModel() {
                    @Override public void onResume() { game.setPaused(false); this.hide(); }   // ← added setPaused(false)
                    @Override public void onExit()   { UiManager.setScreen(new MainMenuScreen()); }
                    @Override public void onSave() {
                        SaveManager.save(game, slotId, slotId);
                        this.hide();
                    }
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
            case Input.Keys.I     -> toggleCharmMenu();
            case Input.Keys.O     -> game.getPlayer().getVelocity().y=1200;
            case Input.Keys.K     -> game.getPlayer().setPosition(new Vector2(7000,5700));
        }
        return false;
    }

    private void toggleCharmMenu() {
        if (charmMenu == null) {
            game.setPaused(true);
            charmMenu = new CharmMenuModal() {
                @Override public void onClose() {
                    game.setPaused(false);
                    charmMenu = null;
                }
            };
            charmMenu.show();
        } else {
            charmMenu.hide();
        }
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
            case Input.Keys.O     -> game.getPlayer().getVelocity().y=5;
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
