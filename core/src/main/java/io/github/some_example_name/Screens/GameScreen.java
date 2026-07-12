package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Manager.AchievementManager;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.SaveInfo.GameSaveData;
import io.github.some_example_name.controller.GameProcessor;
import io.github.some_example_name.controller.SettingsMenuController;
import io.github.some_example_name.model.Game;
import io.github.some_example_name.model.SolidBlock;
import io.github.some_example_name.model.TiledMapHelper;
import io.github.some_example_name.model.costumActors.Hud;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.enemyEntity.enemyBehavior.BossBehavior;
import io.github.some_example_name.model.entity.npc.ZoteEntity;
import io.github.some_example_name.model.enums.AnimationType;
import io.github.some_example_name.model.enums.MusicType;

import java.util.HashMap;

public class GameScreen extends AbstractScreen{
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameProcessor gameProcessor;
    private final Game game;

    // ── Camera shake ─────────────────────────────────────────────────────────
    // shakeMagnitude is the peak offset in pixels; shakeTimer/shakeDuration
    // together drive a linear decay from full strength down to 0 so the shake
    // eases out instead of cutting off abruptly.
    private float shakeTimer     = 0f;
    private float shakeDuration  = 0f;
    private float shakeMagnitude = 0f;

    private void triggerShake(float duration, float magnitude) {
        // Only replace an in-progress shake with a stronger one — a boss slam
        // shouldn't get swallowed by a weaker hit-flinch shake still playing.
        if (magnitude >= shakeMagnitude || shakeTimer <= 0f) {
            shakeDuration  = duration;
            shakeTimer     = duration;
            shakeMagnitude = magnitude;
        }
    }
    private final String slotId;
    private final GameSaveData saveData;
    Vector3 target = new Vector3();
    private OrthogonalTiledMapRenderer mapRenderer;

    private Array<Rectangle> cameraBounds;
    private HashMap<Rectangle, Texture> roomBackgrounds; // Holds the custom backgrounds
    private HashMap<Rectangle, MusicType> roomMusic;      // Which track loops while the player is in each room
    // The camera-bound room the player was inside last frame. Used to detect
    // the moment they leave a room, so its dead enemies can be respawned.
    private Rectangle previousBound;

    private TiledMapHelper mapHelper;
    private Rectangle bossRoom;
    private Rectangle bossDoor;
    private Vector2 bossSpawnPos;
    private boolean bossFightStarted = false;
    private TiledMap map;
    private Array<SolidBlock> solidBlocks;
    Array<Rectangle> deadlyZones ;
    private OrthographicCamera hudCamera;
    private Hud hud;
    private SettingsMenuController settingsMenuController;


    private final int[] background={0,1,2,3};
    private final int[] foreground={4,5};

    /** Fresh, unsaved run — e.g. "play again" from the victory screen. */
    public GameScreen(Game game) {
        this(game, "quicksave", null);
    }

    /**
     * @param slotId   which save slot this playthrough is tied to; the pause
     * menu's Save button writes here.
     * @param saveData if non-null, the run's state is restored from this save
     * right after the room's default entities are spawned.
     */
    public GameScreen(Game game, String slotId, GameSaveData saveData) {
        this.game = game;
        this.slotId = slotId;
        this.saveData = saveData;
    }


