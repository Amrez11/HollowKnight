package io.github.some_example_name.model.entity.enemyEntity;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.SolidBlock;

/**
 * Resolves collisions between a single EnemyEntity and the block world,
 * plus side-contact against other enemies (used by SentryBehavior to know
 * when to stop marching).
 */
public class EnemyCollisionLogic {

    private final EnemyEntity        enemy;
    private final Array<SolidBlock>  solidBlocks;
    private final Array<EnemyEntity> allEnemies;

    public EnemyCollisionLogic(EnemyEntity enemy, Array<SolidBlock> solidBlocks, Array<EnemyEntity> allEnemies) {
        this.enemy       = enemy;
        this.solidBlocks = solidBlocks;
        this.allEnemies  = allEnemies;
    }

    public void checkCollisions() {
        enemy.setOnGround(false);
        enemy.setHitWallThisFrame(false);
        enemy.setHitEnemyThisFrame(false);

        for (SolidBlock block : solidBlocks) {
            resolveBlock(block);
        }

        for (EnemyEntity other : allEnemies) {
            if (other == enemy || other.isDead()) continue;
            resolveEnemyOverlap(other);
        }
    }

    private void resolveBlock(SolidBlock block) {
        float eL = enemy.getHitboxLeft();
        float eR = enemy.getHitboxRight();
        float eB = enemy.getHitboxBottom();
        float eT = enemy.getHitboxTop();

        float bL = block.bounds.x;
        float bR = block.bounds.x + block.bounds.width;
        float bB = block.bounds.y;
        float bT = block.bounds.y + block.bounds.height;

        if (eR <= bL || eL >= bR || eT <= bB || eB >= bT) return;

        float overlapLeft   = eR - bL;
        float overlapRight  = bR - eL;
        float overlapBottom = eT - bB;
        float overlapTop    = bT - eB;

        float minX = Math.min(overlapLeft, overlapRight);
        float minY = Math.min(overlapBottom, overlapTop);

        if (minY < minX) {
            if (overlapTop < overlapBottom) {
                enemy.getPosition().y = bT - enemy.hitboxBottomY;
                enemy.getVelocity().y = 0;
                enemy.setOnGround(true);
            } else {
                enemy.getPosition().y = bB - enemy.hitboxTopY;
                if (enemy.getVelocity().y > 0) enemy.getVelocity().y = 0;
            }
        } else {
            if (overlapLeft < overlapRight) {
                enemy.getPosition().x = bL - enemy.hitboxRightX;
                enemy.setLookingRight(false);
            } else {
                enemy.getPosition().x = bR - enemy.hitboxLeftX;
                enemy.setLookingRight(true);
            }
            enemy.getVelocity().x = 0;
            enemy.setHitWallThisFrame(true);
        }
    }

    private void resolveEnemyOverlap(EnemyEntity other) {
        float eL = enemy.getHitboxLeft();
        float eR = enemy.getHitboxRight();
        float eB = enemy.getHitboxBottom();
        float eT = enemy.getHitboxTop();

        float oL = other.getHitboxLeft();
        float oR = other.getHitboxRight();
        float oB = other.getHitboxBottom();
        float oT = other.getHitboxTop();

        if (eR <= oL || eL >= oR || eT <= oB || eB >= oT) return;

        float overlapLeft   = eR - oL;
        float overlapRight  = oR - eL;
        float overlapBottom = eT - oB;
        float overlapTop    = oT - eB;

        float minX = Math.min(overlapLeft, overlapRight);
        float minY = Math.min(overlapBottom, overlapTop);

        // Only treat this as a "hit" if the contact is mostly horizontal —
        // e.g. ignore a flyer passing directly overhead a marching sentry.
        if (minX <= minY) {
            if (overlapLeft < overlapRight) {
                enemy.getPosition().x -= overlapLeft;
            } else {
                enemy.getPosition().x += overlapRight;
            }
            enemy.getVelocity().x = 0;
            enemy.setHitEnemyThisFrame(true);
        }
    }
}
