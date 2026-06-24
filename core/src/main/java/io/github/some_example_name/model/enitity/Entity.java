package io.github.some_example_name.model.enitity;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.model.enitity.movement.IMovementLogic;
import io.github.some_example_name.model.enums.AnimationType;

public class Entity {
    private Vector2 position;
    private Vector2 rightHitBox;
    private Vector2 bottomHitBox;
    private Vector2 leftHitBox;
    private Vector2 topHitBox;
    private boolean isOnGround = false;
    private boolean movingLeft = false, movingRight = false,movingDown=true;
    private boolean jump;
    private boolean isSliding=false;
    private int numOfJumps=2;

    private IMovementLogic movementLogic;
    private AnimationType currentAnimation=AnimationType.KNIGHT_IDLE;
    private float stateTime=0;
    private boolean lookingRight=true;

    public Entity(IMovementLogic movementLogic, Vector2 position) {
        this.movementLogic = movementLogic;
        this.position = position;
        rightHitBox=new Vector2(this.position.x+50,this.position.y+30);
        leftHitBox=new Vector2(this.position.x+35,this.position.y+30);
        topHitBox=new Vector2(this.position.x+42.5f,this.position.y+60);
        bottomHitBox=new Vector2(this.position.x+42.5f,this.position.y+0);

    }
    public void updateHitBoxes(){
        rightHitBox=new Vector2(this.position.x+50,this.position.y+40);
        leftHitBox=new Vector2(this.position.x+35,this.position.y+40);
        topHitBox=new Vector2(this.position.x+42.5f,this.position.y+60);
        bottomHitBox=new Vector2(this.position.x+42.5f,this.position.y+20);

    }

    public void update(float delta){

        movementLogic.update(delta);
        updateHitBoxes();

    }




    public Vector2 getPosition() {
        return position;
    }


    public Vector2 getRightHitBox() {
        return rightHitBox;
    }

    public Vector2 getBottomHitBox() {
        return bottomHitBox;
    }

    public Vector2 getLeftHitBox() {
        return leftHitBox;
    }

    public Vector2 getTopHitBox() {
        return topHitBox;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }



    public boolean isMovingDown() {
        return movingDown;
    }

    public boolean isLookingRight() {
        return lookingRight;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setRightHitBox(Vector2 rightHitBox) {
        this.rightHitBox = rightHitBox;
    }

    public void setBottomHitBox(Vector2 bottomHitBox) {
        this.bottomHitBox = bottomHitBox;
    }

    public void setLeftHitBox(Vector2 leftHitBox) {
        this.leftHitBox = leftHitBox;
    }

    public void setTopHitBox(Vector2 topHitBox) {
        this.topHitBox = topHitBox;
    }

    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }


    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    public void setLookingRight(boolean lookingRight) {
        this.lookingRight = lookingRight;
    }

    public boolean isSliding() {
        return isSliding;
    }

    public void setSliding(boolean sliding) {
        isSliding = sliding;
    }

    public AnimationType getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(AnimationType currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public int getNumOfJumps() {
        return numOfJumps;
    }

    public void setNumOfJumps(int numOfJumps) {
        this.numOfJumps = numOfJumps;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }
}
