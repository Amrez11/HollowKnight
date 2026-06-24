package io.github.some_example_name.fir.controller.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.fir.controller.ChoosingMenuC;
import io.github.some_example_name.fir.controller.model.enums.MenuTypes;

public class ChoosingMenuV extends ScreenAdapter implements IMenuV {

    private final Game game;
    private final ChoosingMenuC controller;
    private int numberOfSaves = 3;
    private Texture background;
    private Stage stage;

    public ChoosingMenuV(Game game) {
        this.game       = game;
        this.controller = new ChoosingMenuC(game);
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        background = new Texture("h/Menu/vheart_save_Switch.png");
        Image backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        Table rootTable = new Table();
        rootTable.defaults().space(20);
        rootTable.setFillParent(true);
        rootTable.pad(10);
        rootTable.top();
        rootTable.left();

        Skin skin = new Skin(Gdx.files.internal("Untitled.json"));
        BitmapFont font = skin.getFont("TrajanPro-Regular");

        Label topLabel = new Label("choose your path", skin);
        rootTable.add(topLabel).colspan(2).center().row();

        Texture buttonBgNew = new Texture("h/AreaSaveArt/Area_ForgottenCrossroads.png");
        Texture buttonBg1   = new Texture("h/AreaSaveArt/Area_Green_Path.png");
        Texture buttonBg2   = new Texture("h/AreaSaveArt/Area_Art_City_of_Tears.png");
        Texture buttonBg3   = new Texture("h/AreaSaveArt/Area_Deepnest.png");
        Texture buttonBg4   = new Texture("h/AreaSaveArt/Area_Hive.png");

        Array<Texture> backPhoto = new Array<>();
        backPhoto.add(buttonBgNew);
        backPhoto.add(buttonBg1);
        backPhoto.add(buttonBg2);
        backPhoto.add(buttonBg3);
        backPhoto.add(buttonBg4);

        TextButton.TextButtonStyle baseStyle = new TextButton.TextButtonStyle();
        baseStyle.font          = font;
        baseStyle.fontColor     = Color.WHITE;
        baseStyle.overFontColor = Color.GOLD;

        Table entriesTable = new Table();
        entriesTable.defaults().space(10);

        if (numberOfSaves == 0) {
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(baseStyle);
            style.up = new TextureRegionDrawable(backPhoto.get(0));
            TextButton newGameBtn = new TextButton("New Game?", style);
            newGameBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    controller.startNewGame();
                }
            });
            entriesTable.add(newGameBtn).width(250).height(40).left().colspan(2);
            entriesTable.row();

        } else {
            String[] labels = {"load first path", "load second path", "load third path", "load fourth path"};

            for (int i = 0; i < numberOfSaves; i++) {
                final int slot = i;

                TextButton.TextButtonStyle loadStyle = new TextButton.TextButtonStyle(baseStyle);
                loadStyle.up = new TextureRegionDrawable(backPhoto.get(i));
                TextButton loadBtn = new TextButton(labels[i], loadStyle);
                loadBtn.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        controller.loadSlot(slot);
                    }
                });

                TextButton.TextButtonStyle deleteStyle = new TextButton.TextButtonStyle(baseStyle);
                deleteStyle.fontColor     = Color.RED;
                deleteStyle.overFontColor = Color.SALMON;
                TextButton deleteBtn = new TextButton("X", deleteStyle);
                deleteBtn.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        controller.deleteSlot(slot);
                    }
                });

                entriesTable.add(loadBtn).width(500).height(70).left();
                entriesTable.add(deleteBtn).width(50).height(70).right();
                entriesTable.row();
            }

            if (numberOfSaves < 4) {
                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(baseStyle);
                style.up = new TextureRegionDrawable(backPhoto.get(0));
                TextButton newGameBtn = new TextButton("New Game?", style);
                newGameBtn.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        controller.startNewGame();
                    }
                });
                entriesTable.add(newGameBtn).width(600).height(70).colspan(2);
                entriesTable.row();
            }
        }

        // --- Back Button ---
        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle(baseStyle);
        backStyle.fontColor     = Color.LIGHT_GRAY;
        backStyle.overFontColor = Color.WHITE;
        TextButton backBtn = new TextButton("< Back", backStyle);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                controller.goBack();
            }
        });
        entriesTable.add(backBtn).width(150).height(50).left().colspan(2).padTop(30);
        entriesTable.row();

        rootTable.add(entriesTable).padLeft(20).left();
        stage.addActor(rootTable);
        stage.setDebugAll(false);
    }

    @Override public MenuTypes getMenuType() { return null; }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h); }
    @Override public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        stage.act();
        stage.draw();
    }
    @Override public void dispose() { }
}
