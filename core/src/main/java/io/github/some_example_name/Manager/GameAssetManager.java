package io.github.some_example_name.Manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.model.enums.AnimationType;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GameAssetManager {
    public static Skin skin;
    public static final HashMap<AnimationType, Animation<TextureRegion>> animationMap=new HashMap<>();
    public static void init(){
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        for (AnimationType a:AnimationType.values()){
            loadAnimation(a);
        }
    }
    private static void loadAnimation(AnimationType type){
        Texture texture = new Texture(type.path);


        int tileWidth = texture.getWidth() / type.frameCount;
        int tileHeight = texture.getHeight();
        TextureRegion[][] split = TextureRegion.split(texture, tileWidth, tileHeight);

        TextureRegion[] frames = new TextureRegion[type.frameCount];
        for (int i = 0; i < type.frameCount; i++) {
            frames[i] = split[0][i];
        }
        Animation<TextureRegion> animation=new Animation<>(1/13f,frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        animationMap.put(type,animation);




    }

}
