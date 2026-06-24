package io.github.some_example_name.fir.controller;

import com.badlogic.gdx.Game;
import io.github.some_example_name.fir.controller.view.ChoosingMenuV;
import io.github.some_example_name.fir.controller.view.SettingsMenuV;

public class StartGameC {

    final private Game game;

    public StartGameC(Game game) {
        this.game = game;
    }

    public void startGame() {
        game.setScreen(new ChoosingMenuV(game));
    }

    public void openSettings() {
        game.setScreen(new SettingsMenuV(game));
    }
}
