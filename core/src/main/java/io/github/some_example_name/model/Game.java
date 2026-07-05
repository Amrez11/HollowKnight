package io.github.some_example_name.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.DamageResolver;
import io.github.some_example_name.model.entity.enemyEntity.EnemyCollisionLogic;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.BossBehavior;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.CrystalFlyerBehavior;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.LaserFlyerBehavior;
import io.github.some_example_name.model.entity.player.CollisionLogic;
import io.github.some_example_name.model.entity.player.Entity;

public class Game {

    private Entity             player;
    private CollisionLogic     playerCollision;

    private final Array<EnemyEntity>        enemies        = new Array<>();
    private final Array<EnemyCollisionLogic> enemyCollisions = new Array<>();

    private final Array<AttackHitbox> playerHitboxes = new Array<>();
    private final Array<AttackHitbox> enemyHitboxes  = new Array<>();   // ← was missing

    private final DamageResolver damageResolver = new DamageResolver();

    private Array<SolidBlock> solidBlocks;
    private boolean wasAttacking = false;

    // ── Visual-only draw offsets ──────────────────────────────────────────────
    private static final float NAIL_DRAW_OFFSET_X_RIGHT     = -180f;
    private static final float NAIL_DRAW_OFFSET_X_LEFT      = -140f;
    private static final float NAIL_DRAW_OFFSET_Y           = -30f;
    private static final float WRAITH_DRAW_OFFSET_X         = -110f;
    private static final float WRAITH_DRAW_OFFSET_Y         = 0f;
    private static final float VENGEFUL_DRAW_OFFSET_X_RIGHT = -180f;
    private static final float VENGEFUL_DRAW_OFFSET_X_LEFT  = -140f;
    private static final float VENGEFUL_DRAW_OFFSET_Y       = -130f;

    // ─────────────────────────────────────────────────────────────────────────

    public void loadRoom() {
        player = new Entity();
        player.setPosition(new Vector2(1270.21f, 6800f));
        playerCollision = new CollisionLogic(player, solidBlocks);


        spawnEnemy(EnemyEntity.crawler(new Vector2(1270.21f, 7000f)));
        spawnEnemy(EnemyEntity.sentry(new Vector2(1400f, 7000f)));
        spawnEnemy(EnemyEntity.flyer(new Vector2(1270.21f, 6800f)));
        spawnEnemy(EnemyEntity.laserFlyer(new Vector2(1500f, 6800f)));
        spawnEnemy(EnemyEntity.boss(new Vector2(1270.21f, 4400f)));
    }

    private void spawnEnemy(EnemyEntity enemy) {
        enemies.add(enemy);
        enemyCollisions.add(new EnemyCollisionLogic(enemy, solidBlocks, enemies));
    }

    // ─────────────────────────────────────────────────────────────────────────

    public void update(float delta) {
        // 1. Player
        player.update(delta);
        playerCollision.checkCollisions();

        // 2. Enemies — ONE loop only; drain boss hitboxes in the same pass
        for (int i = 0; i < enemies.size; i++) {
            EnemyEntity e = enemies.get(i);
            if (e.isDead()) continue;

            e.update(delta, player);
            enemyCollisions.get(i).checkCollisions();

            // Collect any attack hitboxes the boss spawned this frame
            if (e.getBehavior() instanceof BossBehavior) {
                enemyHitboxes.addAll(((BossBehavior) e.getBehavior()).drainPendingHitboxes());
            } else if (e.getBehavior() instanceof CrystalFlyerBehavior) {
                enemyHitboxes.addAll(((CrystalFlyerBehavior) e.getBehavior()).drainPendingHitboxes());
            } else if (e.getBehavior() instanceof LaserFlyerBehavior) {
                enemyHitboxes.addAll(((LaserFlyerBehavior) e.getBehavior()).drainPendingHitboxes());
            }
        }

        // 3. Player attack hitboxes
        populatePlayerHitboxes();

        // 4. Damage resolution — both hitbox lists
        damageResolver.resolve(delta, player, enemies, playerHitboxes, enemyHitboxes);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void populatePlayerHitboxes() {
        boolean facingRight = player.isLookingRight();

        // Nail swing — spawn once on the rising edge of isAttacking()
        boolean attackingNow = player.isAttacking();
        if (attackingNow && !wasAttacking) {
            float spawnX = facingRight
                ? player.getHitboxRight()
                : player.getHitboxLeft() - 50f;
            AttackHitbox hb = AttackHitbox.nailSwing(
                spawnX, player.getHitboxBottom() + 10f,
                50f, 30f, 1, facingRight);
            hb.drawOffsetX = facingRight ? NAIL_DRAW_OFFSET_X_RIGHT : NAIL_DRAW_OFFSET_X_LEFT;
            hb.drawOffsetY = NAIL_DRAW_OFFSET_Y;
            playerHitboxes.add(hb);
        }
        wasAttacking = attackingNow;

        // Howling Wraiths
        if (player.isHowlingHitTriggered()) {
            player.setHowlingHitTriggered(false);
            float centerX = player.getPosition().x
                + (Entity.HITBOX_LEFT_X + Entity.HITBOX_RIGHT_X) / 2f;
            AttackHitbox hb = AttackHitbox.howlingWraiths(
                centerX, player.getHitboxTop(), 2, facingRight);
            hb.drawOffsetX = WRAITH_DRAW_OFFSET_X;
            hb.drawOffsetY = WRAITH_DRAW_OFFSET_Y;
            playerHitboxes.add(hb);
        }

        // Vengeful Spirit
        if (player.isVengefulFireTriggered()) {
            player.setVengefulFireTriggered(false);
            float spawnX = facingRight
                ? player.getHitboxRight()
                : player.getHitboxLeft() - 24f;
            AttackHitbox hb = AttackHitbox.vengefulSpirit(
                spawnX, player.getHitboxTop() - 20f, 2, facingRight);
            hb.drawOffsetX = facingRight ? VENGEFUL_DRAW_OFFSET_X_RIGHT : VENGEFUL_DRAW_OFFSET_X_LEFT;
            hb.drawOffsetY = VENGEFUL_DRAW_OFFSET_Y;
            playerHitboxes.add(hb);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    public void init(Array<SolidBlock> solidBlocks) {
        this.solidBlocks = solidBlocks;
        playerCollision  = new CollisionLogic(player, solidBlocks);
    }

    public Entity              getPlayer()        { return player; }
    public Array<EnemyEntity>  getEnemies()       { return enemies; }
    public Array<AttackHitbox> getPlayerHitboxes(){ return playerHitboxes; }
    public Array<AttackHitbox> getEnemyHitboxes() { return enemyHitboxes; }
}
