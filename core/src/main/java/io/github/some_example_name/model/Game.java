package io.github.some_example_name.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.SolidBlock;
import io.github.some_example_name.model.enitity.CollisionLogic;
import io.github.some_example_name.model.enitity.Entity;
import io.github.some_example_name.model.enitity.movement.IMovementLogic;
import io.github.some_example_name.model.enitity.movement.PlayerMovement;

public class Game {
    private final Entity player;
    private final IMovementLogic movement;
    private CollisionLogic collisionLogic;

    public Game() {
        movement = new PlayerMovement();
        player = new Entity(movement, new Vector2());
        movement.setEntity(player); // ✅ inside constructor
    }

    public void update(float delta) {
        if (collisionLogic == null) return; // guard

        player.update(delta);
        collisionLogic.checkCollisions();
    }

    public void init(Array<SolidBlock> solidBlocks) {
        collisionLogic = new CollisionLogic(player, solidBlocks);
    }

    public Entity getPlayer() { return player; }
}
