package io.github.some_example_name.SaveInfo;

import java.util.ArrayList;

/**
 * Plain JSON-serializable snapshot of an entire run: player transform and
 * stats, equipped charms, achievement/progress tracking, and every enemy's
 * type/position/hp/alive-state. Written and read by SaveManager using
 * LibGDX's built-in Json class — every field here has to stay public with
 * no-arg-constructible types so the reflection-based (de)serializer can see it.
 */
public class GameSaveData {
    public String saveName = "save";
    public long   timestamp;
    public float  playTimeSeconds;

    // ── Player ──────────────────────────────────────────────────────────────
    public float   playerX;
    public float   playerY;
    public int     hp;
    public int     soul;
    public boolean lookingRight = true;

    // ── Charms ──────────────────────────────────────────────────────────────
    public ArrayList<String> equippedCharms = new ArrayList<>();

    // ── Progress / achievements ────────────────────────────────────────────
    public ArrayList<String> unlockedAchievements = new ArrayList<>();
    public ArrayList<String> enemyTypesDefeated   = new ArrayList<>();
    public int   enemiesKilledCount;
    public int   playerDeathCount;
    public float gameTimer;

    // ── Enemies ─────────────────────────────────────────────────────────────
    public ArrayList<EnemySaveData> enemies = new ArrayList<>();

    /** No-arg constructor required by LibGDX's reflection-based Json. */
    public GameSaveData() {}

    public int getHoursPlayed() {
        return (int) (playTimeSeconds / 3600f);
    }

    public String getPlayTimeDisplay() {
        int totalSeconds = (int) playTimeSeconds;
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
