package io.github.some_example_name.model.enitity;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.SolidBlock;

public class CollisionLogic {
    private Entity entity;
    private Array<SolidBlock> solidBlocks;

    public CollisionLogic(Entity entity, Array<SolidBlock> solidBlocks) {
        this.entity = entity;
        this.solidBlocks = solidBlocks;
    }

    public void checkCollisions() {
        boolean groundedThisFrame = false;
        boolean slidingThisFrame = false;

        for (SolidBlock s : solidBlocks) {
            if (!overlapsBlock(s)) continue;
            if (checkFloor(s))     groundedThisFrame = true;
            checkCeiling(s);
            if (checkRightWall(s)) slidingThisFrame = true;
            if (checkLeftWall(s))  slidingThisFrame = true;
        }

        if (!groundedThisFrame) {
            entity.setOnGround(false);
        }

        if (!slidingThisFrame) {
            entity.setSliding(false);
        }
    }

    private boolean overlapsBlock(SolidBlock s) {
        return entity.getRightHitBox().x  >  s.bounds.x &&
            entity.getLeftHitBox().x   <  s.bounds.x + s.bounds.width &&
            entity.getTopHitBox().y    >  s.bounds.y &&
            entity.getBottomHitBox().y <= s.bounds.y + s.bounds.height;
    }

    private boolean checkFloor(SolidBlock s) {
        float blockTop = s.bounds.y + s.bounds.height;
        boolean horizontalOverlap = entity.getRightHitBox().x > s.bounds.x &&
            entity.getLeftHitBox().x  < s.bounds.x + s.bounds.width;
        boolean crossedTop = entity.isMovingDown() &&
            entity.getBottomHitBox().y <= blockTop &&
            entity.getTopHitBox().y    >  blockTop;

        if (horizontalOverlap && crossedTop) {
            entity.getPosition().y = blockTop;
            entity.setMovingDown(false);
            entity.setOnGround(true);
            entity.setNumOfJumps(2);
            return true;
        }
        return false;
    }

    private void checkCeiling(SolidBlock s) {
        float blockBottom = s.bounds.y;
        boolean horizontalOverlap = entity.getRightHitBox().x > s.bounds.x &&
            entity.getLeftHitBox().x  < s.bounds.x + s.bounds.width;
        boolean hitCeiling = !entity.isMovingDown() &&
            entity.getTopHitBox().y    >= blockBottom &&
            entity.getBottomHitBox().y <  blockBottom;

        if (horizontalOverlap && hitCeiling) {
            entity.getPosition().y = blockBottom - 60;
            entity.setMovingDown(true);
        }
    }

    private boolean checkRightWall(SolidBlock s) {
        float blockLeft = s.bounds.x;
        boolean verticalOverlap = entity.getTopHitBox().y    > s.bounds.y &&
            entity.getBottomHitBox().y < s.bounds.y + s.bounds.height;
        boolean hitRightWall = entity.isMovingRight() &&
            entity.getRightHitBox().x >= blockLeft &&
            entity.getLeftHitBox().x  <  blockLeft;

        if (verticalOverlap && hitRightWall) {
            entity.getPosition().x = blockLeft - 50;
            entity.setMovingRight(false);
            if (!entity.isOnGround()) {
                entity.setSliding(true);
                entity.setNumOfJumps(1);
                return true;
            }
        }
        return false;
    }

    private boolean checkLeftWall(SolidBlock s) {
        float blockRight = s.bounds.x + s.bounds.width;
        boolean verticalOverlap = entity.getTopHitBox().y    > s.bounds.y &&
            entity.getBottomHitBox().y < s.bounds.y + s.bounds.height;
        boolean hitLeftWall = entity.isMovingLeft() &&
            entity.getLeftHitBox().x  <= blockRight &&
            entity.getRightHitBox().x >  blockRight;

        if (verticalOverlap && hitLeftWall) {
            entity.getPosition().x = blockRight - 35;
            entity.setMovingLeft(false);
            if (!entity.isOnGround()) {
                entity.setSliding(true);
                entity.setNumOfJumps(1);
                return true;
            }
        }
        return false;
    }
}
