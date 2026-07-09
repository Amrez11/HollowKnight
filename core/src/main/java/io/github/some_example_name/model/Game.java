package io.github.some_example_name.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.Manager.AchievementManager;
import io.github.some_example_name.Manager.CharmManager;
import io.github.some_example_name.SaveInfo.EnemySaveData;
import io.github.some_example_name.SaveInfo.GameSaveData;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.DamageResolver;
import io.github.some_example_name.model.entity.enemyEntity.EnemyCollisionLogic;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.BossBehavior;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.CrystalFlyerBehavior;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.IEnemyBehavior;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.LaserFlyerBehavior;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.SentryBehavior;
import io.github.some_example_name.model.entity.npc.ZoteEntity;
import io.github.some_example_name.model.entity.player.CollisionLogic;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.Achievement;
import io.github.some_example_name.model.enums.Charm;

import java.util.HashSet;
import java.util.Set;

public class Game {

    private Entity             player;
    private CollisionLogic     playerCollision;

    private final Array<EnemyEntity>        enemies        = new Array<>();
    private final Array<EnemyCollisionLogic> enemyCollisions = new Array<>();

    private final Array<AttackHitbox> playerHitboxes = new Array<>();
    private final Array<AttackHitbox> enemyHitboxes  = new Array<>();   // ← was missing

    private final DamageResolver damageResolver = new DamageResolver();


    // Not in `enemies` on purpose — see ZoteEntity's class doc.
    private ZoteEntity zote;

    private Array<SolidBlock> solidBlocks;
    Array<Rectangle> deadlyZones;
    private boolean wasAttacking = false;
    private boolean paused = false;

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
        enemies.clear();
        enemyCollisions.clear();

