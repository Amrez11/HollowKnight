package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;

import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.SoundType;

/**
 * Normal ground enemy — patrols back and forth at constant speed and turns
 * around whenever EnemyCollisionLogic detects a wall. No aggro, no lunge.
 * Uses a single animation for everything.
 */
public class CrawlerBehavior implements IEnemyBehavior {

    private static final float WALK_SPEED     = 120f;
    private static final float GRAVITY        = 500f;
    private static final int   CONTACT_DAMAGE = 1;

    private EnemyEntity self;

    @Override public void   setEntity(EnemyEntity entity) { this.self = entity; }
    @Override public int    getContactDamage()             { return CONTACT_DAMAGE; }
    @Override public AnimationType idleAnimation()         { return AnimationType.CRAWLER_IDLE; }
    @Override public AnimationType deadAnimation()          { return AnimationType.CRAWLER_DEAD; }
    @Override public SoundType     deathSound()              { return SoundType.CRAWLER_DEATH; }

    @Override
    public void update(float delta, Entity player) {
        float dir = self.isLookingRight() ? 1f : -1f;
        self.getVelocity().x = dir * WALK_SPEED;
        setAnimation(AnimationType.CRAWLER_IDLE);

        applyGravity(delta);
        self.getPosition().x += self.getVelocity().x * delta;
        self.getPosition().y += self.getVelocity().y * delta;
    }

    private void setAnimation(AnimationType anim) {
        if (self.getCurrentAnimation() != anim) {
            self.setCurrentAnimation(anim);
            self.resetStateTime();
        }
    }

    private void applyGravity(float delta) {
        if (self.isOnGround()) {
            if (self.getVelocity().y < 0) self.getVelocity().y = 0;
        } else {
            self.getVelocity().y -= GRAVITY * delta;
        }
    }
}
