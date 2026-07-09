package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.Manager.SaveManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.SaveInfo.GameSaveData;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.Screens.GameScreen;
import io.github.some_example_name.Screens.LoadMenuScreen;
import io.github.some_example_name.model.Game;

/**
 * One row in the Load menu, bound to a specific save slot ("slot0"..).
 * If SaveManager has a save for that slot, shows its name/playtime with
 * Load/Delete. If the slot is empty, shows "empty" with a New Game button
 * instead so the same 4-slot list doubles as the new-game picker.
 */
public class SaveCard extends Table {
    private final String slotId;
    private final GameSaveData gameSave;

    public SaveCard(String slotId, GameSaveData gameSave) {
        this.slotId = slotId;
        this.gameSave = gameSave;
        Skin skin = GameAssetManager.skin;
        TextButton.TextButtonStyle textButtonStyle = GameAssetManager.textButtonStyle;
        Label.LabelStyle labelStyle = GameAssetManager.labelStyle;
        skin.setScale(1 / 2f);

        boolean empty = gameSave == null;

        defaults().space(15);
        pad(0);
        Label nameLabel = new Label(empty ? slotId + " - empty" : gameSave.saveName, labelStyle);
        Label progressLabel = new Label(empty ? "" : gameSave.getPlayTimeDisplay(), labelStyle);
        TextButton actionButton = new TextButton(empty ? "new game" : "load", textButtonStyle);
        TextButton deleteButton = new TextButton("delete", textButtonStyle);

        add(nameLabel);
        add(progressLabel);
        add(actionButton).expandX().right();
        add(deleteButton).expandX();

        deleteButton.setVisible(!empty);

        actionButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game game = new Game();
                UiManager.setScreen(new GameScreen(game, slotId, gameSave));
            }
        });
        deleteButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameSave == null) return;
                SaveManager.delete(slotId);
                UiManager.setScreen(new LoadMenuScreen());
            }
        });
    }
}
