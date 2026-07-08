package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Manager.AchievementManager;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.model.enums.Achievement;

public class AchievementsScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Table container = new Table();
        container.center().pad(20);

        Label titleLabel = new Label("Achievements", labelStyle);
        container.add(titleLabel).colspan(2).padBottom(30).row();


        for (Achievement achievement : Achievement.values()) {
            boolean isUnlocked = AchievementManager.isUnlocked(achievement);

            Image icon = new Image(GameAssetManager.achievementIcons.get(achievement));

            Label nameLabel = new Label(achievement.getTitle(), labelStyle);
            nameLabel.setFontScale(0.8f);
            Label descLabel = new Label(achievement.getDescription(), labelStyle);
            descLabel.setFontScale(0.5f);

            // Apply visual changes based on state
            if (isUnlocked) {
                icon.setColor(Color.GOLD);
                nameLabel.setColor(Color.WHITE);
                descLabel.setColor(Color.LIGHT_GRAY);
            } else {
                icon.setColor(Color.DARK_GRAY);
                icon.getColor().a = 0.4f; // Dimmed transparency

                nameLabel.setColor(Color.GRAY);
                descLabel.setColor(Color.DARK_GRAY);
                nameLabel.setText("???"); // Hide locked names if desired
            }

            Table textTable = new Table();
            textTable.left();
            textTable.add(nameLabel).left().row();
            textTable.add(descLabel).left();

            container.add(icon).size(64, 64).pad(10);
            container.add(textTable).growX().pad(10).row();
        }

        TextButton backBtn = new TextButton("Back", textButtonStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        container.add(backBtn).colspan(2).padTop(40);
        rootTable.add(container).grow();
    }
}
