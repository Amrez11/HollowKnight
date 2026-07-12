package io.github.some_example_name.Manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.Screens.AbstractScreen;
import io.github.some_example_name.model.enums.Achievement;

import java.util.HashSet;
import java.util.Set;

public class AchievementManager {
    private static final Set<Achievement> unlockedAchievements = new HashSet<>();
    private static final Set<String> enemiesDefeated = new HashSet<>();

    private static final String PREFS_NAME = "hollow-knight-achievements";
    private static Preferences prefs;

    private static Preferences prefs() {
        if (prefs == null) prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs;
    }

    /** Call once at startup (after Gdx.app exists) to restore prior unlocks. */
    public static void loadPersisted() {
        for (Achievement a : Achievement.values()) {
            if (prefs().getBoolean(a.name(), false)) {
                unlockedAchievements.add(a);
            }
        }
    }

    private static float gameTimer = 0f;
    private static final float SPEEDRUN_LIMIT = 300f; // 5 minutes in seconds
    private static final int TOTAL_ENEMY_TYPES = 5;
    private static int enemiesKilledCount = 0;
    private static int playerDeathCount = 0;

    // --- Tracking Logic ---
    public static void updateTimer(float delta) {
        gameTimer += delta;
    }

    public static void resetSession() {
        gameTimer = 0f;
        enemiesDefeated.clear();
        enemiesKilledCount = 0;
        playerDeathCount = 0;
    }
    public static void onPlayerDied() {
        playerDeathCount++;
    }
    public static void onEnemyDefeated(String enemyType) {
        enemiesKilledCount++;
        unlock(Achievement.FIRST_BLOOD);

        if (enemyType.equals("BOSS")) {
            unlock(Achievement.DEFEAT_FALSE_KNIGHT);
        } else {
            enemiesDefeated.add(enemyType);
            if (enemiesDefeated.size() >= TOTAL_ENEMY_TYPES) {
                unlock(Achievement.TRUE_HUNTER);
            }
        }
    }

    public static void onGameCompleted() {
        unlock(Achievement.COMPLETION);
        if (gameTimer <= SPEEDRUN_LIMIT) {
            unlock(Achievement.SPEEDRUN);
        }
    }

    // --- Core Unlock & UI Event System ---
    public static boolean isUnlocked(Achievement achievement) {
        return unlockedAchievements.contains(achievement);
    }

    private static void unlock(Achievement achievement) {
        if (!unlockedAchievements.contains(achievement)) {
            unlockedAchievements.add(achievement);
            prefs().putBoolean(achievement.name(), true);
            prefs().flush();
            showNotification(achievement);
        }
    }

    // Event-driven popup (Toast) logic
    private static void showNotification(Achievement achievement) {
        AbstractScreen currentScreen = UiManager.getScreen();
        if (currentScreen != null && currentScreen.getToastStack() != null) {

            Table toast = new Table();
            toast.setBackground(GameAssetManager.skin.getDrawable("window"));
            toast.setColor(0.1f, 0.1f, 0.1f, 0.9f); // Dark background for the toast

            Label title = new Label("Achievement Unlocked!", GameAssetManager.labelStyle);
            title.setFontScale(0.5f);
            Label name = new Label(achievement.getTitle(), GameAssetManager.labelStyle);
            name.setFontScale(0.7f);

            toast.add(title).pad(5).row();
            toast.add(name).pad(5).row();

            // Notification Animation: Fade In -> Wait -> Fade Out -> Remove
            toast.getColor().a = 0f;
            toast.addAction(Actions.sequence(
                Actions.fadeIn(0.5f, Interpolation.fade),
                Actions.delay(3f),
                Actions.fadeOut(0.5f, Interpolation.fade),
                Actions.removeActor()
            ));

            currentScreen.getToastStack().add(toast);
        }
    }
    public static int getEnemiesKilledCount() { return enemiesKilledCount; }
    public static int getPlayerDeathCount() { return playerDeathCount; }
    public static float getGameTimer() { return gameTimer; }

    // ── Save/Load ──────────────────────────────────────────────────────────
    public static Set<Achievement> getUnlockedAchievements() { return unlockedAchievements; }
    public static Set<String> getEnemiesDefeatedTypes()      { return enemiesDefeated; }

    /**
     * Restores progress tracking from a save file, replacing (not merging)
     * the current session's state. Does not re-fire unlock notifications.
     */
    public static void restoreState(Set<Achievement> unlocked, Set<String> enemyTypesDefeated,
                                    int killedCount, int deathCount, float timer) {
        // [FIXED] Union with (rather than replace by) whatever's already unlocked
        // globally, so loading an older/different save can never make an
        // already-earned achievement look locked again.
        unlockedAchievements.addAll(unlocked);
        for (Achievement a : unlocked) {
            if (!prefs().getBoolean(a.name(), false)) {
                prefs().putBoolean(a.name(), true);
            }
        }
        prefs().flush();
        enemiesDefeated.clear();
        enemiesDefeated.addAll(enemyTypesDefeated);
        enemiesKilledCount = killedCount;
        playerDeathCount   = deathCount;
        gameTimer          = timer;
    }
}
