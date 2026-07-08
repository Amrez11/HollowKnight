package io.github.some_example_name.model.enums;

public enum Achievement {
    // Update the 3rd parameter with your actual image file paths!
    FIRST_BLOOD("First Blood", "Defeat your first enemy.", "h/Achievements/achievement_fast_completionist.png"),
    DEFEAT_FALSE_KNIGHT("False Knight", "Conquer the False Knight boss.", "h/Achievements/achievement_fast_completionist.png"),
    TRUE_HUNTER("True Hunter", "Defeat every type of enemy.", "h/Achievements/achievement_fast_completionist.png"),
    COMPLETION("Completion", "Successfully finish the game.", "h/Achievements/achievement_fast_completionist.png"),
    SPEEDRUN("Speedrun", "Finish the game in under 5 minutes.", "h/Achievements/achievement_fast_completionist.png");

    private final String title;
    private final String description;
    private final String imagePath; // <-- Added image path

    Achievement(String title, String description, String imagePath) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
}
