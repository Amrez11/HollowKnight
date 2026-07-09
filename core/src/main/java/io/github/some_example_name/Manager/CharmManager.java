package io.github.some_example_name.Manager;

import com.badlogic.gdx.utils.ObjectSet;
import io.github.some_example_name.model.enums.Charm;

/**
 * Tracks which charms are currently equipped and exposes the multipliers
 * gameplay systems (attack, movement, special ability, damage resolver)
 * read from every frame. Static, same pattern as GameAssetManager/UiManager.
 */
public class CharmManager {
    public static final int MAX_NOTCHES = 3;

    private static final ObjectSet<Charm> equipped = new ObjectSet<>();

    private CharmManager() {}

    public static boolean isEquipped(Charm charm) {
        return equipped.contains(charm);
    }

    public static int getEquippedCount() {
        return equipped.size;
    }

    public static boolean hasFreeNotch() {
        return equipped.size < MAX_NOTCHES;
    }

    /**
     * Toggles a charm on/off.
     * @return true if the charm's equipped state changed, false if it was
     *         rejected (e.g. no free notches left).
     */
    public static boolean toggle(Charm charm) {
        if (equipped.contains(charm)) {
            equipped.remove(charm);
            return true;
        }
        if (!hasFreeNotch()) {
            return false;
        }
        equipped.add(charm);
        return true;
    }

    // ── Save/Load ──────────────────────────────────────────────────────────
    /** Unequips everything. Used before restoring a saved loadout. */
    public static void clearAll() {
        equipped.clear();
    }

    /**
     * Equips a charm without the free-notch check, bypassing MAX_NOTCHES.
     * Only meant for restoring a save that was already valid when written.
     */
    public static void equipDirect(Charm charm) {
        equipped.add(charm);
    }

    // ── Effect multipliers, read by gameplay systems ────────────────────────

    public static float getSoulMultiplier() {
        return isEquipped(Charm.SOUL_CATCHER) ? 2f : 1f;
    }

    public static float getDashDurationMultiplier() {
        return isEquipped(Charm.DASHMASTER) ? 0.75f : 1f;
    }

    public static float getNailDamageMultiplier() {
        return isEquipped(Charm.UNBREAKABLE_STRENGTH) ? 2f : 1f;
    }

    /** Tune this to taste — how much faster the slash animation/window becomes. */
    public static float getNailSlashDurationMultiplier() {
        return isEquipped(Charm.QUICK_SLASH) ? 0.6f : 1f;
    }

    public static float getFocusDurationMultiplier() {
        return isEquipped(Charm.QUICK_FOCUS) ? 0.5f : 1f;
    }
}
