package io.github.some_example_name.Manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.some_example_name.model.enums.AnimationType;

import java.util.HashMap;

public class GameAssetManager {
    public static Skin skin;
    public static Label.LabelStyle labelStyle;
    public static TextButton.TextButtonStyle textButtonStyle;
    public static final HashMap<AnimationType, Animation<TextureRegion>> animationMap=new HashMap<>();

    public static void init(){
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TrajanPro-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 48;
        BitmapFont font = generator.generateFont(param);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 36;
        BitmapFont smallFont = generator.generateFont(smallParam);
        generator.dispose();

        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        textButtonStyle= new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.GOLD;
        textButtonStyle.downFontColor = Color.GRAY;

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

        Animation<TextureRegion> animation = new Animation<>(getFrameDuration(type), frames);
        animation.setPlayMode(getPlayMode(type));
        animationMap.put(type,animation);
    }

    /** Per-type frame duration override. Everything not listed uses the original 1/4f default. */
    private static float getFrameDuration(AnimationType type) {
        switch (type) {
            case NAIL_SLASH:                  return 1/15f;
            case HOWLING_WRAITHS_BLAST:       return 1/12f;
            case VENGEFUL_SPIRIT_PROJECTILE:  return 1/10f;
            case HUD_MASK_BREAK:              return 1/20f; // fast break, ~0.3s total for 6 frames
            case HUD_SOUL_SHINE:              return 1/8f;
            case CRYSTAL_PROJECTILE:          return 1/10f;
            case LASER_BEAM:                  return 1/12f;// slow pulse
            default:                          return 1/4f;
        }
    }

    private static Animation.PlayMode getPlayMode(AnimationType type) {
        switch (type) {
            case LASER_BEAM:
            case CRYSTAL_PROJECTILE:
            case NAIL_SLASH:
            case HOWLING_WRAITHS_BLAST:
            case HUD_MASK_BREAK:
                return Animation.PlayMode.NORMAL; // play once, don't loop
            case HUD_SOUL_SHINE:
                return Animation.PlayMode.LOOP_PINGPONG; // pulses in and out
            default:
                return Animation.PlayMode.LOOP;
        }
    }
}
