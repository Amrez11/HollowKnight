package io.github.some_example_name.model.entity.player;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.model.entity.DamageResolver;
import io.github.some_example_name.model.entity.IDamageable;
import io.github.some_example_name.model.enums.AnimationType;

public class Entity implements IDamageable {
    private Vector2 position;
    public static final float HITBOX_LEFT_X   = 20f;
    public static final float HITBOX_RIGHT_X  = 70f;
    public static final float HITBOX_BOTTOM_Y = 18f;
    public static final float HITBOX_TOP_Y    = 80f;
    public static final float HITBOX_WIDTH    = HITBOX_RIGHT_X - HITBOX_LEFT_X;
    public static final float HITBOX_HEIGHT   = HITBOX_TOP_Y  - HITBOX_BOTTOM_Y;

    private boolean isOnBoss;

    private static final int MAX_HP   = 9;
    private static final int MAX_SOUL = 99;
    private int hp   = MAX_HP;
    private int soul =99;

    private boolean isInvincible=false;
    private              float flashDuration = 0.1f;
    private boolean focus;

    private boolean howlingWraith;
    private boolean vengefulSpirit;
    private boolean castLock;

    // ── Hitbox-spawn triggers (consumed once by Game, then reset to false) ─────
    private boolean howlingHitTriggered;
    private boolean vengefulFireTriggered;

    private boolean attackPressed;
    private boolean attacking;

    private boolean damaged;
    private boolean isDead=false;

    private boolean isOnGround  = false;
    private boolean dash        = false;
    private boolean dashPressed = false;
    private boolean movingLeft  = false;
    private boolean movingRight = false;
    private boolean movingDown  = true;
    private boolean jump;
    private boolean jumpPressed;
    private boolean isSliding   = false;
    private boolean wallSliding = false;
    private int     numOfJumps  = 2;

    private Vector2         velocity         = new Vector2();
    private PlayerMovement movementLogic=new PlayerMovement(this);
    private PlayerSpecialAbility specialAbility=new PlayerSpecialAbility(this);
    private PlayerAttackLogic attackLogic=new PlayerAttackLogic(this);
    private AnimationType   currentAnimation = AnimationType.KNIGHT_IDLE;
    private float           stateTime        = 0;
    private boolean         lookingRight     = true;


    public void update(float delta) {
        movementLogic.update(delta);


        // Cast lock must tick every frame regardless of invincibility, otherwise
        // getting hit mid-cast freezes the player's movement lock indefinitely.
        specialAbility.tickCastLock(delta);

        if (isInvincible) {
            this.setCurrentAnimation(AnimationType.KNIGHT_Damaged);
            flashDuration-=delta;
            if (flashDuration>0f){
                this.setCurrentAnimation(AnimationType.KNIGHT_Flashing);
            }else {
                this.setCurrentAnimation(AnimationType.KNIGHT_Damaged);
                if (flashDuration<-0.2f){
                    flashDuration=0.2f;
                }
            }
        }else {
            specialAbility.update(delta);
            attackLogic.update(delta);
        }

        // Damaged is a one-frame signal for ability-cancel checks (e.g. Focus);
        // reset it here so it doesn't permanently block re-casting after a hit.
        if (damaged) {
            damaged = false;
        }

        System.out.println("entity"+hp);
    }

    public float getHitboxLeft()   { return position.x + HITBOX_LEFT_X; }
    public float getHitboxRight()  { return position.x + HITBOX_RIGHT_X; }
    public float getHitboxBottom() { return position.y + HITBOX_BOTTOM_Y; }
    public float getHitboxTop()    { return position.y + HITBOX_TOP_Y; }

    public int  getMaxHp()          { return MAX_HP; }

    @Override
    public boolean isDead() {
        if (hp>0){
            return false;
        }
        return true;
    }

    @Override
    public void takeDamage(int amount) {
        hp -=1;
        if (hp == 0) isDead = true;
    }

    public int  getHp()             { return hp; }
    public void setHp(int hp)       { this.hp = Math.max(0, Math.min(hp, MAX_HP)); }

    public int  getMaxSoul()        { return MAX_SOUL; }
    public int  getSoul()           { return soul; }
    public void setSoul(int soul)   { this.soul = Math.max(0, Math.min(soul, MAX_SOUL)); }
    public void addSoul(int amount) { setSoul(soul + amount); }

