package io.github.some_example_name.model.entity.player;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.SolidBlock;

public class  CollisionLogic {

    private final Entity entity;
    private final Array<SolidBlock> solidBlocks;

    private boolean wasOnGround = false;  // tracks previous frame's ground state

    private static final float GROUND_SNAP = 1.0f;  // how close is "on ground"

    public CollisionLogic(Entity entity, Array<SolidBlock> solidBlocks) {
        this.entity = entity;
        this.solidBlocks = solidBlocks;
    }

    public void checkCollisions() {
        // Reset for this frame
        entity.setOnGround(false);
        entity.setSliding(false);

        // 1. Resolve all block collisions (vertical & horizontal)
        for (SolidBlock block : solidBlocks) {
            resolveBlock(block);
        }

        // 2. Ground‑snapping: prevent the "idle‑fall‑idle" flicker
        if (!entity.isOnGround() && wasOnGround) {
            snapToGround();
        }

        // 3. Wall‑touching detection (only matters when airborne)
        boolean touchingWall = isTouchingWall();

        // 4. Wall‑sliding logic (only while truly airborne)
        if (touchingWall && !entity.isOnGround()) {
            if (!entity.isWallSliding()) {
                entity.setWallSliding(true);
            }
            entity.setSliding(true);
            entity.setOnGround(false);          // ignore floor while sliding
            entity.setNumOfJumps(1);
        } else {
            // Not touching wall or already on ground → exit wall‑slide
            entity.setWallSliding(false);
        }

        // 5. Final ground state
        if (entity.isOnGround()) {
            entity.setSliding(false);
            entity.setNumOfJumps(2);
        }

        // 6. Remember for next frame
        wasOnGround = entity.isOnGround();
    }

    /**
     * Resolves collisions with a single block.
     */
    private void resolveBlock(SolidBlock block) {
        float entityLeft   = entity.getHitboxLeft();
        float entityRight  = entity.getHitboxRight();
        float entityBottom = entity.getHitboxBottom();
        float entityTop    = entity.getHitboxTop();

        float blockLeft   = block.bounds.x;
        float blockRight  = block.bounds.x + block.bounds.width;
        float blockBottom = block.bounds.y;
        float blockTop    = block.bounds.y + block.bounds.height;

        // No overlap
        if (entityRight <= blockLeft || entityLeft >= blockRight ||
            entityTop <= blockBottom || entityBottom >= blockTop) {
            return;
        }

        float overlapLeft   = entityRight - blockLeft;
        float overlapRight  = blockRight - entityLeft;
        float overlapBottom = entityTop - blockBottom;
        float overlapTop    = blockTop - entityBottom;

        float minX = Math.min(overlapLeft, overlapRight);
        float minY = Math.min(overlapBottom, overlapTop);

        if (minY < minX) {
            // Vertical collision
            if (overlapTop < overlapBottom) {
                // Land on top
                entity.getPosition().y = blockTop - Entity.HITBOX_BOTTOM_Y;
                entity.getVelocity().y = 0;
                entity.setMovingDown(false);
                entity.setOnGround(true);
            } else {
                // Hit ceiling
                entity.getPosition().y = blockBottom - Entity.HITBOX_TOP_Y;
                entity.getVelocity().y = 0;
                entity.setMovingDown(true);
            }
        } else {
            // Horizontal collision (wall)
            if (overlapLeft < overlapRight) {
                entity.getPosition().x = blockLeft - Entity.HITBOX_RIGHT_X;
                entity.setMovingRight(false);
            } else {
                entity.getPosition().x = blockRight - Entity.HITBOX_LEFT_X;
                entity.setMovingLeft(false);
            }
            entity.getVelocity().x = 0;
            // Wall‑sliding activation is handled later
        }
    }

    /**
     * Snaps the player to the ground if they are within GROUND_SNAP of a block's top.
     */
    private void snapToGround() {
        float feetY = entity.getHitboxBottom();
        float vx = entity.getVelocity().x;
        float vy = entity.getVelocity().y;

        // Only snap if moving downward or stationary vertically
        if (vy > 0) return;

        for (SolidBlock block : solidBlocks) {
            float blockTop = block.bounds.y + block.bounds.height;
            // Check horizontal overlap
            if (entity.getHitboxRight() > block.bounds.x &&
                entity.getHitboxLeft() < block.bounds.x + block.bounds.width) {
                // Feet are very close to the top?
                float dist = feetY - blockTop;
                if (dist >= 0 && dist <= GROUND_SNAP) {
                    entity.getPosition().y = blockTop - Entity.HITBOX_BOTTOM_Y;
                    entity.getVelocity().y = 0;
                    entity.setMovingDown(false);
                    entity.setOnGround(true);
                    break;
                }
            }
        }
    }

    /**
     * Checks if the player is horizontally touching any wall block, with vertical overlap.
     */
    private boolean isTouchingWall() {
        float playerLeft  = entity.getHitboxLeft();
        float playerRight = entity.getHitboxRight();
        float playerTop   = entity.getHitboxTop();
        float playerBottom= entity.getHitboxBottom();

        for (SolidBlock block : solidBlocks) {
            float blockLeft   = block.bounds.x;
            float blockRight  = block.bounds.x + block.bounds.width;
            float blockBottom = block.bounds.y;
            float blockTop    = block.bounds.y + block.bounds.height;

            // Vertical overlap must exist
            if (playerBottom < blockTop && playerTop > blockBottom) {
                // Check if perfectly flush against the wall
                if (Math.abs(playerRight - blockLeft) < 0.01f ||
                    Math.abs(playerLeft - blockRight) < 0.01f) {
                    return true;
                }
            }
        }
        return false;
    }
}

