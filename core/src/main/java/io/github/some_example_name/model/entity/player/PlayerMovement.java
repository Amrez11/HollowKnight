package io.github.some_example_name.model.entity.player;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Manager.CharmManager;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.Charm;

public class PlayerMovement {

    private Entity entity;

    // ── Physics ───────────────────────────────────────────────────────────────
    private static final float GRAVITY        = 1000f;
    private static final float SLIDE_GRAVITY  = 300f;
    private static final float MAX_SLIDE_SPEED = -400f;

    // ── Movement ──────────────────────────────────────────────────────────────
    private static final float RUN_SPEED  = 300f;
    private static final float JUMP_SPEED = 600f;
    private static final float DASH_SPEED = 900f;


    private static final float MIN_JUMP_SPEED = 400f;

    // ── Timers / state ────────────────────────────────────────────────────────
    private static final float BASE_DASH_DURATION = 0.4f;
    private float dashTimer         =  BASE_DASH_DURATION;
    private float dashCooldownTimer = -1.5f;


    private boolean jumpInitiated = false;


    private boolean wallJumpUsed = false;

    public PlayerMovement(Entity entity) {
        this.entity = entity;
    }

    public void update(float delta) {
        if (entity.isCastLocked()) {
            doTheChanges(entity.getVelocity(), delta);
            return;
        }
        if (entity == null) return;

        // ── Dash activation ───────────────────────────────────────────────────
        if (entity.isDashPressed() && dashCooldownTimer <= 0f) {
            entity.setDash(true);
            entity.setDashPressed(false);

        }

        // ── Dash update (early-return branch) ─────────────────────────────────
        if (entity.isDash()) {
            entity.setCurrentAnimation(AnimationType.KNIGHT_Dash);

            if (entity.isMovingRight()) {
                entity.getVelocity().x = DASH_SPEED;
                entity.setLookingRight(true);
            } else if (entity.isMovingLeft()) {
                entity.getVelocity().x = -DASH_SPEED;
                entity.setLookingRight(false);
            } else {
                entity.getVelocity().x = entity.isLookingRight() ? DASH_SPEED : -DASH_SPEED;
            }
            entity.setMovingDown(false);
            entity.getVelocity().y = 0f;
            doTheChanges(entity.getVelocity(), delta);
            return;
        }

        // ── Wall-slide update (early-return branch) ───────────────────────────
        if (entity.isSliding()) {
            if (entity.isJumpPressed() || entity.isDash() ||
                entity.isMovingLeft() || entity.isMovingRight()) {
                entity.setSliding(false);
                entity.setWallSliding(false);


            } else {
                entity.getVelocity().y -= SLIDE_GRAVITY * delta;
                if (entity.getVelocity().y < MAX_SLIDE_SPEED) {
                    entity.getVelocity().y = MAX_SLIDE_SPEED;
                }
                entity.getVelocity().x = 0f;
                if (!entity.isOnGround()) {
                    entity.setCurrentAnimation(AnimationType.KNIGHT_Slide);
                }
                doTheChanges(entity.getVelocity(), delta);
                return;
            }
        }

        // ── Jump: detect a fresh key press ────────────────────────────────────
        //
        // jumpInitiated stays true while the key is held, so this block fires
        // only on the FIRST frame of each press.  This is what enables:
        //   • normal jump  (numOfJumps == 2  →  first  jump)
        //   • double jump  (numOfJumps == 1  →  second jump)
        //
        if (entity.isJumpPressed() && !jumpInitiated && entity.getNumOfJumps() >= 1) {
            jumpInitiated = true;
            entity.setJump(true);
            entity.getVelocity().y = JUMP_SPEED;
            entity.setOnGround(false);

            if (entity.getNumOfJumps() == 2) {
                // ── First jump ────────────────────────────────────────────────
                entity.setCurrentAnimation(AnimationType.KNIGHT_Jump);
                entity.setNumOfJumps(1);          // one jump remaining
            } else {
                // ── Double jump (numOfJumps == 1) ─────────────────────────────
                entity.setCurrentAnimation(AnimationType.KNIGHT_DJump);
                entity.setNumOfJumps(0);          // both jumps consumed
            }
        }

        // Reset gate when key is released so the next press can jump again
        if (!entity.isJumpPressed()) {
            jumpInitiated = false;
            entity.setJump(false);
        }

        // ── Variable-height jump cut ──────────────────────────────────────────
        //
        // If the player releases the key while still moving upward faster than
        // MIN_JUMP_SPEED, clamp velocity.y immediately.  Gravity then takes
        // over and the character falls sooner → shorter jump arc.
        //
        if (!entity.isJumpPressed() && entity.getVelocity().y > MIN_JUMP_SPEED) {
            entity.getVelocity().y = MIN_JUMP_SPEED;
        }

        // ── Horizontal movement ───────────────────────────────────────────────
        if (entity.isMovingLeft()) {
            entity.getVelocity().x = -RUN_SPEED;
            entity.setLookingRight(false);
            if (entity.isOnGround()) {
                entity.setCurrentAnimation(AnimationType.KNIGHT_RUN);
            }
        } else if (entity.isMovingRight()) {
            entity.getVelocity().x = RUN_SPEED;
            entity.setLookingRight(true);
            if (entity.isOnGround()) {
                entity.setCurrentAnimation(AnimationType.KNIGHT_RUN);
            }
        } else {
            entity.getVelocity().x = 0f;
            if (entity.isOnGround()) {
                entity.setCurrentAnimation(AnimationType.KNIGHT_IDLE);
            }
        }


        if (!entity.isOnGround()) {
            if (!entity.isNoclip()) {
                entity.getVelocity().y -= GRAVITY * delta;
                if (entity.getVelocity().y < 0f) {
                    entity.setCurrentAnimation(AnimationType.KNIGHT_Fall);
                }

            }
        } else {
            // Snap downward drift to zero while grounded
            if (entity.getVelocity().y < 0f) {
                entity.getVelocity().y = 0f;
            }
            // Landing resets the wall-jump allowance for the next wall contact
            wallJumpUsed = false;
        }

        doTheChanges(entity.getVelocity(), delta);
    }

    // ─────────────────────────────────────────────────────────────────────────

    public void doTheChanges(Vector2 velocity, float delta) {
        entity.setMovingDown(entity.getVelocity().y < 0f);
        entity.getPosition().add(entity.getVelocity().cpy().scl(delta));
        entity.setStateTime(entity.getStateTime() + delta);

        // ── Dash timer ────────────────────────────────────────────────────────
        if (entity.isDash() && dashTimer > 0f) {
            dashTimer -= delta;
        }

        if (dashTimer < 0f) {
            entity.setDash(false);

            // Check if the specific charm is equipped.
            // (Replace 'Charm.YOUR_DASH_CHARM' with your actual charm enum value)
            if (CharmManager.isEquipped(Charm.DASHMASTER)) {
                dashCooldownTimer = 0.8f;
            } else {
                dashCooldownTimer = 1.5f;
            }

            dashTimer = BASE_DASH_DURATION;
        }

        if (dashCooldownTimer > 0f) {
            dashCooldownTimer -= delta;
        }
    }
}
