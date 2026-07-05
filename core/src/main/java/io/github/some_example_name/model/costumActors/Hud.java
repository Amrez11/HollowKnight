package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;

public class Hud {

    private static final float MASK_WIDTH   = 40f;
    private static final float MASK_HEIGHT  = 36f;
    private static final float MASK_SPACING = 44f;
    private static final float MARGIN_X     = 30f;
    private static final float MARGIN_Y_TOP = 40f;

    private static final float SOUL_WIDTH  = 70f;
    private static final float SOUL_HEIGHT = 70f;
    private static final float SOUL_GAP_Y  = 20f;

    private final float[]   breakStateTime; // per-mask local animation clock
    private final boolean[] breaking;
    private int   previousHp;
    private float soulStateTime = 0f;

    public Hud(int maxHp) {
        this.breakStateTime = new float[maxHp];
        this.breaking        = new boolean[maxHp];
        this.previousHp      = maxHp;
    }

    /** Call once per frame, same delta as Game.update(). */
    public void update(float delta, Entity player) {
        soulStateTime += delta;

        int hp = player.getHp();
        if (hp < previousHp) {
            for (int i = hp; i < previousHp; i++) {
                breaking[i]       = true;
                breakStateTime[i] = 0f;
            }
        }
        previousHp = hp;

        Animation<TextureRegion> breakAnim = GameAssetManager.animationMap.get(AnimationType.HUD_MASK_BREAK);
        for (int i = 0; i < breaking.length; i++) {
            if (breaking[i]) {
                breakStateTime[i] += delta;
                if (breakAnim.isAnimationFinished(breakStateTime[i])) {
                    breaking[i] = false;
                }
            }
        }
    }

    /**
     * Draw with a SCREEN-SPACE batch: batch.setProjectionMatrix(hudCamera.combined),
     * where hudCamera is a separate ortho camera that does NOT follow the player.
     */
    public void render(SpriteBatch batch, Entity player) {
        int maxHp = player.getMaxHp();
        int hp    = player.getHp();

        TextureRegion maskFull  = GameAssetManager.animationMap.get(AnimationType.HUD_MASK_FULL).getKeyFrame(0);
        TextureRegion maskEmpty = GameAssetManager.animationMap.get(AnimationType.HUD_MASK_EMPTY).getKeyFrame(0);
        Animation<TextureRegion> breakAnim = GameAssetManager.animationMap.get(AnimationType.HUD_MASK_BREAK);

        float startX = MARGIN_X;
        float startY = Gdx.graphics.getHeight() - MARGIN_Y_TOP;

        for (int i = 0; i < maxHp; i++) {
            float x = startX + i * MASK_SPACING;
            TextureRegion frame;

            if (breaking[i]) {
                frame = breakAnim.getKeyFrame(breakStateTime[i], false);
            } else if (i < hp) {
                frame = maskFull;
            } else {
                frame = maskEmpty;
            }

            batch.draw(frame, x, startY, MASK_WIDTH, MASK_HEIGHT);
        }

        // ── Soul orb ────────────────────────────────────────────────────
        float soulPct = player.getSoul() / (float) player.getMaxSoul();
        AnimationType soulType;
        if (soulPct >= 0.66f)      soulType = AnimationType.HUD_SOUL_FULL;
        else if (soulPct >= 0.33f) soulType = AnimationType.HUD_SOUL_HALF;
        else                       soulType = AnimationType.HUD_SOUL_EMPTY;

        TextureRegion soulFrame = GameAssetManager.animationMap.get(soulType).getKeyFrame(soulStateTime);

        float soulX = startX;
        float soulY = startY - MASK_HEIGHT - SOUL_GAP_Y - SOUL_HEIGHT;

        batch.draw(soulFrame, soulX, soulY, SOUL_WIDTH, SOUL_HEIGHT);

        if (soulPct >= 1f) {
            TextureRegion shineFrame = GameAssetManager.animationMap
                .get(AnimationType.HUD_SOUL_SHINE).getKeyFrame(soulStateTime);
            batch.draw(shineFrame, soulX, soulY, SOUL_WIDTH, SOUL_HEIGHT);
        }
    }
}
