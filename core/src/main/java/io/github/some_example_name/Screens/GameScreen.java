package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.controller.GameProcessor;
import io.github.some_example_name.model.Game;
import io.github.some_example_name.model.SolidBlock;
import io.github.some_example_name.model.TiledMapHelper;
import io.github.some_example_name.model.costumActors.Hud;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.enums.AnimationType;

public class GameScreen extends AbstractScreen{
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameProcessor gameProcessor;
    private final Game game;
    Vector3 target = new Vector3();
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapHelper mapHelper;
    private TiledMap map;
    private Array<SolidBlock> solidBlocks;
    private OrthographicCamera hudCamera;
    private Hud hud;


    private final int[] background={0,1,2,3};
    private final int[] foreground={4,5};

    public GameScreen(Game game) {
        this.game = game;
    }


    @Override
    public void show() {
        super.show();
        mapHelper=new TiledMapHelper();
        map=mapHelper.load("/Users/amrez/Desktop/map1/betterrrr.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        solidBlocks = mapHelper.getSolidBlock();
        game.init(solidBlocks);
        game.loadRoom();
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hud = new Hud(game.getPlayer().getMaxHp());

        batch=new SpriteBatch();
        camera=new OrthographicCamera();
        camera.zoom=0.7f;

        viewport=new ScreenViewport(camera);
        shapeRenderer=new ShapeRenderer();
        gameProcessor=new GameProcessor(game);
        InputMultiplexer inputMultiplexer=new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
        MapLayer spawnLayer=map.getLayers().get("Logic");
        MapObject spawnPoint=spawnLayer.getObjects().get("playerSpawnPoint");
        float spawnX=spawnPoint.getProperties().get("x", Float.class);
        float spawnY=spawnPoint.getProperties().get("y", Float.class);
        game.getPlayer().setPosition(new Vector2(spawnX,5700));
        game.spawnEnemy(EnemyEntity.crawler(new Vector2(1270.21f, 7000f)));
        game.spawnEnemy(EnemyEntity.sentry(new Vector2(1400f, 7000f)));
        game.spawnEnemy(EnemyEntity.flyer(new Vector2(1270.21f, 6800f)));
        game.spawnEnemy(EnemyEntity.laserFlyer(new Vector2(1500f, 6800f)));
        game.spawnEnemy(EnemyEntity.boss(new Vector2(9100, 7400f)));
        io.github.some_example_name.Manager.AchievementManager.resetSession();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        hudCamera.setToOrtho(false, width, height);
        viewport.update(width,height);
    }

    @Override
    public void render(float delta) {

        if (!game.isPaused()) {
            game.update(delta);
            hud.update(delta, game.getPlayer());
        }
        target.set(game.getPlayer().getPosition().x,game.getPlayer().getPosition().y+120,0);
        camera.position.lerp(target,0.1f);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
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

        batch.end();
        mapRenderer.render(foreground);

        // ── [FIXED] PRO-LEVEL DEBUG RENDERING ────────────────────────────────
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // 1. Environment (Green)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0,0,100,0);
        shapeRenderer.line(0,0,0,100);

        // 2. Entity Hurtboxes (Cyan)
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(game.getPlayer().getPosition().x+20,game.getPlayer().getPosition().y,50,100);
        for (EnemyEntity e:game.getEnemies()){
            shapeRenderer.rect(e.getPosition().x+e.hitboxLeftX,e.getPosition().y+e.hitboxBottomY,e.hitboxRightX-e.hitboxLeftX,e.hitboxTopY-e.hitboxBottomY);
        }

        // 3. Player Attack Hitboxes (Yellow)
        shapeRenderer.setColor(Color.YELLOW);
        for (AttackHitbox h : game.getPlayerHitboxes()) {
            shapeRenderer.rect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
            float cx = h.bounds.x + h.bounds.width / 2f;
            float cy = h.bounds.y + h.bounds.height / 2f;
            shapeRenderer.circle(cx, cy, Math.max(h.bounds.width, h.bounds.height) / 2f, 24);
        }

        // 4. Enemy Attack Hitboxes (Red) - NEW! This lets you see the boss attacks
        shapeRenderer.setColor(Color.RED);
        for (AttackHitbox h : game.getEnemyHitboxes()) {
            shapeRenderer.rect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
        }

        shapeRenderer.end();
        // ─────────────────────────────────────────────────────────────────────

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(-1000,-200 ,2000,200);
        shapeRenderer.end();
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        hud.render(batch, game.getPlayer());
        batch.end();

        super.render(delta);
    }
}
