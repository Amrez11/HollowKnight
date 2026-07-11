package io.github.some_example_name.model.enums;

/**
 * A charm the player can equip from the inventory menu.
 * displayName / description are shown in the UI; iconPath points at the
 * charm's icon under assets/ (drop your charm photos in assets/charms/).
 */
public enum Charm {
    SOUL_CATCHER(
        "Soul Catcher",
        "After every successful nail hit, double the soul gained.",
        "h/Inventory & UI/Charms/Soul Eater - charm_soul_up_large.png"
    ),
    DASHMASTER(
        "Dashmaster",
        "Cuts the time it takes to dash down to three quarters.",
        "h/Inventory & UI/Charms/Dashmaster - _0011_charm_generic_03.png"
    ),
    UNBREAKABLE_STRENGTH(
        "Unbreakable Strength",
        "Doubles the damage dealt by the nail.",
        "h/Inventory & UI/Charms/Dreamshield - charm_grimm_markoth_shield.png"
    ),
    QUICK_SLASH(
        "Quick Slash",
        "Speeds up the nail's slash.",
        "h/Inventory & UI/Charms/Fragile Heart - _0002_charm_glass_heal.png"
    ),
    QUICK_FOCUS(
        "Quick Focus",
        "Halves the time it takes to cast Focus.",
        "h/Inventory & UI/Charms/Thorns of Agony - _0000_charm_thorn_counter.png"
    );

    public final String displayName;
    public final String description;
    public final String iconPath;

    Charm(String displayName, String description, String iconPath) {
        this.displayName = displayName;
        this.description = description;
        this.iconPath = iconPath;
    }
}
