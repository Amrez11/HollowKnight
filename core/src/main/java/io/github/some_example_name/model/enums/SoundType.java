package io.github.some_example_name.model.enums;


public enum SoundType {

    // ── Player — movement ───────────────────────────────────────────────────
    PLAYER_JUMP       ("Hollow Knight Audio Files/hero_jump.wav",       0.5f),
    PLAYER_DOUBLE_JUMP("Hollow Knight Audio Files/hero_wall_jump.wav", 0.5f),
    PLAYER_DASH       ("516407_26hollow-knight-sfx/hero_dash.wav",       0.7f),
    PLAYER_LAND       ("Hollow Knight Audio Files/hero_land_hard.wav",       0.4f),

    // ── Player — combat ──────────────────────────────────────────────────────
    PLAYER_NAIL_SWING ("516407_26hollow-knight-sfx/hero_nail_art_charge_complete.wav",  0.6f),
    PLAYER_SOUL_GAIN  ("516407_26hollow-knight-sfx/soul_pickup_1.wav",   0.35f),
    PLAYER_HURT       ("516407_26hollow-knight-sfx/hero_damage.wav",       0.8f),
    PLAYER_DEATH      ("516407_26hollow-knight-sfx/boss_final_hit.wav",      1f),

    // ── Player — spells / focus ──────────────────────────────────────────────
    PLAYER_HOWLING_WRAITHS("Hollow Knight Audio Files/hero_void_scream_spell.wav", 0.7f),
    PLAYER_VENGEFUL_SPIRIT("Hollow Knight Audio Files/hero_scream_spell.wav", 0.7f),
    PLAYER_FOCUS_HEAL     ("516407_26hollow-knight-sfx/boss_final_hit.wav",      0.6f),

    // ── Enemies — shared ──────────────────────────────────────────────────────
    ENEMY_HIT("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.55f),

    // ── Enemies — per-type death, paired 1:1 with the *_DEAD AnimationTypes ──
    CRAWLER_DEATH    ("516407_26hollow-knight-sfx/Boss Defeat.wav",    0.7f),
    SENTRY_DEATH     ("516407_26hollow-knight-sfx/Boss Defeat.wav",     0.7f),
    FLYER_DEATH      ("516407_26hollow-knight-sfx/Boss Defeat.wav",      0.7f),
    LASER_FLYER_DEATH("516407_26hollow-knight-sfx/Boss Defeat.wav", 0.7f),
    BOSS_DEATH       ("516407_26hollow-knight-sfx/Boss Defeat.wav",       1f),

    // ── UI ───────────────────────────────────────────────────────────────────
    UI_CLICK ("Hollow Knight Audio Files/grass_cut_1.wav", 0.5f),
    UI_PAUSE ("Hollow Knight Audio Files/grass_cut_1.wav", 0.5f);

    public final String path;
    public final float  volume;

    SoundType(String path, float volume) {
        this.path   = path;
        this.volume = volume;
    }
}
