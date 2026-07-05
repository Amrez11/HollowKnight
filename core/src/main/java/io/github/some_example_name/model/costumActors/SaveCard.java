package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.SaveInfo.GameSave;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.Screens.GameScreen;
import io.github.some_example_name.model.Game;

public class SaveCard extends Table {
    private final GameSave gameSave;

    public SaveCard(GameSave gameSave) {
        this.gameSave = gameSave;
        Skin skin=GameAssetManager.skin;
        TextButton.TextButtonStyle textButtonStyle =GameAssetManager.textButtonStyle;
        Label.LabelStyle labelStyle=GameAssetManager.labelStyle;
        skin.setScale(1/2f);

        defaults().space(15);
        pad(0);
        Label nameLabel= new Label(gameSave.saveName(),labelStyle);
        Label progressLabel=new Label(Integer.toString(gameSave.hoursPlayed())+"hours",labelStyle);
        TextButton loadButten=new TextButton("load",textButtonStyle);
        TextButton deleteButten=new TextButton("delete",textButtonStyle);


        add(nameLabel);
        add(progressLabel);
        add(loadButten).expandX().right();
        add(deleteButten).expandX();

        loadButten.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game game=new Game();
                UiManager.setScreen(new GameScreen(game));
            }
        });
        deleteButten.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });
    }
}
