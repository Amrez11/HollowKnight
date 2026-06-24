package io.github.some_example_name.fir.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Gdx;
import io.github.some_example_name.fir.controller.view.ChoosingMenuV;
import io.github.some_example_name.fir.controller.view.StartGameV;
import io.github.some_example_name.fir.controller.view.GameMenuV;

public class ChoosingMenuC {

    final private Game game;
    final private Preferences prefs;

    public ChoosingMenuC(Game game) {
        this.game  = game;
        this.prefs = Gdx.app.getPreferences("HollowKnightSaves");
    }

    public void loadSlot(int slot) {
        game.setScreen(new GameMenuV(game));
    }

    public void deleteSlot(int slot) {
        prefs.remove("slot_" + slot + "_exists");
        prefs.flush();
        game.setScreen(new ChoosingMenuV(game));
    }

    public void startNewGame() {
        int newSlot = -1;
        for (int i = 0; i < 4; i++) {
            if (!prefs.getBoolean("slot_" + i + "_exists", false)) {
                newSlot = i;
                break;
            }
        }
        if (newSlot < 0) return;
        prefs.putBoolean("slot_" + newSlot + "_exists", true);
        prefs.flush();
        game.setScreen(new GameMenuV(game));
    }

    public void goBack() {
        game.setScreen(new StartGameV(game));
    }
}