    public boolean isFocus()               { return focus; }
    public void    setFocus(boolean focus) { this.focus = focus; }

    public boolean isHowlingWraith()                       { return howlingWraith; }
    public void    setHowlingWraith(boolean howlingWraith) { this.howlingWraith = howlingWraith; }

    public boolean isVengefulSpirit()                        { return vengefulSpirit; }
    public void    setVengefulSpirit(boolean vengefulSpirit) { this.vengefulSpirit = vengefulSpirit; }

    public boolean isCastLocked()                  { return castLock; }
    public void    setCastLocked(boolean castLock) { this.castLock = castLock; }

    public boolean isHowlingHitTriggered()                       { return howlingHitTriggered; }
    public void    setHowlingHitTriggered(boolean triggered)     { this.howlingHitTriggered = triggered; }

    public boolean isVengefulFireTriggered()                     { return vengefulFireTriggered; }
    public void    setVengefulFireTriggered(boolean triggered)   { this.vengefulFireTriggered = triggered; }

    public boolean isAttackPressed()                       { return attackPressed; }
    public void    setAttackPressed(boolean attackPressed) { this.attackPressed = attackPressed; }

    public boolean isAttacking()                   { return attacking; }
    public void    setAttacking(boolean attacking) { this.attacking = attacking; }

    public boolean isDamaged()                 { return damaged; }
    public void    setDamaged(boolean damaged) { this.damaged = damaged; }

    public Vector2 getPosition()                 { return position; }
    public void    setPosition(Vector2 position) { this.position = position; }
    public Vector2 getVelocity()                 { return velocity; }
    public void    setVelocity(Vector2 velocity) { this.velocity = velocity; }

    public boolean isOnGround()                    { return isOnGround; }
    public void    setOnGround(boolean onGround)   { isOnGround = onGround; }

    public boolean isMovingLeft()                    { return movingLeft; }
    public void    setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }

    public boolean isMovingRight()                     { return movingRight; }
    public void    setMovingRight(boolean movingRight) { this.movingRight = movingRight; }

    public boolean isMovingDown()                    { return movingDown; }
    public void    setMovingDown(boolean movingDown) { this.movingDown = movingDown; }

    public boolean isLookingRight()                      { return lookingRight; }
    public void    setLookingRight(boolean lookingRight) { this.lookingRight = lookingRight; }

    public boolean isJump()              { return jump; }
    public void    setJump(boolean jump) { this.jump = jump; }

    public boolean isJumpPressed()                     { return jumpPressed; }
    public void    setJumpPressed(boolean jumpPressed) { this.jumpPressed = jumpPressed; }

    public int  getNumOfJumps()               { return numOfJumps; }
    public void setNumOfJumps(int numOfJumps) { this.numOfJumps = numOfJumps; }

    public boolean isDash()             { return dash; }
    public void    setDash(boolean dash) { this.dash = dash; }

    public boolean isDashPressed()                     { return dashPressed; }
    public void    setDashPressed(boolean dashPressed) { this.dashPressed = dashPressed; }

    public boolean isSliding()                 { return isSliding; }
    public void    setSliding(boolean sliding) { isSliding = sliding; }

    public boolean isWallSliding()                     { return wallSliding; }
    public void    setWallSliding(boolean wallSliding) { this.wallSliding = wallSliding; }

    public AnimationType getCurrentAnimation()                               { return currentAnimation; }
    public void          setCurrentAnimation(AnimationType currentAnimation) { this.currentAnimation = currentAnimation; }

    public float getStateTime()                { return stateTime; }
    public void  setStateTime(float stateTime) { this.stateTime = stateTime; }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }

    public float getFlashDuration() {
        return flashDuration;
    }

    public void setFlashDuration(float flashDuration) {
        this.flashDuration = flashDuration;
    }

    public boolean isOnBoss() {
        return isOnBoss;
    }

    public void setOnBoss(boolean onBoss) {
        isOnBoss = onBoss;
    }

    // ── Save/Load ──────────────────────────────────────────────────────────
    /**
     * Clears one-shot/transient combat flags after teleporting the player in
     * via a save file, so they don't spawn mid-flash, mid-cast-lock, etc.
     */
    public void resetTransientCombatState() {
        this.isInvincible = false;
        this.damaged = false;
        this.attacking = false;
        this.howlingHitTriggered = false;
        this.vengefulFireTriggered = false;
        this.flashDuration = 0.1f;
    }
}
