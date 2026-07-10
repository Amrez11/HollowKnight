package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import io.github.some_example_name.controller.SettingsMenuController;

/**
 * Screen-wide brightness dimmer.
 *
 * Added once per screen (see AbstractScreen.show()), sitting above the
 * screen's own content but below the modal layer, so pause/settings panels
 * stay fully readable no matter how low brightness is set.
 *
 * It reads SettingsMenuController.getInstance() fresh every frame, so
 * dragging the Brightness slider updates whatever screen you're looking at
 * immediately — main menu, gameplay, load screen, all of it — not just
 * one specific screen.
 */
public class BrightnessOverlay extends Actor {

    private static final Texture WHITE_PIXEL;
    private static final TextureRegion WHITE_REGION;

    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        WHITE_PIXEL = new Texture(pixmap);
        pixmap.dispose();
        WHITE_REGION = new TextureRegion(WHITE_PIXEL);
    }

    public BrightnessOverlay() {
        setTouchable(Touchable.disabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() != null) {
            setSize(getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
            setPosition(0, 0);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        SettingsMenuController controller = SettingsMenuController.getInstance();
        float brightness = controller != null ? controller.getBrightness() : 1f;
        if (brightness >= 0.999f) return;

        float alpha = (1f - brightness) * 0.9f;
        Color previous = batch.getColor();
        batch.setColor(0f, 0f, 0f, alpha);
        batch.draw(WHITE_REGION, getX(), getY(), getWidth(), getHeight());
        batch.setColor(previous);
    }
}
