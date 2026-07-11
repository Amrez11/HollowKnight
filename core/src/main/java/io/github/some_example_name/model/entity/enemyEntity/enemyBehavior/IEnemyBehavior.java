package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;


import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.SoundType;

/**
 * Strategy interface for enemy AI.
 *
 * Every concrete behavior (crawler, flyer, boss…) implements this.
 * EnemyEntity delegates its update entirely to the strategy, so adding a
 * new enemy type is just writing a new class — zero changes elsewhere.
 *
 * The pattern mirrors how IMovementLogic / ISpecialAbility already work
 * for the player, so the architecture stays consistent.
 */
public interface IEnemyBehavior {

    /** Called once after the EnemyEntity is constructed. */
    void setEntity(EnemyEntity entity);

    /**
     * Drive movement, state-machine transitions, and attack decisions.
     *
     * @param delta  seconds since the last frame
     * @param player the player entity — read position/velocity for targeting;
     *               never modify it here (damage goes through DamageResolver)
     */
    void update(float delta, Entity player);

    /**
     * Damage dealt to the player when the enemy body overlaps the player hitbox.
     * DamageResolver reads this every frame it detects an overlap.
     */
    int getContactDamage();

    /**
     * The animation to display when no other state is active.
     * EnemyEntity calls this once in its constructor to set the initial animation.
     */
    AnimationType idleAnimation();

    /**
     * The animation to display once the enemy has died. Defaults to the idle
     * animation so behaviors that don't override this still compile/run —
     * override per-enemy-type with a dedicated death sprite.
     */
    default AnimationType deadAnimation() {
        return idleAnimation();
    }

    /** Sound played whenever this enemy takes damage and survives the hit. */
    default SoundType hitSound() {
        return SoundType.ENEMY_HIT;
    }

    /** Sound played the instant this enemy's hp hits 0 — pair it with deadAnimation(). */
    default SoundType deathSound() {
        return SoundType.ENEMY_HIT;
    }

    /**
     * Clears any internal state-machine progress (state enum, timers, etc.)
     * back to its starting point. Called by EnemyEntity.respawn() so a
     * revived enemy doesn't resume mid-attack or mid-cooldown.
     */
    default void reset() {
        // no-op by default
    }
}
