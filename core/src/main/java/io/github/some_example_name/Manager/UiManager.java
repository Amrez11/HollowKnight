package io.github.some_example_name.Manager;

import com.badlogic.gdx.Screen;
import io.github.some_example_name.Main;
import io.github.some_example_name.Screens.AbstractScreen;

public class UiManager {
    private static Main main;
    public static void init(Main main){
         UiManager.main=main;
    }
    public static void setScreen(Screen screen){
        main.setScreen(screen );
    }
    public static AbstractScreen getScreen(){
        if (main.getScreen()  instanceof AbstractScreen abstractScreen){
            return abstractScreen;
        }
        return null;

    }
}
