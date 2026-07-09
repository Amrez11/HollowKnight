package io.github.some_example_name.Manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import io.github.some_example_name.SaveInfo.GameSaveData;
import io.github.some_example_name.model.Game;

/**
 * Persists/loads GameSaveData as plain JSON files under saves/, using
 * LibGDX's built-in reflection-based Json (de)serializer. Same static-manager
 * pattern as GameAssetManager/UiManager/CharmManager.
 */
public class SaveManager {
    private static final String SAVE_DIR = "saves/";
    public  static final int    SAVE_SLOTS = 4;

    private static final Json json = new Json();
    static {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
    }

    private SaveManager() {}

    private static FileHandle fileFor(String slotId) {
        return Gdx.files.local(SAVE_DIR + slotId + ".json");
    }

    /** Captures the full state of {@code game} and writes it to {@code slotId}. */
    public static void save(Game game, String slotId, String saveName) {
        GameSaveData data = game.captureSave(saveName);
        FileHandle file = fileFor(slotId);
        file.writeString(json.prettyPrint(data), false);
    }

    /** Returns the save stored in {@code slotId}, or null if that slot is empty/unreadable. */
    public static GameSaveData load(String slotId) {
        FileHandle file = fileFor(slotId);
        if (!file.exists()) return null;
        try {
            return json.fromJson(GameSaveData.class, file);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load save " + slotId, e);
            return null;
        }
    }

    public static boolean exists(String slotId) {
        return fileFor(slotId).exists();
    }

    public static void delete(String slotId) {
        FileHandle file = fileFor(slotId);
        if (file.exists()) file.delete();
    }
}
