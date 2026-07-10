package io.github.some_example_name.model.entity.npc;


public class ZoteDialogue {

    private static final String[] INTRO_LINES = {
        "Hah! You there, worm - feast your eyes on the mighty Zote!",
        "None have bested me, none EVER shall, for I am the greatest warrior this kingdom has seen!",
        "Go on then, run along. Zote has no more time for the small and the weak."
    };

    private static final String[] PRECEPTS = {
        "Precept: Never fall in love.",
        "Precept: Never let an ally best you in a challenge.",
        "Precept: Always be the hero.",
        "Precept: Never stand near a fool, lest you fall with them.",
        "Precept: Never let your guard down before a lesser foe.",
        "Precept: Always claim the glory, even when it was not yours to claim."
    };

    private int     introIndex     = 0;
    private boolean introExhausted = false;
    private int     preceptIndex   = 0;

    /** True once all three intro lines have been shown at least once. */
    public boolean isIntroExhausted() { return introExhausted; }

    /** Called when a brand-new conversation opens (fresh press of the interact key). */
    public String startConversation() {
        if (!introExhausted) {
            introIndex = 0;
            return INTRO_LINES[introIndex];
        }
        return nextPrecept();
    }

    /**
     * Called on Enter once the current line has fully finished revealing.
     * @return the next line to display, or null if the conversation should close.
     */
    public String advance() {
        if (!introExhausted) {
            if (introIndex < INTRO_LINES.length - 1) {
                introIndex++;
                return INTRO_LINES[introIndex];
            }
            // Just showed the third and final intro line — conversation ends here,
            // and every future conversation switches to Precept mode.
            introExhausted = true;
            return null;
        }
        // Precept mode is always exactly one sentence per interaction.
        return null;
    }

    private String nextPrecept() {
        String line = PRECEPTS[preceptIndex % PRECEPTS.length];
        preceptIndex++;
        return line;
    }
}