        player = new Entity();
        player.setPosition(new Vector2(1270.21f, 6800f));
        playerCollision = new CollisionLogic(player, solidBlocks);
    }

    public void spawnEnemy(EnemyEntity enemy) {
        enemies.add(enemy);
        enemyCollisions.add(new EnemyCollisionLogic(enemy, solidBlocks, enemies));
    }

    public void spawnZote(Vector2 pos) {
        zote = new ZoteEntity(pos);
    }

    // ─────────────────────────────────────────────────────────────────────────

    public void update(float delta) {
        io.github.some_example_name.Manager.AchievementManager.updateTimer(delta);
        player.update(delta);
        playerCollision.checkCollisions();
        if (player.isOnBoss()) {
            player.getPosition().set(this.getEnemies().get(6).getPosition());
        }

        // 2. Enemies — ONE loop only; drain boss hitboxes in the same pass
        for (int i = 0; i < enemies.size; i++) {
            EnemyEntity e = enemies.get(i);
            if (e.isDead() && !e.isDeathProcessed()) {
                String type = "CRAWLER";
                if (e.getBehavior() instanceof BossBehavior) type = "BOSS";
                else if (e.getBehavior() instanceof LaserFlyerBehavior) type = "LASER_FLYER";
                else if (e.getBehavior() instanceof CrystalFlyerBehavior) type = "FLYER";
                e.setDeathProcessed(true);
                io.github.some_example_name.Manager.AchievementManager.onEnemyDefeated(type);
                if (type.equals("BOSS")) {
                    io.github.some_example_name.Manager.AchievementManager.onGameCompleted(); // Triggers Speedrun/Completion achievements
                    io.github.some_example_name.Manager.UiManager.setScreen(new io.github.some_example_name.Screens.VictoryScreen());
                }

            }
            if (e.isDead()) continue;

            e.update(delta, player);
            enemyCollisions.get(i).checkCollisions();


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

        // 3b. Zote (NPC) — checks nail hits against playerHitboxes itself,
        // then hands over any attack hitbox it spawned this frame so it goes
        // through the same rendering/damage pipeline as every other enemy.
        if (zote != null) {
            zote.update(delta, player, playerHitboxes);
            enemyHitboxes.addAll(zote.drainPendingHitboxes());
        }

        // 4. Damage resolution — both hitbox lists
        damageResolver.resolve(delta, player, enemies, playerHitboxes, enemyHitboxes,deadlyZones);
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
            int nailDamage = Math.round(1 * CharmManager.getNailDamageMultiplier());
            AttackHitbox hb = AttackHitbox.nailSwing(
                spawnX, player.getHitboxBottom() + 10f,
                50f, 30f, nailDamage, facingRight);
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

    public void init(Array<SolidBlock> solidBlocks, Array<Rectangle> deadlyZones) {
        this.solidBlocks = solidBlocks;
        this.deadlyZones = deadlyZones; // <-- Save the deadly zones
        playerCollision  = new CollisionLogic(player, solidBlocks);
    }

    public boolean isPaused()             { return paused; }
    public void    setPaused(boolean paused) { this.paused = paused; }

    public Entity              getPlayer()        { return player; }
    public Array<EnemyEntity>  getEnemies()       { return enemies; }
    public Array<AttackHitbox> getPlayerHitboxes(){ return playerHitboxes; }
    public Array<AttackHitbox> getEnemyHitboxes() { return enemyHitboxes; }

    // ── Zote (NPC) ────────────────────────────────────────────────────────
    public ZoteEntity getZote()             { return zote; }
    public boolean    isNearZote()          { return zote != null && zote.isPlayerInRange(player); }
    public boolean    isZoteDialogueActive(){ return zote != null && zote.isDialogueActive(); }
    public void       interactWithZote()    { if (zote != null) zote.tryOpenDialogue(player); }
    public void       advanceZoteDialogue() { if (zote != null) zote.advanceDialogue(player); }

    // ─────────────────────────────────────────────────────────────────────────
    // Save / Load
    // ─────────────────────────────────────────────────────────────────────────

    /** Maps an enemy's IEnemyBehavior back to the factory-type string used in saves. */
    private String enemyTypeOf(EnemyEntity e) {
        IEnemyBehavior b = e.getBehavior();
        if (b instanceof BossBehavior)        return "BOSS";
        if (b instanceof LaserFlyerBehavior)  return "LASER_FLYER";
        if (b instanceof CrystalFlyerBehavior) return "FLYER";
        if (b instanceof SentryBehavior)      return "SENTRY";
        return "CRAWLER";
    }

    private EnemyEntity createEnemyOfType(String type, Vector2 pos) {
        return switch (type) {
            case "BOSS"        -> EnemyEntity.boss(pos);
            case "LASER_FLYER" -> EnemyEntity.laserFlyer(pos);
            case "FLYER"       -> EnemyEntity.flyer(pos);
            case "SENTRY"      -> EnemyEntity.sentry(pos);
            default            -> EnemyEntity.crawler(pos);
        };
    }

    /** Snapshots the entire current run — player, charms, progress, and every enemy. */
    public GameSaveData captureSave(String saveName) {
        GameSaveData data = new GameSaveData();
        data.saveName = saveName;
        data.timestamp = System.currentTimeMillis();
        data.playTimeSeconds = AchievementManager.getGameTimer();

        data.playerX = player.getPosition().x;
        data.playerY = player.getPosition().y;
        data.hp = player.getHp();
        data.soul = player.getSoul();
        data.lookingRight = player.isLookingRight();

        for (Charm c : Charm.values()) {
            if (CharmManager.isEquipped(c)) data.equippedCharms.add(c.name());
        }

        for (Achievement a : Achievement.values()) {
            if (AchievementManager.isUnlocked(a)) data.unlockedAchievements.add(a.name());
        }
        data.enemyTypesDefeated.addAll(AchievementManager.getEnemiesDefeatedTypes());
        data.enemiesKilledCount = AchievementManager.getEnemiesKilledCount();
        data.playerDeathCount   = AchievementManager.getPlayerDeathCount();
        data.gameTimer          = AchievementManager.getGameTimer();

        for (EnemyEntity e : enemies) {
            data.enemies.add(new EnemySaveData(
                enemyTypeOf(e), e.getPosition().x, e.getPosition().y, e.getHp(), e.isDead()));
        }
        return data;
    }

    /**
     * Restores a full run from a save. Assumes init()/loadRoom() (and the
     * default room spawns) have already run — this overrides player stats,
     * charms, progress, and replaces the enemy list with the saved one.
     */
    public void applySave(GameSaveData data) {
        player.getPosition().set(data.playerX, data.playerY);
        player.setHp(data.hp);
        player.setSoul(data.soul);
        player.setLookingRight(data.lookingRight);
        player.resetTransientCombatState();

        CharmManager.clearAll();
        for (String name : data.equippedCharms) {
            try { CharmManager.equipDirect(Charm.valueOf(name)); } catch (IllegalArgumentException ignored) {}
        }

        Set<Achievement> unlocked = new HashSet<>();
        for (String name : data.unlockedAchievements) {
            try { unlocked.add(Achievement.valueOf(name)); } catch (IllegalArgumentException ignored) {}
        }
        Set<String> enemiesDefeated = new HashSet<>(data.enemyTypesDefeated);
        AchievementManager.restoreState(unlocked, enemiesDefeated,
            data.enemiesKilledCount, data.playerDeathCount, data.gameTimer);

        enemies.clear();
        enemyCollisions.clear();
        for (EnemySaveData es : data.enemies) {
            EnemyEntity e = createEnemyOfType(es.type, new Vector2(es.x, es.y));
            e.applySavedState(es.hp, es.dead);
            enemies.add(e);
            enemyCollisions.add(new EnemyCollisionLogic(e, solidBlocks, enemies));
        }
    }
}
