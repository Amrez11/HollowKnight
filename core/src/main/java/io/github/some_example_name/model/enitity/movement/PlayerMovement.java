package io.github.some_example_name.model.enitity.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.SolidBlock;
import io.github.some_example_name.model.enitity.CollisionLogic;
import io.github.some_example_name.model.enitity.Entity;
import io.github.some_example_name.model.enums.AnimationType;

public class PlayerMovement implements IMovementLogic {
      private Entity entity;

      private CollisionLogic collisionLogic;
      private Vector2 velocity=new Vector2();

    @Override
    public void update(float delta) {

        if (!entity.isOnGround()){
            if (entity.isSliding()){
                velocity.y-=500*delta;
            }else {
                velocity.y-=1000*delta;
            }

        } else if (velocity.y<0.01){
            velocity.y=0;
        }
        if (entity.isMovingLeft()){
            velocity.x=-300;
            entity.setCurrentAnimation(AnimationType.KNIGHT_RUN);
            entity.setLookingRight(false);
        } else if (entity.isMovingRight()) {
            velocity.x=300;
            entity.setCurrentAnimation(AnimationType.KNIGHT_RUN);
            entity.setLookingRight(true);
        }else {
            velocity.x=0;
            entity.setCurrentAnimation(AnimationType.KNIGHT_IDLE);
        }
        if (entity.isJump()) {
            if (entity.getNumOfJumps() >= 1) {
                velocity.y = 500;
                entity.setNumOfJumps(entity.getNumOfJumps() - 1);
                entity.setOnGround(false);
            }
            entity.setJump(false);
        } else if (velocity.y<0) {
            entity.setMovingDown(true);
        } else if (velocity.y>0) {
            entity.setMovingDown(false);
        }
        entity.getPosition().add(velocity.cpy().scl(delta));
        entity.setStateTime(entity.getStateTime()+delta);
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
