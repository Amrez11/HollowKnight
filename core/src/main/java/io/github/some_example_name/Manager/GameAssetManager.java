package io.github.some_example_name.Manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.enums.Achievement;
import io.github.some_example_name.model.enums.AnimationType;

import java.util.HashMap;

public class GameAssetManager {
    public static Skin skin;
    public static Label.LabelStyle labelStyle;
    public static TextButton.TextButtonStyle textButtonStyle;
    // [FIXED] smallFont used to be generated and immediately discarded — never
    // assigned anywhere. Exposed here so dialogue/HUD text has something
    // smaller than the 48pt menu font to render with.
    public static BitmapFont dialogueFont;
    public static final HashMap<AnimationType, Animation<TextureRegion>> animationMap=new HashMap<>();
    public static final HashMap<Achievement, TextureRegionDrawable> achievementIcons = new HashMap<>();

    // ── Zote voice SFX ─────────────────────────────────────────────────────
    // NOTE: these files don't exist in the repo yet — add short growl/grumble
    // clips at these paths (assets/audio/zote/) before they'll actually play.
    // Loading is defensive (per-file try/catch) so a missing clip never
    // crashes startup — Zote's dialogue still works, just silently.
    private static final String[] ZOTE_GROWL_PATHS = {
        "audio/zote/ZoteGrowl1.ogg",
        "audio/zote/ZoteGrowl2.ogg",
        "audio/zote/ZoteGrowl3.ogg"
    };
    public static final Array<Sound> zoteGrowlSounds = new Array<>();

    public static void init(){
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TrajanPro-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 48;
        BitmapFont font = generator.generateFont(param);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 28;
        dialogueFont = generator.generateFont(smallParam);
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
        for (Achievement a : Achievement.values()) {
            Texture tex = new Texture(Gdx.files.internal(a.getImagePath()));
            achievementIcons.put(a, new TextureRegionDrawable(new TextureRegion(tex)));
        }

        for (String path : ZOTE_GROWL_PATHS) {
            try {
                zoteGrowlSounds.add(Gdx.audio.newSound(Gdx.files.internal(path)));
            } catch (Exception e) {
                Gdx.app.error("GameAssetManager", "Missing Zote growl SFX: " + path, e);
            }
        }
    }

    /** Plays one of Zote's growl SFX at random. No-op if none loaded. */
    public static void playRandomZoteGrowl() {
        if (zoteGrowlSounds.size == 0) return;
        zoteGrowlSounds.random().play();
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
    /** Per-type frame duration override. Everything not listed uses the original 1/4f default. */
    private static float getFrameDuration(AnimationType type) {
        switch (type) {
            case NAIL_SLASH:                  return 1/15f;
            case HOWLING_WRAITHS_BLAST:       return 1/12f;
            case VENGEFUL_SPIRIT_PROJECTILE:  return 1/10f;
            case HUD_MASK_BREAK:              return 1/20f;
            case HUD_SOUL_SHINE:              return 1/8f;
            case CRYSTAL_PROJECTILE:          return 1/10f;
            case LASER_BEAM:                  return 1/12f;

            // ── ADDED: Boss Animation Timings ──
            case BOSS_MACE_WINDUP:            return 0.35f / 3f;  // Plays 3 frames beautifully over windup duration
            case BOSS_MACE_SLAM:              return 0.40f / 3f;  // Fits slam window perfectly
            case BOSS_MACE_RECOVER:           return 0.40f / 5f;  // Fits recovery window perfectly
            case BOSS_CHARGE:                 return 1/12f;       // Smooth running speed
            case BOSS_LEAP_RISE:              return 1/10f;
            case BOSS_LEAP_FALL:              return 1/10f;
            case BOSS_DEF_LEAP:               return 1/10f;
            case BOSS_POWER_RISE:             return 0.45f / 8f;  // Fits all 8 frames into power rise window
            case BOSS_POWER_FALL:             return 1/16f;       // Rapid falling frames
            case BOSS_POWER_IMPACT:           return 0.40f / 8f;  // Fits impact window
            case BOSS_STUN:                   return 1/8f;        // Smooth, slow stunned looping rhythm

            case ZOTE_IDLE:                   return 1/5f;        // Slow, exaggerated breathing
            case ZOTE_ENRAGE_ATTACK:          return 0.9f / 6f;   // Fits ZoteEntity.ENRAGE_DURATION

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

            // ── ADDED: Boss Play Mode Rules ──
            case BOSS_MACE_WINDUP:
            case BOSS_MACE_SLAM:
            case BOSS_MACE_RECOVER:
            case BOSS_LEAP_RISE:
            case BOSS_LEAP_FALL:
            case BOSS_DEF_LEAP:
            case BOSS_POWER_RISE:
            case BOSS_POWER_FALL:
            case BOSS_POWER_IMPACT:
            case ZOTE_ENRAGE_ATTACK:
                return Animation.PlayMode.NORMAL; // These actions should only execute once per swing/jump

            case BOSS_CHARGE:
            case BOSS_STUN:
            case BOSS_IDLE:
                return Animation.PlayMode.LOOP;   // Running, stun states, and standing still should continuous-loop

            default:
                return Animation.PlayMode.LOOP;
        }
    }
}
