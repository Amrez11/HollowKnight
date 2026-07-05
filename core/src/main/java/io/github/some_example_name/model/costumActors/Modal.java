package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.Manager.UiManager;

public class Modal extends Table {
    protected Skin skin;
    private  Table wrapperTable;
    protected TextButton.TextButtonStyle textButtonStyle;
    public Modal() {
        skin= GameAssetManager.skin;
        textButtonStyle=GameAssetManager.textButtonStyle;


        wrapperTable=new Table();
        wrapperTable.setTouchable(Touchable.enabled);
        setTouchable(Touchable.enabled);

        wrapperTable.add(this);
        wrapperTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget()==wrapperTable){
                    hide();
                }
            }
        });
        wrapperTable.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }
        });


        pad(10);
    }

    public void show(){
        UiManager.getScreen().getModalStack().add(wrapperTable );
        wrapperTable.getStage().setKeyboardFocus(wrapperTable);
    }
    public void hide(){
         wrapperTable.remove();
    }
}
