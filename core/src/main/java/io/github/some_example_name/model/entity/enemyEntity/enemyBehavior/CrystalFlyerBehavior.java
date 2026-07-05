package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;

/**
 * Normal flying enemy. Stays fixed at its spawn point forever — it never
 * moves, in any state — and fires a single crystal projectile straight at
 * the player whenever they come within range, then cools down before it
 * can fire again.
 */
public class CrystalFlyerBehavior implements IEnemyBehavior {

    private static final float DETECT_RANGE      = 400f;
    private static final float WINDUP_DURATION   = 0.4f;
    private static final float COOLDOWN_DURATION = 1.2f;

    private static final float PROJECTILE_SPEED    = 260f;
    private static final float PROJECTILE_W        = 40f;
    private static final float PROJECTILE_H        = 40f;
    private static final float PROJECTILE_LIFETIME = 3f;
    private static final int   PROJECTILE_DAMAGE   = 1;
    private static final int   CONTACT_DAMAGE      = 1;

    private EnemyEntity self;

    private enum State { IDLE, WINDUP, COOLDOWN }
    private State state      = State.IDLE;
    private float stateTimer = 0f;

    private final Array<AttackHitbox> pendingHitboxes = new Array<>();
    private final Array<AttackHitbox> tempDrain       = new Array<>();

    @Override public void   setEntity(EnemyEntity entity) { this.self = entity; }
    @Override public int    getContactDamage()             { return CONTACT_DAMAGE; }
    @Override public AnimationType idleAnimation()         { return AnimationType.FLYER_IDLE; }

    public Array<AttackHitbox> drainPendingHitboxes() {
        tempDrain.clear();
        tempDrain.addAll(pendingHitboxes);
        pendingHitboxes.clear();
        return tempDrain;
    }

    @Override
    public void update(float delta, Entity player) {
        stateTimer += delta;

        // Never moves, in any state.
        self.getVelocity().set(0, 0);

        float cxSelf   = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        float cySelf   = (self.getHitboxBottom() + self.getHitboxTop()) / 2f;
        float cxPlayer = (player.getHitboxLeft() + player.getHitboxRight()) / 2f;
        float cyPlayer = (player.getHitboxBottom() + player.getHitboxTop()) / 2f;
        float dx = cxPlayer - cxSelf;
        float dy = cyPlayer - cySelf;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        self.setLookingRight(dx >= 0);

        switch (state) {
            case IDLE:
                setAnimation(AnimationType.FLYER_IDLE);
                if (dist < DETECT_RANGE) enterState(State.WINDUP);
                break;

            case WINDUP:
                setAnimation(AnimationType.FLYER_HOVER);
                if (stateTimer >= WINDUP_DURATION) {
                    fireCrystal(dx, dy, dist);
                    enterState(State.COOLDOWN);
                }
                break;

            case COOLDOWN:
                setAnimation(AnimationType.FLYER_IDLE);
                if (stateTimer >= COOLDOWN_DURATION) enterState(State.IDLE);
                break;
        }
    }

    private void fireCrystal(float dx, float dy, float dist) {
        float dirX = dist > 0 ? dx / dist : 1f;
        float dirY = dist > 0 ? dy / dist : 0f;

        float cx = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        float cy = (self.getHitboxBottom() + self.getHitboxTop()) / 2f;

        pendingHitboxes.add(new AttackHitbox(
            cx - PROJECTILE_W / 2f, cy - PROJECTILE_H / 2f,
            PROJECTILE_W, PROJECTILE_H,
            PROJECTILE_DAMAGE, PROJECTILE_LIFETIME,
            AnimationType.CRYSTAL_PROJECTILE,
            self.isLookingRight(),
            new Vector2(dirX * PROJECTILE_SPEED, dirY * PROJECTILE_SPEED)
        ));
    }

    private void enterState(State next) {
        state      = next;
        stateTimer = 0f;
    }

    private void setAnimation(AnimationType anim) {
        if (self.getCurrentAnimation() != anim) {
            self.setCurrentAnimation(anim);
            self.resetStateTime();
        }
    }
}
