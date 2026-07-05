package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;


import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;

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
}
