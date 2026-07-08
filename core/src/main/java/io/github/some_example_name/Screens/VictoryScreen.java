package io.github.some_example_name.Screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.Manager.AchievementManager;
import io.github.some_example_name.Manager.UiManager;
import io.github.some_example_name.model.Game;

public class VictoryScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Table container = new Table();
        container.center().pad(20);

        Label titleLabel = new Label("VICTORY ACHIEVED", labelStyle);
        titleLabel.setFontScale(1.2f);
        titleLabel.setColor(Color.GOLD);


        int totalSeconds = (int) AchievementManager.getGameTimer();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        Label timeLabel = new Label("Total Time: " + timeString, labelStyle);
        Label enemiesLabel = new Label("Enemies Killed: " + AchievementManager.getEnemiesKilledCount(), labelStyle);
        Label deathsLabel = new Label("Player Deaths: " + AchievementManager.getPlayerDeathCount(), labelStyle);

        timeLabel.setFontScale(0.8f);
        enemiesLabel.setFontScale(0.8f);
        deathsLabel.setFontScale(0.8f);

        TextButton restartBtn = new TextButton("Restart Game", textButtonStyle);
        TextButton menuBtn = new TextButton("Main Menu", textButtonStyle);

        // Restart logic: clear stats and load a fresh GameScreen
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AchievementManager.resetSession();
                UiManager.setScreen(new GameScreen(new Game()));
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        // Layout the UI
        container.add(titleLabel).colspan(2).padBottom(40).row();
        container.add(timeLabel).colspan(2).padBottom(15).row();
        container.add(enemiesLabel).colspan(2).padBottom(15).row();
        container.add(deathsLabel).colspan(2).padBottom(50).row();

        container.add(restartBtn).width(250).padRight(20);
        container.add(menuBtn).width(250);

        rootTable.add(container).grow();
    }
}
