package io.github.some_example_name.model.entity.player;

import io.github.some_example_name.Manager.CharmManager;
import io.github.some_example_name.model.enums.AnimationType;

public class PlayerSpecialAbility {

    private Entity entity;

    private static final int SOUL_COST = 33;

    private static final float FOCUS_DURATION   = 1.5f;
    private static final int   FOCUS_HEALTH_GAIN = 1;

    private float   focusTimer  = 0f;
    private boolean isFocusing  = false;

    private static final float CAST_LOCK_DURATION   = 0.7f;
    private static final float HOWLING_HIT_INTERVAL = 0.15f;

    private float castLockTimer        = 0f;
    private float howlingHitTimer      = 0f;
    private int   howlingHitsRemaining = 0;

    private static final float VENGEFUL_CAST_DURATION = 0.4f;
    private float vengefulCastTimer = 0f;
    private boolean vengefulFiring  = false;

    public PlayerSpecialAbility(Entity entity) {
        this.entity = entity;
    }

    public void update(float delta) {
        if (entity == null) return;

        updateFocus(delta);
        updateHowlingWraiths(delta);
        updateVengefulSpirit(delta);
    }

    public void tickCastLock(float delta) {
        if (castLockTimer > 0f) {
            castLockTimer -= delta;
            if (castLockTimer <= 0f) {
                castLockTimer = 0f;
                entity.setCastLocked(false);
            }
        }
    }

    private void updateFocus(float delta) {
        boolean canFocus = entity.isOnGround()
            && !entity.isMovingLeft()
            && !entity.isMovingRight()
            && !entity.isJump()
            && !entity.isDash()
            && !entity.isCastLocked()
            && entity.getSoul() >= SOUL_COST;

        if (entity.isFocus()) {
            if (!canFocus) {
                cancelFocus();
                return;
            }

            if (!isFocusing) {
                isFocusing = true;
                focusTimer = 0f;
            }

            if (entity.isDamaged()) {
                cancelFocus();
                return;
            }

            entity.setCurrentAnimation(AnimationType.KNIGHT_Focus);
            focusTimer += delta;

            if (focusTimer >= FOCUS_DURATION * CharmManager.getFocusDurationMultiplier()) {
                entity.setSoul(entity.getSoul() - SOUL_COST);
                entity.setHp(Math.min(
                    entity.getHp() + FOCUS_HEALTH_GAIN,
                    entity.getMaxHp()));
                cancelFocus();
            }

        } else {
            if (isFocusing) {
                cancelFocus();
            }
        }
    }

    private void cancelFocus() {
        isFocusing = false;
        focusTimer = 0f;
        entity.setFocus(false);
    }

    private void updateHowlingWraiths(float delta) {

        if (entity.isHowlingWraith()) {
            entity.setHowlingWraith(false);

            if (entity.getSoul() < SOUL_COST || entity.isCastLocked()) {
                return;
            }

            entity.setSoul(entity.getSoul() - SOUL_COST);
            entity.setCastLocked(true);
            castLockTimer = CAST_LOCK_DURATION;
            entity.setCurrentAnimation(AnimationType.KNIGHT_HowlingWraiths);

            howlingHitsRemaining = 3;
            howlingHitTimer      = 0f;
        }

        if (howlingHitsRemaining > 0) {
            howlingHitTimer += delta;
            if (howlingHitTimer >= HOWLING_HIT_INTERVAL) {
                howlingHitTimer = 0f;
                howlingHitsRemaining--;
                entity.setHowlingHitTriggered(true);
            }
        }
    }

    private void updateVengefulSpirit(float delta) {

        if (entity.isVengefulSpirit()) {
            entity.setVengefulSpirit(false);

            if (entity.getSoul() < SOUL_COST || entity.isCastLocked()) {
                return;
            }

            entity.setSoul(entity.getSoul() - SOUL_COST);
            entity.setCastLocked(true);
            castLockTimer    = VENGEFUL_CAST_DURATION;
            vengefulCastTimer = VENGEFUL_CAST_DURATION;
            vengefulFiring   = true;
            entity.setCurrentAnimation(AnimationType.KNIGHT_VengefulSpirit);
        }

        if (vengefulFiring) {
            vengefulCastTimer -= delta;
            if (vengefulCastTimer <= 0f) {
                vengefulFiring = false;
                entity.setVengefulFireTriggered(true);
            }
        }
    }
}
