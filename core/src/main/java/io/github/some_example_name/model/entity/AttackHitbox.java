package io.github.some_example_name.model.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;

public class AttackHitbox {

    public final Rectangle bounds;      // REAL collision box — never adjust this for visuals
    public final int       damage;
    public       float     lifetime;
    public boolean hasHitPlayer = false;

    public final AnimationType animationType;
    public       float         stateTime = 0f;
    public       boolean       lookingRight;

    public final Vector2 velocity;

    /** Pure visual nudge — offsets ONLY where the sprite is drawn, never affects collision. */
    public float drawOffsetX = 0f;
    public float drawOffsetY = 0f;

    public final ObjectSet<EnemyEntity> hitEnemies = new ObjectSet<>();

    public AttackHitbox(float x, float y, float w, float h,
                        int damage, float lifetime,
                        AnimationType animationType, boolean lookingRight,
                        Vector2 velocity) {
        this.bounds        = new Rectangle(x, y, w, h);
        this.damage         = damage;
        this.lifetime       = lifetime;
        this.animationType  = animationType;
        this.lookingRight   = lookingRight;
        this.velocity       = velocity;
    }

    public boolean isExpired() {
        return lifetime <= 0f;
    }

    public void tick(float delta) {
        lifetime -= delta;
        stateTime += delta;
        if (velocity.x != 0f || velocity.y != 0f) {
            bounds.x += velocity.x * delta;
            bounds.y += velocity.y * delta;
        }
    }

    private static final float NAIL_LIFETIME   = 0.35f;
    private static final float SPELL_LIFETIME  = 1.2f;
    private static final float WRAITH_LIFETIME = 0.15f;
    private static final float VENGEFUL_SPEED  = 600f;

    public static AttackHitbox nailSwing(float x, float y, float w, float h, int damage, boolean lookingRight) {
        return new AttackHitbox(x, y, w, h, damage, NAIL_LIFETIME,
            AnimationType.NAIL_SLASH, lookingRight, new Vector2(0, 0));
    }

    public static AttackHitbox vengefulSpirit(float x, float y, int damage, boolean lookingRight) {
        float vx = lookingRight ? VENGEFUL_SPEED : -VENGEFUL_SPEED;
        return new AttackHitbox(x, y, 24f, 16f, damage, SPELL_LIFETIME,
            AnimationType.VENGEFUL_SPIRIT_PROJECTILE, lookingRight, new Vector2(vx, 0));
    }

    public static AttackHitbox howlingWraiths(float x, float y, int damage, boolean lookingRight) {
        return new AttackHitbox(x - 40f, y, 80f, 60f, damage, WRAITH_LIFETIME,
            AnimationType.HOWLING_WRAITHS_BLAST, lookingRight, new Vector2(0, 0));
    }
}
