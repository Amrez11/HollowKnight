package io.github.some_example_name.model.entity.enemyEntity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.model.entity.IDamageable;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.*;

import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;

public class EnemyEntity implements IDamageable {

    public final float hitboxLeftX;
    public final float hitboxRightX;
    public final float hitboxBottomY;
    public final float hitboxTopY;

    private final Vector2 position;
    // Fixed spawn point, used to send the enemy home when it's respawned
    // (e.g. after the player leaves and re-enters its home room).
    private final Vector2 spawnPosition;
    private final Vector2 velocity    = new Vector2();
    private       boolean lookingRight = true;
    private       boolean onGround     = false;

    // ── Collision flags, reset+set each frame by EnemyCollisionLogic ──────────
    private boolean hitWallThisFrame  = false;
    private boolean hitEnemyThisFrame = false;

    private final int maxHp;
    private       int hp;
    private       boolean dead = false;
    private       boolean deathProcessed = false; // <-- ADDED: Prevents multi-firing achievements

    // Whether a dead body has settled onto the ground yet. While false,
    // Game.update() keeps feeding this enemy gravity + collision so a corpse
    // (especially a flyer that died mid-air) drops and lands instead of just
    // freezing wherever it happened to die.
    private boolean landed = true;
    private static final float DEATH_GRAVITY = 1000f; // matches PlayerMovement.GRAVITY

    private static final float I_FRAME_DURATION = 0f;
    private              float invincibilityTimer = 0f;

    private final IEnemyBehavior behavior;

    private AnimationType currentAnimation;
    private float         stateTime = 0f;

    public EnemyEntity(Vector2 startPosition,
                       int maxHp,
                       float hitboxLeft, float hitboxRight,
                       float hitboxBottom, float hitboxTop,
                       IEnemyBehavior behavior) {
        this.position     = new Vector2(startPosition);
        this.spawnPosition = new Vector2(startPosition);
        this.maxHp        = maxHp;
        this.hp           = maxHp;
        this.hitboxLeftX  = hitboxLeft;
        this.hitboxRightX = hitboxRight;
        this.hitboxBottomY = hitboxBottom;
        this.hitboxTopY   = hitboxTop;
        this.behavior     = behavior;
        this.behavior.setEntity(this);
        this.currentAnimation = behavior.idleAnimation();
    }

    // ── Factory helpers ─────────────────────────────────────────────────────

    public static EnemyEntity crawler(Vector2 pos) {
        return new EnemyEntity(pos, 20, 0f, 140f, 0f, 120, new CrawlerBehavior());
    }

    public static EnemyEntity sentry(Vector2 pos) {
        return new EnemyEntity(pos, 20, 0f, 140f, 0f, 120, new SentryBehavior());
    }

    public static EnemyEntity flyer(Vector2 pos) {
        return new EnemyEntity(pos, 2, 20f, 130f, 10f, 200f, new CrystalFlyerBehavior());
    }

    public static EnemyEntity laserFlyer(Vector2 pos) {
        return new EnemyEntity(pos, 4, 20f, 130f, 10f, 200f, new LaserFlyerBehavior());
    }


    public static EnemyEntity boss(Vector2 pos) {
        return new EnemyEntity(pos, 10, 300, 700, 4f, 300f,new BossBehavior());
    }

    public void update(float delta, Entity player) {
        System.out.println("entityEnemy" + hp);
        if (dead) return;

        if (invincibilityTimer > 0f) {
            invincibilityTimer = Math.max(0f, invincibilityTimer - delta);
        }

        stateTime += delta;
        behavior.update(delta, player);
    }

    @Override
    public void takeDamage(int amount) {
        if (dead) return;
        if (behavior instanceof BossBehavior && ((BossBehavior) behavior).isStunned()) {
            return;
        }
        hp = Math.max(0, hp - amount);
        invincibilityTimer = I_FRAME_DURATION;
        if (hp == 0) {
            dead = true;
            landed = false;
            velocity.set(0f, 0f);
            currentAnimation = behavior.deadAnimation();
            stateTime = 0f;
            io.github.some_example_name.Manager.GameAssetManager.playSound(behavior.deathSound());
        } else {
            io.github.some_example_name.Manager.GameAssetManager.playSound(behavior.hitSound());
        }
    }

