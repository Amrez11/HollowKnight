package io.github.some_example_name.model.entity.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * Handles the word-by-word text reveal and on-screen rendering for a single
 * line of NPC dialogue. Screen-space only — render with a batch/ShapeRenderer
 * whose projection matrix is already set to the HUD camera (see GameScreen).
 */
public class DialogueBox {

    private static final float WORD_INTERVAL = 0.12f; // seconds between revealed words
    private static final float MARGIN_BOTTOM = 60f;
    private static final float BOX_WIDTH_PCT = 0.7f;
    private static final float BOX_HEIGHT    = 130f;
    private static final float BOX_PADDING   = 24f;

    private String[] words            = new String[0];
    private int       visibleWordCount = 0;
    private float     revealTimer      = 0f;
    private boolean   active           = false;

    /** Starts showing a new line of text from scratch (fully hidden, then reveals word by word). */
    public void show(String text) {
        this.words            = text.trim().split("\\s+");
        this.visibleWordCount = 0;
        this.revealTimer      = 0f;
        this.active           = true;
    }

    public void hide() {
        active = false;
    }

    public boolean isActive()         { return active; }
    public boolean isFullyRevealed()  { return visibleWordCount >= words.length; }

    /** Skips straight to the fully revealed line — called on Enter mid-reveal. */
    public void revealAll() {
        visibleWordCount = words.length;
    }

    public void update(float delta) {
        if (!active || isFullyRevealed()) return;
        revealTimer += delta;
        while (revealTimer >= WORD_INTERVAL && !isFullyRevealed()) {
            revealTimer -= WORD_INTERVAL;
            visibleWordCount++;
        }
    }

    private String getVisibleText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < visibleWordCount; i++) {
            sb.append(words[i]);
            if (i < visibleWordCount - 1) sb.append(' ');
        }
        return sb.toString();
    }

    private float boxX() { return (Gdx.graphics.getWidth() - boxW()) / 2f; }
    private float boxW() { return Gdx.graphics.getWidth() * BOX_WIDTH_PCT; }

    /** Draw with a ShapeRenderer already begun in ShapeType.Filled, HUD-camera projection. */
    public void renderPanel(ShapeRenderer sr) {
        if (!active) return;
        sr.setColor(0f, 0f, 0f, 0.78f);
        sr.rect(boxX(), MARGIN_BOTTOM, boxW(), BOX_HEIGHT);
    }

    /** Draw with a SpriteBatch already begun, HUD-camera projection. */
    public void renderText(SpriteBatch batch, BitmapFont font) {
        if (!active) return;
        float x = boxX();
        float w = boxW();

        Color old = font.getColor().cpy();
        font.setColor(Color.WHITE);
        font.draw(batch, getVisibleText(),
            x + BOX_PADDING, MARGIN_BOTTOM + BOX_HEIGHT - BOX_PADDING,
            w - BOX_PADDING * 2f, Align.left, true);

        if (isFullyRevealed()) {
            font.setColor(1f, 1f, 1f, 0.7f);
            font.draw(batch, "[Enter]", x + w - 110f, MARGIN_BOTTOM + 30f);
        }
        font.setColor(old);
    }
}
