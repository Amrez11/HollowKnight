package io.github.some_example_name.model.costumActors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align; // Make sure to import Align
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.Manager.CharmManager;
import io.github.some_example_name.Manager.GameAssetManager;
import io.github.some_example_name.model.enums.Charm;

/**
 * Inventory / charm menu. Opened with 'I' from GameProcessor.
 * Pauses the game while open (caller is responsible for toggling Game.setPaused).
 * Clicking a charm equips/unequips it (max CharmManager.MAX_NOTCHES at once).
 * Hovering a charm shows its description in the footer label.
 * The Back button (or clicking outside the panel) closes the menu — override
 * onClose() to know when that happens.
 */
public class CharmMenuModal extends Modal {

    private static final Texture NOTCH_EMPTY    = new Texture("ui/charm-notch-empty.png");
    private static final Texture NOTCH_EQUIPPED = new Texture("ui/charm-notch-equipped.png");
    private static final Texture NOTCH_HOVER    = new Texture("ui/charm-notch-hover.png");
    private static final Texture PANEL_BG       = new Texture("ui/stone-tablet-bg.png");

    private final Array<Texture> charmIconTextures = new Array<>();
    private final Array<Image>   notchIcons         = new Array<>(); // 3 top notch slots
    private final Array<Image>   notchFrames         = new Array<>();
    private final Array<Stack>   charmSlots          = new Array<>(); // one per Charm, in Charm.values() order
    private Label descriptionLabel;

    public CharmMenuModal() {
        super();
        setTouchable(Touchable.enabled);
        background(new TextureRegionDrawable(new TextureRegion(PANEL_BG)));

        Label title = new Label("Charms", GameAssetManager.labelStyle);

        Table notchStrip = new Table();
        notchStrip.defaults().size(48).space(10);
        for (int i = 0; i < CharmManager.MAX_NOTCHES; i++) {
            Stack notchStack = new Stack();
            Image frame = new Image(new TextureRegionDrawable(new TextureRegion(NOTCH_EMPTY)));
            Image icon  = new Image();
            icon.setVisible(false);
            notchStack.add(frame);
            notchStack.add(icon);
            notchFrames.add(frame);
            notchIcons.add(icon);
            notchStrip.add(notchStack);
        }

        Table charmGrid = new Table();
        charmGrid.defaults().size(64).space(16);
        int column = 0;
        for (Charm charm : Charm.values()) {
            Stack slot = buildCharmSlot(charm);
            charmSlots.add(slot);
            charmGrid.add(slot);
            column++;
            if (column % 3 == 0) charmGrid.row();
        }

        descriptionLabel = new Label("", GameAssetManager.labelStyle);
        descriptionLabel.setWrap(true);
        // Fix 1: Anchor the text to the top center so it grows downwards neatly
        descriptionLabel.setAlignment(Align.top | Align.center);
        // Fix 2: Scale the font down so it actually fits within the UI panel
        descriptionLabel.setFontScale(0.5f);

        TextButton backButton = new TextButton("Back", textButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        defaults().space(10); // Slightly reduced from 15 to keep things inside the panel
        pad(30);
        add(title).row();
        add(notchStrip).padBottom(10).row();
        add(charmGrid).padBottom(15).row();
        // Fix 3: Use minHeight to ensure the label reserves enough space, preventing overlap with the Back button
        add(descriptionLabel).width(380).minHeight(80).row();
        add(backButton).width(160).height(55);

        refreshNotches();
    }

    private Stack buildCharmSlot(Charm charm) {
        Texture iconTexture = new Texture(Gdx.files.internal(charm.iconPath));
        charmIconTextures.add(iconTexture);

        Stack stack = new Stack();
        Image frame = new Image(new TextureRegionDrawable(new TextureRegion(NOTCH_EMPTY)));
        Image icon  = new Image(new TextureRegionDrawable(new TextureRegion(iconTexture)));

        stack.add(frame);
        stack.add(icon);
        stack.setTouchable(Touchable.enabled);
        stack.setUserObject(frame);

        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onCharmClicked(charm, stack);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // Fix 4: Replaced the dash with a newline character for a much cleaner look
                descriptionLabel.setText(charm.displayName + "\n" + charm.description);
                if (!CharmManager.isEquipped(charm)) {
                    frame.setDrawable(new TextureRegionDrawable(new TextureRegion(NOTCH_HOVER)));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!CharmManager.isEquipped(charm)) {
                    frame.setDrawable(new TextureRegionDrawable(new TextureRegion(NOTCH_EMPTY)));
                }
                // Optional: Clear text when not hovering so it doesn't linger
                // descriptionLabel.setText("");
            }
        });

        return stack;
    }

    private void onCharmClicked(Charm charm, Stack slot) {
        boolean wasEquipped = CharmManager.isEquipped(charm);
        boolean changed = CharmManager.toggle(charm);

        if (!changed && !wasEquipped) {
            descriptionLabel.setText("No open notches\nUnequip a charm first.");
            return;
        }

        Image frame = (Image) slot.getUserObject();
        frame.setDrawable(new TextureRegionDrawable(new TextureRegion(
            CharmManager.isEquipped(charm) ? NOTCH_EQUIPPED : NOTCH_EMPTY)));

        refreshNotches();
    }

    private void refreshNotches() {
        int slotIndex = 0;
        for (Charm charm : Charm.values()) {
            if (CharmManager.isEquipped(charm) && slotIndex < notchIcons.size) {
                Image notchIcon = notchIcons.get(slotIndex);
                notchIcon.setDrawable(new TextureRegionDrawable(
                    new TextureRegion(findIconTexture(charm))));
                notchIcon.setVisible(true);
                notchFrames.get(slotIndex).setDrawable(
                    new TextureRegionDrawable(new TextureRegion(NOTCH_EQUIPPED)));
                slotIndex++;
            }
        }
        for (int i = slotIndex; i < notchIcons.size; i++) {
            notchIcons.get(i).setVisible(false);
            notchFrames.get(i).setDrawable(new TextureRegionDrawable(new TextureRegion(NOTCH_EMPTY)));
        }
    }

    private Texture findIconTexture(Charm charm) {
        int index = charm.ordinal();
        return charmIconTextures.get(index);
    }

    @Override
    public void hide() {
        super.hide();
        onClose();
    }

    /** Override to know when the menu closes (Back button, click outside, etc.) */
    public void onClose() {}
}