    @Override
    public void show() {
        super.show();
        mapHelper=new TiledMapHelper();
        map=mapHelper.load("/Users/amrez/Desktop/map1/betterrrr.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        solidBlocks = mapHelper.getSolidBlock();
        cameraBounds = mapHelper.getCameraBounds();
        deadlyZones = mapHelper.getDeadlyZones();

        roomBackgrounds = new HashMap<>();


        if (cameraBounds.size >= 4) {
            roomBackgrounds.put(cameraBounds.get(0), new Texture("ChatGPT Image Jul 10, 2026 at 06_18_14 AM.png"));
            roomBackgrounds.put(cameraBounds.get(1), new Texture("cd_room_BG_02.png"));
            roomBackgrounds.put(cameraBounds.get(2), new Texture("ChatGPT Image Jul 10, 2026 at 06_18_14 AM.png"));
            roomBackgrounds.put(cameraBounds.get(3), new Texture("ChatGPT Image Jul 10, 2026 at 06_18_14 AM.png"));

            roomMusic = new HashMap<>();
            roomMusic.put(cameraBounds.get(0), MusicType.ROOM_1);
            roomMusic.put(cameraBounds.get(1), MusicType.ROOM_2);
            roomMusic.put(cameraBounds.get(2), MusicType.ROOM_3);
            roomMusic.put(cameraBounds.get(3), MusicType.BOSS_ROOM_AMBIENT);
        }
        // -----------------------------------

        game.init(solidBlocks,deadlyZones);
        game.loadRoom();
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hud = new Hud(game.getPlayer().getMaxHp());

        batch=new SpriteBatch();
        camera=new OrthographicCamera();
        camera.zoom=0.7f;

        viewport=new ScreenViewport(camera);
        shapeRenderer=new ShapeRenderer();
        settingsMenuController = new SettingsMenuController();
        gameProcessor=new GameProcessor(game, slotId, settingsMenuController);
        InputMultiplexer inputMultiplexer=new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
        MapLayer spawnLayer=map.getLayers().get("Logic");
        MapObject spawnPoint=spawnLayer.getObjects().get("playerSpawnPoint");
        float spawnX=spawnPoint.getProperties().get("x", Float.class);
        float spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.getPlayer().setPosition(new Vector2(spawnX,spawnY));
        game.setPlayerSpawnPoint(new Vector2(spawnX, spawnY));


        spawnPoint=spawnLayer.getObjects().get("zoteSpawn");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        Vector2 zotePos = new Vector2(spawnX , spawnY);
        game.spawnZote(zotePos);

        spawnPoint=spawnLayer.getObjects().get("enemy3");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.spawnEnemy(EnemyEntity.flyer(new Vector2(spawnX,spawnY)));


        spawnPoint=spawnLayer.getObjects().get("enemy4");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.spawnEnemy(EnemyEntity.crawler(new Vector2(spawnX,spawnY)));

        spawnPoint=spawnLayer.getObjects().get("enemy5");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.spawnEnemy(EnemyEntity.crawler(new Vector2(spawnX,spawnY)));


        spawnPoint=spawnLayer.getObjects().get("enemy6");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.spawnEnemy(EnemyEntity.flyer(new Vector2(spawnX,spawnY)));


        spawnLayer=map.getLayers().get("boss");
        spawnPoint=spawnLayer.getObjects().get("enemy1");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.spawnEnemy(EnemyEntity.laserFlyer(new Vector2(spawnX,spawnY)));



        spawnLayer=map.getLayers().get("boss");
        spawnPoint=spawnLayer.getObjects().get("enemy2");
        spawnX=spawnPoint.getProperties().get("x", Float.class);
        spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.spawnEnemy(EnemyEntity.sentry(new Vector2(spawnX,spawnY)));

        bossRoom = mapHelper.getNamedRectangle("boss", "bossRoom");
        bossDoor = mapHelper.getNamedRectangle("boss", "bossDoor");

        spawnLayer = map.getLayers().get("boss");
        spawnPoint = spawnLayer.getObjects().get("bossSpawnPoint");
        spawnX = spawnPoint.getProperties().get("x", Float.class);
        spawnY = spawnPoint.getProperties().get("y", Float.class);
        bossSpawnPos = new Vector2(spawnX, spawnY);











        AchievementManager.resetSession();

        // Override the freshly-spawned defaults above with the saved run, if any.
        if (saveData != null) {
            game.applySave(saveData);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        hudCamera.setToOrtho(false, width, height);
        viewport.update(width,height);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);
        if (!game.isPaused()) {
            // --- BOSS TRIGGER LOGIC ---
            if (!bossFightStarted && bossRoom != null) {
                float px = game.getPlayer().getPosition().x;
                float py = game.getPlayer().getPosition().y;

                // If the player steps inside the boss room rectangle
                if (bossRoom.contains(px, py)) {
                    bossFightStarted = true;

                    // 1. Lock the door by turning it into a SolidBlock
                    if (bossDoor != null) {
                        solidBlocks.add(new SolidBlock(bossDoor.x, bossDoor.y, bossDoor.width, bossDoor.height));
                        System.out.println("Door locked!");
                    }

                    // 2. Spawn the boss NOW
                    game.spawnEnemy(EnemyEntity.boss(bossSpawnPos));
                    System.out.println("Boss spawned!");

                    // 3. Swap the ambient boss-room track for the fight theme
                    GameAssetManager.playMusic(MusicType.BOSS_THEME);
                }
            }

            game.update(delta);
            hud.update(delta, game.getPlayer());

            if (game.getPlayer().isDamaged()) {
                triggerShake(0.6f, 30f);
            }

            for (EnemyEntity e : game.getEnemies()) {
                if (e.getBehavior() instanceof BossBehavior) {
                    BossBehavior boss = (BossBehavior) e.getBehavior();
                    if (boss.isPoundLanded()) {
                        triggerShake(0.45f, 26f);
                        boss.clearPoundLanded();
                    }
                }
            }
        }
        target.set(game.getPlayer().getPosition().x, game.getPlayer().getPosition().y + 120, 0);
        camera.position.lerp(target, 0.1f);

        Rectangle currentBound = null;
        float playerX = game.getPlayer().getPosition().x;
        float playerY = game.getPlayer().getPosition().y;

        for (Rectangle bound : cameraBounds) {
            if (bound.contains(playerX, playerY)) {
                currentBound = bound;
                break;
            }
        }


        if (previousBound != null && previousBound != currentBound) {
            game.respawnDeadEnemiesInRoom(previousBound);
        }

        if (currentBound != previousBound && currentBound != null && !bossFightStarted && roomMusic != null) {
            MusicType track = roomMusic.get(currentBound);
            if (track != null) GameAssetManager.playMusic(track);
        }
        previousBound = currentBound;

        if (currentBound != null) {

            float camHalfWidth = (camera.viewportWidth * camera.zoom) / 2f;
            float camHalfHeight = (camera.viewportHeight * camera.zoom) / 2f;


            if (currentBound.width < camHalfWidth * 2) {

                camera.position.x = currentBound.x + currentBound.width / 2f;
            } else {

                float minX = currentBound.x + camHalfWidth;
                float maxX = currentBound.x + currentBound.width - camHalfWidth;
                camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
            }

            // Y-Axis Clamping
            if (currentBound.height < camHalfHeight * 2) {
                camera.position.y = currentBound.y + currentBound.height / 2f;
            } else {
                float minY = currentBound.y + camHalfHeight;
                float maxY = currentBound.y + currentBound.height - camHalfHeight;
                camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
            }
        }

        // Shake is applied last, after room clamping, so it isn't clamped away
        // and so it offsets whatever position the camera settled on this frame.
        if (shakeTimer > 0f) {
            shakeTimer -= delta;
            float t   = Math.max(0f, shakeTimer / shakeDuration); // 1 -> 0
            float amp = shakeMagnitude * t;
            camera.position.x += MathUtils.random(-amp, amp);
            camera.position.y += MathUtils.random(-amp, amp);
        }

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // --- DRAW THE CUSTOM ROOM BACKGROUND AT THE VERY BACK ---
        batch.begin();
        if (currentBound != null && roomBackgrounds.containsKey(currentBound)) {
            Texture bgTexture = roomBackgrounds.get(currentBound);
            // This stretches the background perfectly inside the camera bounds rectangle
            batch.draw(bgTexture, currentBound.x, currentBound.y, currentBound.width, currentBound.height);
        }
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render(background);

        batch.begin();
        AnimationType currentAnimation=game.getPlayer().getCurrentAnimation();
        Animation<TextureRegion> animation= GameAssetManager.animationMap.get(currentAnimation);
        TextureRegion keyFrame= animation.getKeyFrame(game.getPlayer().getStateTime());
        batch.draw(keyFrame,
            game.getPlayer().getPosition().x-127,game.getPlayer().getPosition().y,
            keyFrame.getRegionWidth()/2,0,
            keyFrame.getRegionWidth(),keyFrame.getRegionHeight(),
            game.getPlayer().isLookingRight() ? -1:1 ,1,0) ;

        for (EnemyEntity enemy : game.getEnemies()) {
            AnimationType currentAnimationEnemy=enemy.getCurrentAnimation();
            Animation<TextureRegion> animationEnemy= GameAssetManager.animationMap.get(currentAnimationEnemy);
            TextureRegion keyFrameEnemy= animationEnemy.getKeyFrame(enemy.getStateTime());
            boolean facingRight = enemy.isLookingRight();
            // Crawler art is drawn facing right by default, opposite of every other sprite in this game
            boolean isCrawler = currentAnimationEnemy == AnimationType.CRAWLER_IDLE
                || currentAnimationEnemy == AnimationType.CRAWLER_WALK
                || currentAnimationEnemy == AnimationType.CRAWLER_LUNGE;
            if (isCrawler) facingRight = !facingRight;

            batch.draw(keyFrameEnemy,
                enemy.getPosition().x,
                enemy.getPosition().y, keyFrameEnemy.getRegionWidth()/2, 0,
                keyFrameEnemy.getRegionWidth(),
                keyFrameEnemy.getRegionHeight(), facingRight ? -1 : 1,
                1, 0);
        }

        for (AttackHitbox h : game.getPlayerHitboxes()) {
            Animation<TextureRegion> animEffect = GameAssetManager.animationMap.get(h.animationType);
            TextureRegion frameEffect = animEffect.getKeyFrame(h.stateTime);
            batch.draw(frameEffect,
                h.bounds.x + h.drawOffsetX, h.bounds.y + h.drawOffsetY,
                frameEffect.getRegionWidth()/2f, 0,
                frameEffect.getRegionWidth(), frameEffect.getRegionHeight(),
                h.lookingRight ? 1:-1, 1, 0);
        }
        for (AttackHitbox h : game.getEnemyHitboxes()) {
            Animation<TextureRegion> animEffect = GameAssetManager.animationMap.get(h.animationType);
            TextureRegion frameEffect = animEffect.getKeyFrame(h.stateTime);
            batch.draw(frameEffect,
                h.bounds.x + h.drawOffsetX, h.bounds.y + h.drawOffsetY,
                h.bounds.width, h.bounds.height);
        }

        // ── Zote (NPC) ──────────────────────────────────────────────────────
        ZoteEntity zote = game.getZote();
        if (zote != null) {
            Animation<TextureRegion> zoteAnim = GameAssetManager.animationMap.get(zote.getCurrentAnimation());
            TextureRegion zoteFrame = zoteAnim.getKeyFrame(zote.getStateTime());
            batch.draw(zoteFrame,
                zote.getPosition().x, zote.getPosition().y,
                zoteFrame.getRegionWidth()/2f, 0,
                zoteFrame.getRegionWidth(), zoteFrame.getRegionHeight(),
                zote.isLookingRight() ? -1 : 1, 1, 0);

            if (game.isNearZote() && !game.isZoteDialogueActive()) {
                GameAssetManager.dialogueFont.draw(batch, "[F] Talk",
                    zote.getPosition().x, zote.getHitboxTop() + 40f);
            }
        }

        batch.end();
        mapRenderer.render(foreground);

        // ── [FIXED] PRO-LEVEL DEBUG RENDERING ────────────────────────────────
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // 1. Environment (Green)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0,0,100,0);
        shapeRenderer.line(0,0,0,100);


        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(game.getPlayer().getPosition().x+20,game.getPlayer().getPosition().y,50,100);
        for (EnemyEntity e:game.getEnemies()){
            shapeRenderer.rect(e.getPosition().x+e.hitboxLeftX,e.getPosition().y+e.hitboxBottomY,e.hitboxRightX-e.hitboxLeftX,e.hitboxTopY-e.hitboxBottomY);
        }


        shapeRenderer.setColor(Color.YELLOW);
        for (AttackHitbox h : game.getPlayerHitboxes()) {
            shapeRenderer.rect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
            float cx = h.bounds.x + h.bounds.width / 2f;
            float cy = h.bounds.y + h.bounds.height / 2f;
            shapeRenderer.circle(cx, cy, Math.max(h.bounds.width, h.bounds.height) / 2f, 24);
        }


        shapeRenderer.setColor(Color.RED);
        for (AttackHitbox h : game.getEnemyHitboxes()) {
            shapeRenderer.rect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
        }

        shapeRenderer.setColor(Color.MAGENTA);
        for (Rectangle r : deadlyZones) {
            shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }

        shapeRenderer.end();
        // ─────────────────────────────────────────────────────────────────────

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(-1000,-200 ,2000,200);
        shapeRenderer.end();

        hudCamera.update();
        if (zote != null && zote.isDialogueActive()) {
            shapeRenderer.setProjectionMatrix(hudCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            zote.getDialogueBox().renderPanel(shapeRenderer);
            shapeRenderer.end();
        }

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        hud.render(batch, game.getPlayer());
        if (zote != null && zote.isDialogueActive()) {
            zote.getDialogueBox().renderText(batch, GameAssetManager.dialogueFont);
        }
        batch.end();

        super.render(delta);
    }


    @Override
    public void dispose() {
        GameAssetManager.stopMusic();
        if (roomBackgrounds != null) {
            for (Texture bg : roomBackgrounds.values()) {
                bg.dispose();
            }
        }
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        super.dispose();
    }
}
