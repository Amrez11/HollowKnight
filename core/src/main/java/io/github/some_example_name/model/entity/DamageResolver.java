package io.github.some_example_name.model.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;

public class DamageResolver {

    private static final float PLAYER_I_FRAME_DURATION = 1.0f;
    private              float playerIFrameTimer        = 0f;

    private final Rectangle playerRect = new Rectangle();

    /**
     * Call once per frame. Pass both hitbox lists:
     * playerHitboxes — created by the player's attacks, tested against enemies
     * enemyHitboxes  — created by boss attack spawners, tested against the player
     */
    public void resolve(float delta,
                        Entity player,
                        Array<EnemyEntity>  enemies,
                        Array<AttackHitbox> playerHitboxes,
                        Array<AttackHitbox> enemyHitboxes) {

        tickPlayerIFrames(delta);
        player.setDamaged(false);
        player.setInvincible(playerIFrameTimer > 0f);
        tickAndPruneHitboxes(delta, playerHitboxes);
        tickAndPruneHitboxes(delta, enemyHitboxes);

        resolvePlayerAttacks(playerHitboxes, enemies);
        resolveEnemyHitboxesVsPlayer(enemyHitboxes, player);
        resolveEnemyContactVsPlayer(enemies, player);
    }

    // ── Player attacks → enemies ──────────────────────────────────────────────

    private void resolvePlayerAttacks(Array<AttackHitbox> hitboxes,
                                      Array<EnemyEntity>  enemies) {
        for (AttackHitbox atk : hitboxes) {
            for (EnemyEntity enemy : enemies) {
                if (enemy.isDead()) continue;
                if (atk.hitEnemies.contains(enemy)) continue;
                if (atk.bounds.overlaps(enemy.getHitboxRect())) {
                    enemy.takeDamage(atk.damage);
                    atk.hitEnemies.add(enemy);
                }
            }
        }
    }

    // ── Enemy attack hitboxes → player ────────────────────────────────────────

    private void resolveEnemyHitboxesVsPlayer(Array<AttackHitbox> hitboxes,
                                              Entity player) {
        if (isPlayerInvincible()) return;
        rebuildPlayerRect(player);

        for (AttackHitbox atk : hitboxes) {
            // [FIXED] Ignore this hitbox if it has already damaged the player
            if (atk.hasHitPlayer) continue;

            if (playerRect.overlaps(atk.bounds)) {
                hitPlayer(player, atk.damage);

                // [FIXED] Mark as having hit the player instead of destroying it.
                // This allows projectile hitboxes (like shockwaves) to continue traveling visually.
                atk.hasHitPlayer = true;

                return;   // one hit per frame is enough
            }
        }
    }

    // ── Enemy body contact → player ───────────────────────────────────────────

    private void resolveEnemyContactVsPlayer(Array<EnemyEntity> enemies,
                                             Entity player) {
        if (isPlayerInvincible()) return;
        rebuildPlayerRect(player);

        for (EnemyEntity enemy : enemies) {
            if (enemy.isDead()) continue;
            if (playerRect.overlaps(enemy.getHitboxRect())) {
                hitPlayer(player, enemy.getBehavior().getContactDamage());
                return;
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void tickPlayerIFrames(float delta) {
        playerIFrameTimer = Math.max(0f, playerIFrameTimer - delta);
    }

    private boolean isPlayerInvincible() {
        return playerIFrameTimer > 0f;
    }

    private void hitPlayer(Entity player, int damage) {
        player.setHp(Math.max(0, player.getHp() - damage));
        player.setDamaged(true);
        player.setFlashDuration(0.1f);
        player.setInvincible(true);
        playerIFrameTimer = PLAYER_I_FRAME_DURATION;
    }

    private void tickAndPruneHitboxes(float delta, Array<AttackHitbox> hitboxes) {
        for (int i = hitboxes.size - 1; i >= 0; i--) {
            hitboxes.get(i).tick(delta);
            if (hitboxes.get(i).isExpired()) hitboxes.removeIndex(i);
        }
    }

    private void rebuildPlayerRect(Entity player) {
        playerRect.set(
            player.getHitboxLeft(),
            player.getHitboxBottom(),
            Entity.HITBOX_WIDTH,
            Entity.HITBOX_HEIGHT);
    }
}
