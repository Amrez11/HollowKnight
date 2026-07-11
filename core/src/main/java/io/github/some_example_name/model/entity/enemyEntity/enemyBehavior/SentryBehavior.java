package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;

import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.SoundType;

/**
 * Special ground enemy. Normally alternates PATROL / RESTING on a timer.
 * If it spots the player during either of those states, it turns to face
 * them and marches until it hits a wall, another enemy, or the player
 * (EnemyCollisionLogic sets the wall/enemy flags; player contact is
 * checked directly here since update() already has the player reference),
 * then rests before resuming its normal patrol/rest cycle.
 */
public class SentryBehavior implements IEnemyBehavior {

    private static final float WALK_SPEED  = 100f;
    private static final float MARCH_SPEED = 220f;
    private static final float GRAVITY     = 500f;
    private static final int   CONTACT_DAMAGE = 1;

    private static final float PATROL_DURATION = 3.0f;
    private static final float REST_DURATION   = 2.0f;

    private static final float DETECT_RANGE = 300f;
    private static final float SAME_LEVEL_Y = 80f;

    private EnemyEntity self;

    private enum State { PATROL, RESTING, MARCH }
    private State state      = State.PATROL;
    private float stateTimer = 0f;

    @Override public void   setEntity(EnemyEntity entity) { this.self = entity; }
    @Override public int    getContactDamage()             { return CONTACT_DAMAGE; }
    @Override public AnimationType idleAnimation()         { return AnimationType.SENTRY_PATROL; }
    @Override public AnimationType deadAnimation()          { return AnimationType.SENTRY_DEAD; }
    @Override public SoundType     deathSound()              { return SoundType.SENTRY_DEATH; }

    @Override
    public void reset() {
        state      = State.PATROL;
        stateTimer = 0f;
    }

    @Override
    public void update(float delta, Entity player) {
        stateTimer += delta;

        float dx = player.getHitboxLeft() - self.getHitboxLeft();
        boolean sameLevel   = Math.abs(player.getHitboxBottom() - self.getHitboxBottom()) < SAME_LEVEL_Y;
        boolean seesPlayer  = Math.abs(dx) < DETECT_RANGE && sameLevel;

        switch (state) {
            case PATROL:
                runPatrol();
                if (seesPlayer) {
                    enterMarch(dx);
                } else if (stateTimer >= PATROL_DURATION) {
                    enterState(State.RESTING);
                }
                break;

            case RESTING:
                runRest();

                if (stateTimer >= REST_DURATION) {
                    enterState(State.PATROL);
                }
                break;

            case MARCH:
                runMarch();
                if (self.didHitWall() || self.didHitEnemy() || overlapsPlayer(player)) {
                    self.getVelocity().x = 0;
                    enterState(State.RESTING); // rests, then resumes patrol afterward
                }
                break;
        }

        applyGravity(delta);
        self.getPosition().x += self.getVelocity().x * delta;
        self.getPosition().y += self.getVelocity().y * delta;
    }

    private void runPatrol() {
        float dir = self.isLookingRight() ? 1f : -1f;
        self.getVelocity().x = dir * WALK_SPEED;
        setAnimation(AnimationType.SENTRY_PATROL);
    }

    private void runRest() {
        self.getVelocity().x = 0;
        setAnimation(AnimationType.SENTRY_REST);
    }

    private void runMarch() {
        float dir = self.isLookingRight() ? 1f : -1f;
        self.getVelocity().x = dir * MARCH_SPEED;
        setAnimation(AnimationType.SENTRY_MARCH);
    }

    private void enterMarch(float dx) {
        self.setLookingRight(dx > 0); // turn to face the player's side
        enterState(State.MARCH);
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

    private boolean overlapsPlayer(Entity player) {
        float eL = self.getHitboxLeft();
        float eR = self.getHitboxRight();
        float eB = self.getHitboxBottom();
        float eT = self.getHitboxTop();

        float pL = player.getHitboxLeft();
        float pR = player.getHitboxRight();
        float pB = player.getHitboxBottom();
        float pT = player.getHitboxTop();

        return !(eR <= pL || eL >= pR || eT <= pB || eB >= pT);
    }

    private void applyGravity(float delta) {
        if (self.isOnGround()) {
            if (self.getVelocity().y < 0) self.getVelocity().y = 0;
        } else {
            self.getVelocity().y -= GRAVITY * delta;
        }
    }
}
