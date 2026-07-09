package io.github.some_example_name.model.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.Manager.CharmManager;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.entity.player.PlayerAttackLogic;
import io.github.some_example_name.model.enums.AnimationType;

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

        resolvePlayerAttacks(playerHitboxes, enemies, player);
        resolveEnemyHitboxesVsPlayer(enemyHitboxes, player);
        resolveEnemyContactVsPlayer(enemies, player);
    }

    // ── Player attacks → enemies ──────────────────────────────────────────────

    private void resolvePlayerAttacks(Array<AttackHitbox> hitboxes,
                                      Array<EnemyEntity>  enemies,
                                      Entity player) {
        for (AttackHitbox atk : hitboxes) {
            for (EnemyEntity enemy : enemies) {
                if (enemy.isDead()) continue;
                if (atk.hitEnemies.contains(enemy)) continue;
                if (atk.bounds.overlaps(enemy.getHitboxRect())) {
                    enemy.takeDamage(atk.damage);
                    atk.hitEnemies.add(enemy);

                    // Only nail hits grant soul — spells cost soul, they don't earn it.
                    if (atk.animationType == AnimationType.NAIL_SLASH) {
                        int soulGain = Math.round(
                            PlayerAttackLogic.SOUL_PER_HIT * CharmManager.getSoulMultiplier());
                        player.addSoul(soulGain);
                    }
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

    // Inside io.github.some_example_name.model.entity.DamageResolver

    /**
     * Call once per frame.
     * Now includes 'deadlyZones' for environmental hazards.
     */
    public void resolve(float delta,
                        Entity player,
                        Array<EnemyEntity>  enemies,
                        Array<AttackHitbox> playerHitboxes,
                        Array<AttackHitbox> enemyHitboxes,
                        Array<Rectangle>    deadlyZones) { // <--- Added parameter

        tickPlayerIFrames(delta);
        player.setDamaged(false);
        player.setInvincible(playerIFrameTimer > 0f);
        tickAndPruneHitboxes(delta, playerHitboxes);
        tickAndPruneHitboxes(delta, enemyHitboxes);

        resolvePlayerAttacks(playerHitboxes, enemies, player);
        resolveEnemyHitboxesVsPlayer(enemyHitboxes, player);
        resolveEnemyContactVsPlayer(enemies, player);

        // <--- Added the environmental damage check
        resolveDeadlyZonesVsPlayer(deadlyZones, player);
    }

    // ── Environmental Damage → player ─────────────────────────────────────────

    private void resolveDeadlyZonesVsPlayer(Array<Rectangle> deadlyZones, Entity player) {
        if (isPlayerInvincible()) return; // Don't hurt the player if they have i-frames

        rebuildPlayerRect(player);

        for (Rectangle zone : deadlyZones) {
            if (playerRect.overlaps(zone)) {
                hitPlayer(player, 3); // Applies exactly 3 damage as requested
                return; // Break out early so we only take damage once per frame
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
        // [FIXED] A 0-damage hit (e.g. Zote's harmless tantrum swing) should
        // be a complete no-op for the player — no hurt flash, no i-frames.
        // Only real damage should trigger the feedback/invincibility window.
        if (damage <= 0) return;

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
