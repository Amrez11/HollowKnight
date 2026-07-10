package io.github.some_example_name;

import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.Screens.MainMenuScreen;
import io.github.some_example_name.controller.SettingsMenuController;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game  {

    @Override
    public void create() {
        GameAssetManager.init();
        UiManager.init(this);
        new SettingsMenuController();
        MainMenuScreen mainMenuScreen= new MainMenuScreen();
        setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0,0,0,0);
        super.render();
    }
}