    @Override public int     getHp()        { return hp; }
    @Override public int     getMaxHp()     { return maxHp; }
    @Override public boolean isDead()       { return dead; }

    public float getHitboxLeft()   { return position.x + hitboxLeftX; }
    public float getHitboxRight()  { return position.x + hitboxRightX; }
    public float getHitboxBottom() { return position.y + hitboxBottomY; }
    public float getHitboxTop()    { return position.y + hitboxTopY; }

    public Rectangle getHitboxRect() {
        return new Rectangle(
            getHitboxLeft(),
            getHitboxBottom(),
            hitboxRightX - hitboxLeftX,
            hitboxTopY   - hitboxBottomY);
    }

    /** The room-relative point this enemy first spawned at (used by respawn()). */
    public Vector2       getSpawnPosition()        { return spawnPosition; }

    /**
     * Brings a dead enemy back to life at its original spawn point with full
     * hp, clearing every transient combat/AI flag so it behaves exactly like
     * a freshly-spawned enemy. Called when the player leaves the room this
     * enemy belongs to, so rooms feel populated again on re-entry.
     */
    public void respawn() {
        hp                 = maxHp;
        dead               = false;
        deathProcessed     = false;
        invincibilityTimer = 0f;
        hitWallThisFrame   = false;
        hitEnemyThisFrame  = false;
        landed             = true;
        position.set(spawnPosition);
        velocity.set(0f, 0f);
        behavior.reset();
        currentAnimation = behavior.idleAnimation();
        stateTime = 0f;
    }

    public Vector2       getPosition()             { return position; }
    public Vector2       getVelocity()             { return velocity; }
    public boolean       isLookingRight()          { return lookingRight; }
    public void          setLookingRight(boolean v){ lookingRight = v; }
    public boolean       isOnGround()              { return onGround; }
    public void          setOnGround(boolean v)    { onGround = v; }
    public IEnemyBehavior getBehavior()            { return behavior; }

    // ── Death-fall physics ──────────────────────────────────────────────────
    // A dead body keeps falling (via Game.update()'s special-cased branch)
    // until it lands on solid ground, then stays put forever.
    public boolean hasLanded()          { return landed; }
    public void    setLanded(boolean v) { landed = v; }

    /** Simple gravity integration, used only while a corpse is settling onto the ground. */
    public void applyDeathGravity(float delta) {
        velocity.y -= DEATH_GRAVITY * delta;
    }

    // ── Collision flags ───────────────────────────────────────────────────────
    public boolean didHitWall()                      { return hitWallThisFrame; }
    public void    setHitWallThisFrame(boolean v)    { hitWallThisFrame = v; }
    public boolean didHitEnemy()                     { return hitEnemyThisFrame; }
    public void    setHitEnemyThisFrame(boolean v)   { hitEnemyThisFrame = v; }

    public AnimationType getCurrentAnimation()                               { return currentAnimation; }
    public void          setCurrentAnimation(AnimationType a)                { this.currentAnimation = a; }
    public float         getStateTime()                                      { return stateTime; }
    public void          resetStateTime()                                    { this.stateTime = 0f; }

    // ── Achievement Tracking Flags ────────────────────────────────────────────
    public boolean isDeathProcessed() { return deathProcessed; }
    public void setDeathProcessed(boolean v) { deathProcessed = v; }

    // ── Save/Load ──────────────────────────────────────────────────────────
    /**
     * Restores hp/alive-state from a save file. Enemies loaded already-dead
     * have deathProcessed forced true so Game.update() doesn't re-fire their
     * defeat achievement / victory screen on the next frame.
     */
    public void applySavedState(int hp, boolean dead) {
        this.hp = Math.max(0, Math.min(hp, maxHp));
        this.dead = dead;
        this.deathProcessed = dead;
        this.invincibilityTimer = 0f;
        if (dead) {
            velocity.set(0f, 0f);
            currentAnimation = behavior.deadAnimation();
            // Trust the saved position rather than re-dropping the corpse —
            // it was presumably already resting on the ground when saved.
            landed = true;
        }
    }

}
