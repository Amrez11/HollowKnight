package io.github.some_example_name.fir.controller.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.fir.controller.SettingsMenuC;

public class BrightnessOverlay {

    private static Texture blackTexture;
    SettingsMenuC controller = SettingsMenuC.getInstance();
    public static void init() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        blackTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public static void apply(SpriteBatch batch, Viewport viewport) {
        float brightness = SettingsMenuC.getInstance().getBrightness();
        float alpha = .95f - brightness;

        if (alpha > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            batch.setProjectionMatrix(viewport.getCamera().combined);
            batch.begin();
            batch.setColor(0, 0, 0, alpha);
            batch.draw(blackTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            batch.setColor(Color.WHITE);
            batch.end();
        }
    }

    public static void dispose() {
        if (blackTexture != null) {
            blackTexture.dispose();
        }
    }
}
