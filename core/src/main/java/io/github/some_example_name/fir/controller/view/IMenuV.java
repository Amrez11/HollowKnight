package io.github.some_example_name.fir.controller.view;

import com.badlogic.gdx.Screen;
import io.github.some_example_name.fir.controller.model.enums.MenuTypes;

public interface IMenuV extends Screen {
    public MenuTypes getMenuType();
}
