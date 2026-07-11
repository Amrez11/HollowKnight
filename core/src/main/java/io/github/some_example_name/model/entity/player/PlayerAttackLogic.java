package io.github.some_example_name.model.entity.player;

import io.github.some_example_name.Manager.CharmManager;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.SoundType;

public class PlayerAttackLogic {

    private Entity entity;

    private static final float ATTACK_DURATION = 0.35f;
    private static final float ATTACK_REACH  = 85f;
    private static final float ATTACK_HEIGHT = 35f;
    public static final int SOUL_PER_HIT = 11;

    private float   attackTimer     = 0f;
    private boolean isAttacking     = false;
    private boolean attackInitiated = false;

    public PlayerAttackLogic(Entity entity) {
        this.entity = entity;
    }

    public void update(float delta) {
        if (entity == null) return;

        if (entity.isCastLocked() || entity.isFocus()) {
            return;
        }

        if (entity.isAttackPressed() && !attackInitiated && !isAttacking) {
            attackInitiated = true;
            isAttacking     = true;
            attackTimer     = ATTACK_DURATION * CharmManager.getNailSlashDurationMultiplier();
            entity.setAttacking(true);
            GameAssetManager.playSound(SoundType.PLAYER_NAIL_SWING);
        }
        if (!entity.isAttackPressed()) {
            attackInitiated = false;
        }

        if (isAttacking) {
            entity.setCurrentAnimation(AnimationType.KNIGHT_Attack);

            attackTimer -= delta;
            if (attackTimer <= 0f) {
                isAttacking = false;
                entity.setAttacking(false);
                attackTimer = 0f;
            }
        }
    }

    public float getAttackHitboxLeft() {
        return entity.isLookingRight()
            ? entity.getHitboxRight()
            : entity.getHitboxLeft() - ATTACK_REACH;
    }

    public float getAttackHitboxRight() {
        return entity.isLookingRight()
            ? entity.getHitboxRight() + ATTACK_REACH
            : entity.getHitboxLeft();
    }

    public float getAttackHitboxBottom() {
        float mid = entity.getHitboxBottom() + Entity.HITBOX_HEIGHT / 2f;
        return mid - ATTACK_HEIGHT / 2f;
    }

    public float getAttackHitboxTop() {
        return getAttackHitboxBottom() + ATTACK_HEIGHT;
    }
}
