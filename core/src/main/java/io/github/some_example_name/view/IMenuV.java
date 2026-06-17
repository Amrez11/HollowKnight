package io.github.some_example_name.view;

import com.badlogic.gdx.Screen;
import io.github.some_example_name.model.enums.MenuTypes;

public interface IMenuV extends Screen {
    public MenuTypes getMenuType();
}
