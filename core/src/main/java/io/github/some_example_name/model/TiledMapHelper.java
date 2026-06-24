package io.github.some_example_name.model;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.SolidBlock;

public class TiledMapHelper {
    private TiledMap tiledMap;
    public TiledMap load(String path){
        tiledMap=new TmxMapLoader().load(path);
        return tiledMap;


    }


    public Array<SolidBlock> getSolidBlock(){
        Array<SolidBlock> solidBlocks=new Array<>();

        MapLayer layer =tiledMap.getLayers().get("logic");
        for (MapObject object : layer.getObjects()){
            if (object instanceof RectangleMapObject){
                Rectangle rect=((RectangleMapObject) object).getRectangle();
                solidBlocks.add(new SolidBlock(rect.x, rect.y, rect.width, rect.height));
            }
        }
        return solidBlocks;
    }
}
