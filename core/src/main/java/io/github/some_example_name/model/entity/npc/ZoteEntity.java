package io.github.some_example_name.model.entity.npc;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;


public class ZoteEntity {

    private enum State { IDLE, TALKING, ENRAGED }

    // ── Hitbox (roughly humanoid — matches Crawler/Sentry sizing) ────────────
    private static final float HITBOX_LEFT_X   = 0f;
    private static final float HITBOX_RIGHT_X  = 140f;
    private static final float HITBOX_BOTTOM_Y = 0f;
    private static final float HITBOX_TOP_Y    = 120f;

    private static final float INTERACT_RANGE  = 160f;
    /** Must roughly match AnimationType.ZOTE_ENRAGE_ATTACK's total playtime (see GameAssetManager). */
    private static final float ENRAGE_DURATION = 0.9f;
    private static final float ATTACK_HITBOX_LIFETIME = 0.35f;

    private final Vector2 position;
    private State   state        = State.IDLE;
    private boolean lookingRight = false;

    private AnimationType currentAnimation = AnimationType.ZOTE_IDLE;
    private float          stateTime       = 0f;

    private float enrageTimer = 0f;
    private final Array<AttackHitbox> pendingHitboxes = new Array<>();

    private final ZoteDialogue dialogue    = new ZoteDialogue();
    private final DialogueBox  dialogueBox = new DialogueBox();

    public ZoteEntity(Vector2 position) {
        this.position = new Vector2(position);
    }

    // ── Frame update ──────────────────────────────────────────────────────

    public void update(float delta, Entity player, Array<AttackHitbox> playerHitboxes) {
        stateTime += delta;
        dialogueBox.update(delta);
        faceTowards(player);

        if (state == State.ENRAGED) {
            enrageTimer -= delta;
            if (enrageTimer <= 0f) {
                state = State.IDLE;
                setAnimation(AnimationType.ZOTE_IDLE);
            }
            return;
        }

        checkForNailHit(player, playerHitboxes);
    }

    private void faceTowards(Entity player) {
        if (state == State.ENRAGED) return;
        lookingRight = player.getPosition().x >= position.x;
    }

    private void checkForNailHit(Entity player, Array<AttackHitbox> playerHitboxes) {
        Rectangle myBounds = getHitboxRect();
        for (AttackHitbox hb : playerHitboxes) {
            if (hb.animationType != AnimationType.NAIL_SLASH) continue;
            if (hb.hasHitZote) continue;
            if (!hb.bounds.overlaps(myBounds)) continue;

            hb.hasHitZote = true;
            triggerEnrage(player);
            return;
        }
    }

    private void triggerEnrage(Entity player) {
        // A conversation in progress is interrupted — he's too busy flailing to talk.
        if (dialogueBox.isActive()) {
            dialogueBox.hide();
            player.setDialogueLocked(false);
        }

        state       = State.ENRAGED;
        enrageTimer = ENRAGE_DURATION;
        setAnimation(AnimationType.ZOTE_ENRAGE_ATTACK);

        // Zote "attacks" but the strike is famously ineffectual — 0 damage.
        // Still a real hitbox through the normal DamageResolver pipeline, so
        // it renders and behaves consistently with every other enemy attack.
        float spawnX = lookingRight ? getHitboxRight() : getHitboxLeft() - 60f;
        AttackHitbox hb = new AttackHitbox(
            spawnX, getHitboxBottom() + 10f, 60f, 60f,
            0, ATTACK_HITBOX_LIFETIME, AnimationType.BOSS_SLAM_EFFECT, lookingRight, new Vector2(0, 0));
        pendingHitboxes.add(hb);
    }

    /** Called once per frame by Game.update() — drains this frame's attack hitboxes into game.enemyHitboxes. */
    public Array<AttackHitbox> drainPendingHitboxes() {
        Array<AttackHitbox> out = new Array<>(pendingHitboxes);
        pendingHitboxes.clear();
        return out;
    }

    private void setAnimation(AnimationType anim) {
        if (currentAnimation != anim) {
            currentAnimation = anim;
            stateTime = 0f;
        }
    }

    // ── Dialogue ─────────────────────────────────────────────────────────

    public boolean isPlayerInRange(Entity player) {
        float dx = player.getPosition().x - position.x;
        float dy = player.getPosition().y - position.y;
        return (dx * dx + dy * dy) <= INTERACT_RANGE * INTERACT_RANGE;
    }

    public boolean isDialogueActive() { return dialogueBox.isActive(); }

    /** Fires on the interact key. Starts a fresh conversation, if possible. */
    public void tryOpenDialogue(Entity player) {
        if (state == State.ENRAGED) return;
        if (dialogueBox.isActive()) return;
        if (!isPlayerInRange(player)) return;

        state = State.TALKING;
        player.setDialogueLocked(true);
        dialogueBox.show(dialogue.startConversation());
        GameAssetManager.playRandomZoteGrowl();
    }

    /** Fires on Enter while a conversation is open. */
    public void advanceDialogue(Entity player) {
        if (!dialogueBox.isActive()) return;

        // First Enter press on a line just fast-forwards the reveal.
        if (!dialogueBox.isFullyRevealed()) {
            dialogueBox.revealAll();
            return;
        }

        String next = dialogue.advance();
        if (next == null) {
            closeDialogue(player);
            return;
        }
        dialogueBox.show(next);
        GameAssetManager.playRandomZoteGrowl();
    }

    private void closeDialogue(Entity player) {
        dialogueBox.hide();
        state = State.IDLE;
        player.setDialogueLocked(false);
    }

    // ── Getters for rendering ───────────────────────────────────────────

    public Vector2       getPosition()         { return position; }
    public boolean       isLookingRight()      { return lookingRight; }
    public AnimationType getCurrentAnimation() { return currentAnimation; }
    public float         getStateTime()        { return stateTime; }
    public DialogueBox   getDialogueBox()      { return dialogueBox; }

    public float getHitboxLeft()   { return position.x + HITBOX_LEFT_X; }
    public float getHitboxRight()  { return position.x + HITBOX_RIGHT_X; }
    public float getHitboxBottom() { return position.y + HITBOX_BOTTOM_Y; }
    public float getHitboxTop()    { return position.y + HITBOX_TOP_Y; }

    public Rectangle getHitboxRect() {
        return new Rectangle(getHitboxLeft(), getHitboxBottom(),
            HITBOX_RIGHT_X - HITBOX_LEFT_X, HITBOX_TOP_Y - HITBOX_BOTTOM_Y);
    }
}
