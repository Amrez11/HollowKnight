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


     private final int[] background={0};
    private final int[] foreground={1,2,3,4,5};

    public GameScreen(Game game) {
        this.game = game;
    }


    @Override
    public void show() {
        super.show();
        mapHelper=new TiledMapHelper();
        map=mapHelper.load("/Users/amrez/Desktop/map1/untitled.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        solidBlocks = mapHelper.getSolidBlock();
        game.init(mapHelper.getSolidBlock());

        MapLayer spawnLayer=map.getLayers().get("logic");
        MapObject spawnPoint=spawnLayer.getObjects().get("spawnPoint");
        float x=spawnPoint.getProperties().get("x",Float.class);
        float y=spawnPoint.getProperties().get("y",Float.class);
        Vector2 v=new Vector2(x,y+10000);
        game.getPlayer().setPosition(v);

        batch=new SpriteBatch();
        camera=new OrthographicCamera();
        camera.zoom=2f;

        viewport=new ScreenViewport(camera);
        shapeRenderer=new ShapeRenderer();
        gameProcessor=new GameProcessor(game);
        InputMultiplexer inputMultiplexer=new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width,height);
    }

    @Override
    public void render(float delta) {

        game.update(delta);
        target.set(game.getPlayer().getPosition(),0);
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
        batch.end();




        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(0,0,100,0);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0,0,0,100);
        shapeRenderer.rect(game.getPlayer().getPosition().x,game.getPlayer().getPosition().y,100,100);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(-1000,-200 ,2000,200);
        shapeRenderer.end();
        mapRenderer.render(foreground);
        super.render(delta);
    }
}
