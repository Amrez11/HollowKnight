package io.github.some_example_name.model.enums;

/**
 * One-shot sound effects, loaded the same way AnimationType drives sprite
 * loading: GameAssetManager iterates SoundType.values() once at startup and
 * loads whatever's at `path` into a HashMap keyed by the enum constant.
 *
 * Unlike animations (which crash on a missing file — see loadAnimation),
 * sound loading is defensive per-clip, the same way the existing Zote growl
 * SFX are loaded: a missing file is logged and skipped, not fatal. Combat
 * feedback shouldn't be able to crash the whole game over a missing .ogg.
 *
 * `volume` is this clip's own mix level (0–1) *before* the player's SFX
 * slider is applied — GameAssetManager.playSound() multiplies the two.
 */
public enum SoundType {

    // ── Player — movement ───────────────────────────────────────────────────
    PLAYER_JUMP       ("516407_26hollow-knight-sfx/boss_final_hit.wav",       0.5f),
    PLAYER_DOUBLE_JUMP("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.5f),
    PLAYER_DASH       ("516407_26hollow-knight-sfx/boss_final_hit.wav",       0.55f),
    PLAYER_LAND       ("516407_26hollow-knight-sfx/boss_final_hit.wav",       0.4f),

    // ── Player — combat ──────────────────────────────────────────────────────
    PLAYER_NAIL_SWING ("516407_26hollow-knight-sfx/boss_final_hit.wav",  0.6f),
    PLAYER_SOUL_GAIN  ("516407_26hollow-knight-sfx/boss_final_hit.wav",   0.35f),
    PLAYER_HURT       ("516407_26hollow-knight-sfx/boss_final_hit.wav",       0.8f),
    PLAYER_DEATH      ("516407_26hollow-knight-sfx/boss_final_hit.wav",      1f),

    // ── Player — spells / focus ──────────────────────────────────────────────
    PLAYER_HOWLING_WRAITHS("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.7f),
    PLAYER_VENGEFUL_SPIRIT("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.7f),
    PLAYER_FOCUS_HEAL     ("516407_26hollow-knight-sfx/boss_final_hit.wav",      0.6f),

    // ── Enemies — shared ──────────────────────────────────────────────────────
    ENEMY_HIT("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.55f),

    // ── Enemies — per-type death, paired 1:1 with the *_DEAD AnimationTypes ──
    CRAWLER_DEATH    ("516407_26hollow-knight-sfx/boss_final_hit.wav",    0.7f),
    SENTRY_DEATH     ("516407_26hollow-knight-sfx/boss_final_hit.wav",     0.7f),
    FLYER_DEATH      ("516407_26hollow-knight-sfx/boss_final_hit.wav",      0.7f),
    LASER_FLYER_DEATH("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.7f),
    BOSS_DEATH       ("516407_26hollow-knight-sfx/boss_final_hit.wav",       1f),

    // ── UI ───────────────────────────────────────────────────────────────────
    UI_CLICK ("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.5f),
    UI_PAUSE ("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.5f);

    public final String path;
    public final float  volume;

    SoundType(String path, float volume) {
        this.path   = path;
        this.volume = volume;
    }
}
