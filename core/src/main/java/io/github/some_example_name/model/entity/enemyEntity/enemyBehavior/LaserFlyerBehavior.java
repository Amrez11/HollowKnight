package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.SoundType;

/**
 * Special flying enemy. Hovers in place until it spots the player, fires a
 * long-range laser, then enters an enraged dash that continuously homes in
 * on the player's LIVE position (recomputed every frame, not a locked
 * destination). Once it gets close enough — or a safety timeout elapses —
 * it stops completely and just hovers there.
 */
public class LaserFlyerBehavior implements IEnemyBehavior {

    private static final float DETECT_RANGE      = 500f;
    private static final float CHARGE_DURATION   = 0.6f;
    private static final float FIRE_DURATION     = 0.5f;
    private static final float ENRAGE_SPEED      = 500f;
    private static final float ENRAGE_TIMEOUT    = 3f;    // safety cap
    private static final float ARRIVAL_THRESHOLD = 20f;

    private static final float LASER_LENGTH = 2000f;
    private static final float LASER_HEIGHT = 50f;
    private static final int   LASER_DAMAGE = 2;
    private static final int   CONTACT_DAMAGE = 1;

    private EnemyEntity self;

    private enum State { IDLE, CHARGE, FIRE, ENRAGE }
    private State state      = State.IDLE;
    private float stateTimer = 0f;

    private final Array<AttackHitbox> pendingHitboxes = new Array<>();
    private final Array<AttackHitbox> tempDrain       = new Array<>();

    @Override public void   setEntity(EnemyEntity entity) { this.self = entity; }
    @Override public int    getContactDamage()             { return CONTACT_DAMAGE; }
    @Override public AnimationType idleAnimation()         { return AnimationType.LASER_FLYER_IDLE; }
    @Override public AnimationType deadAnimation()          { return AnimationType.LASER_FLYER_DEAD; }
    @Override public SoundType     deathSound()              { return SoundType.LASER_FLYER_DEATH; }

    @Override
    public void reset() {
        state      = State.IDLE;
        stateTimer = 0f;
        pendingHitboxes.clear();
    }

    public Array<AttackHitbox> drainPendingHitboxes() {
        tempDrain.clear();
        tempDrain.addAll(pendingHitboxes);
        pendingHitboxes.clear();
        return tempDrain;
    }

    @Override
    public void update(float delta, Entity player) {
        stateTimer += delta;

        float cxSelf   = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        float cySelf   = (self.getHitboxBottom() + self.getHitboxTop()) / 2f;
        float cxPlayer = (player.getHitboxLeft() + player.getHitboxRight()) / 2f;
        float cyPlayer = (player.getHitboxBottom() + player.getHitboxTop()) / 2f;
        float dx = cxPlayer - cxSelf;
        float dy = cyPlayer - cySelf;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        switch (state) {
            case IDLE:
                self.getVelocity().set(0, 0);
                setAnimation(AnimationType.LASER_FLYER_IDLE);
                self.setLookingRight(dx >= 0);
                if (dist < DETECT_RANGE) enterState(State.CHARGE);
                break;

            case CHARGE:
                self.getVelocity().set(0, 0);
                setAnimation(AnimationType.LASER_FLYER_CHARGE);
                self.setLookingRight(dx >= 0);
                if (stateTimer >= CHARGE_DURATION) {
                    fireLaser();
                    enterState(State.FIRE);
                }
                break;

            case FIRE:
                self.getVelocity().set(0, 0);
                setAnimation(AnimationType.LASER_FLYER_FIRE);
                if (stateTimer >= FIRE_DURATION) {
                    enterState(State.ENRAGE);
                }
                break;

            case ENRAGE:
                setAnimation(AnimationType.LASER_FLYER_ENRAGE);
                // dx/dy/dist above are already computed from the player's
                // CURRENT position this frame, so this homes continuously.
                if (dist <= ARRIVAL_THRESHOLD || stateTimer >= ENRAGE_TIMEOUT) {
                    self.getVelocity().set(0, 0);
                    enterState(State.IDLE);
                } else {
                    self.setLookingRight(dx >= 0);
                    self.getVelocity().set((dx / dist) * ENRAGE_SPEED, (dy / dist) * ENRAGE_SPEED);
                }
                break;
        }

        self.getPosition().x += self.getVelocity().x * delta;
        self.getPosition().y += self.getVelocity().y * delta;
    }

    private void fireLaser() {
        float x;
        if (self.isLookingRight()) {
            x = self.getHitboxRight();
        } else {
            x = self.getHitboxLeft() - LASER_LENGTH;
        }
        float cy = (self.getHitboxBottom() + self.getHitboxTop()) / 2f;

        pendingHitboxes.add(new AttackHitbox(
            x, cy - LASER_HEIGHT / 2f,
            LASER_LENGTH, LASER_HEIGHT,
            LASER_DAMAGE, FIRE_DURATION,
            AnimationType.LASER_BEAM,
            self.isLookingRight(),
            new Vector2(0, 0)
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
