package io.github.some_example_name.model.entity;

/**
 * Shared contract for anything that can be hit and take damage:
 * the player (Entity) and every enemy type (EnemyEntity).
 *
 * DamageResolver operates on this interface so it never needs to
 * know whether it is hurting a player or an enemy.
 */
public interface IDamageable {
    void    takeDamage(int amount);
    int     getHp();
    int     getMaxHp();
    boolean isDead();


}
