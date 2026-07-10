package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.model.enums.Charm;

/**
 * In-game help screen: what every key does, what every ability/charm does,
 * and — separately and clearly labelled so it's obvious these aren't normal
 * controls — which keys trigger the debug/cheat codes.
 */
public class GuideModal extends Modal {

    private static final Texture PANEL_BG = new Texture("ui/stone-tablet-bg.png");

    public GuideModal() {
        super();
        setTouchable(Touchable.enabled);
        background(new TextureRegionDrawable(new TextureRegion(PANEL_BG)));
        pad(30);

        Label title = new Label("Guide", GameAssetManager.labelStyle);

        Table content = new Table();
        content.top();
        content.padLeft(15);  // Adds a visual margin away from the edge
        content.defaults().space(6);

        addSection(content, "Movement");
        addRow(content, "A", "Move left");
        addRow(content, "D", "Move right");
        addRow(content, "SPACE", "Jump");
        addRow(content, "W", "Dash");

        addSection(content, "Combat & Abilities");
        addRow(content, "X", "Nail attack (melee slash)");
        addRow(content, "Q", "Vengeful Spirit — ranged soul projectile");
        addRow(content, "R", "Howling Wraiths — soul nova attack");
        addRow(content, "E", "Focus — hold to spend soul and heal");

        addSection(content, "Menus & Interaction");
        addRow(content, "I", "Open charm inventory");
        addRow(content, "F", "Talk / interact");
        addRow(content, "ENTER", "Advance dialogue");
        addRow(content, "ESC", "Pause game");



        addSection(content, "Cheat Codes");



        addRow(content, "Ctrl + K", "Insta-Kill: Wipe all enemies on screen");
        addRow(content, "Ctrl + N", "Noclip: Disable collision & gravity (Fly)");
        addRow(content, "Ctrl + T", "Teleport: Jump to False Knight Boss Arena");
        addRow(content, "Ctrl + G", "God Mode: Complete Invincibility");
        addRow(content, "Ctrl + H", "Emergency Heal: Max Soul & Extra HP");

        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        TextButton backButton = new TextButton("Back", textButtonStyle);
        backButton.getLabel().setFontScale(0.45f);
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        defaults().space(14);
        add(title).padBottom(10).row();
        add(scrollPane).width(560).height(420).row();
        add(backButton).width(220).height(56).padTop(16);
    }

    private void addSection(Table content, String text) {
        Label label = new Label(text, GameAssetManager.labelStyle);
        label.setFontScale(0.55f);
        label.setColor(Color.GOLD);
        content.add(label).colspan(2).left().padLeft(20).padTop(14).padBottom(4).row();
    }

    private void addRow(Table content, String key, String description) {
        Label keyLabel = new Label(key, GameAssetManager.labelStyle);
        keyLabel.setFontScale(0.42f);
        keyLabel.setAlignment(Align.left);

        Label descLabel = new Label(description, GameAssetManager.labelStyle);
        descLabel.setFontScale(0.4f);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.left);

        content.add(keyLabel).width(110).left().padLeft(20).top();
        content.add(descLabel).width(400).left().padLeft(20).row();
    }
}
