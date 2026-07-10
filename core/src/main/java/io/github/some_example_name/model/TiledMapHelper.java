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

        MapLayer layer =tiledMap.getLayers().get("Logic");
        for (MapObject object : layer.getObjects()){
            if (object instanceof RectangleMapObject){
                Rectangle rect=((RectangleMapObject) object).getRectangle();
                solidBlocks.add(new SolidBlock(rect.x, rect.y, rect.width, rect.height));
            }
        }
        return solidBlocks;
    }

    public Array<Rectangle> getDeadlyZones() {
        Array<Rectangle> deadlyZones = new Array<>();

        MapLayer layer = tiledMap.getLayers().get("deadly");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                if (object instanceof RectangleMapObject) {

                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    deadlyZones.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
                }
            }
        }
        return deadlyZones;
    }
    public Array<Rectangle> getCameraBounds() {
        Array<Rectangle> bounds = new Array<>();
        // Assuming the layer is literally named "cameraBounds"
        MapLayer layer = tiledMap.getLayers().get("cameraBounds");

        if (layer != null) {
            for (MapObject object : layer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    bounds.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
                }
            }
        }
        return bounds;
    }
    public Rectangle getNamedRectangle(String layerName, String objectName) {
        MapLayer layer = tiledMap.getLayers().get(layerName);
        if (layer != null) {
            MapObject object = layer.getObjects().get(objectName);
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                // Return a copy so we don't accidentally modify the map data
                return new Rectangle(rect.x, rect.y, rect.width, rect.height);
            }
        }
        return null;
    }
}
