package io.github.some_example_name.model.enums;


public enum MusicType {
    ROOM_1            ("03. Crossroads.mp3",       0.4f),
    ROOM_2            ("09. City of Tears.mp3",        0.4f),
    ROOM_3            ("26. Hollow Knight.mp3",        0.4f),
    BOSS_ROOM_AMBIENT ("Hollow Knight Audio Files/S61-161 Suspence 1.wav", 0.35f),
    BOSS_THEME        ("26. Hollow Knight.mp3",    0.5f),
    MAIN_MENU         ("26. Hollow Knight.mp3",     0.4f),
    VICTORY           ("14. Furious Gods.mp3",      0.5f);

    public final String path;
    public final float  volume;

    MusicType(String path, float volume) {
        this.path   = path;
        this.volume = volume;
    }
}
