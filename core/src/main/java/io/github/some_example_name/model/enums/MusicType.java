package io.github.some_example_name.model.enums;

/**
 * Looping background tracks. Kept separate from SoundType because libGDX
 * streams Music from disk instead of holding it fully in memory like Sound —
 * they need different loader/playback calls, so they get their own enum
 * rather than a "isMusic" flag bolted onto SoundType.
 *
 * ROOM_1 / ROOM_2 / ROOM_3 line up 1:1 with the first three entries GameScreen
 * already maps in `roomBackgrounds` (cameraBounds indices 0–2). BOSS_ROOM_AMBIENT
 * covers cameraBounds index 3 (the boss arena) before the fight starts;
 * BOSS_THEME takes over the moment GameScreen flips bossFightStarted.
 */
public enum MusicType {
    ROOM_1            ("516407_26hollow-knight-sfx/boss_final_hit.wav",       0.4f),
    ROOM_2            ("516407_26hollow-knight-sfx/boss_final_hit.wav",        0.4f),
    ROOM_3            ("516407_26hollow-knight-sfx/boss_final_hit.wav",        0.4f),
    BOSS_ROOM_AMBIENT ("516407_26hollow-knight-sfx/boss_final_hit.wav", 0.35f),
    BOSS_THEME        ("516407_26hollow-knight-sfx/boss_final_hit.wav",    0.5f),
    MAIN_MENU         ("516407_26hollow-knight-sfx/boss_final_hit.wav",     0.4f),
    VICTORY           ("516407_26hollow-knight-sfx/boss_final_hit.wav",      0.5f);

    public final String path;
    public final float  volume;

    MusicType(String path, float volume) {
        this.path   = path;
        this.volume = volume;
    }
}
