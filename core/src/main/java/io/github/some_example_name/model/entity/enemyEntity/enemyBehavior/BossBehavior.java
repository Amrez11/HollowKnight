package io.github.some_example_name.model.entity.enemyEntity.enemyBehavior;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.model.entity.AttackHitbox;
import io.github.some_example_name.model.entity.enemyEntity.EnemyEntity;
import io.github.some_example_name.model.entity.player.Entity;
import io.github.some_example_name.model.enums.AnimationType;

public class BossBehavior implements IEnemyBehavior {

    // ── Tuning: phase 1 ───────────────────────────────────────────────────────
    private static final float WALK_SPEED_P1        = 180f;
    private static final float CHARGE_SPEED_P1      = 300f;
    private static final float DECISION_INTERVAL_P1 = 1.4f;
    private static final float ANIM_RATE_P1         = 1.0f;


    // ── Tuning: phase 2 ───────────────────────────────────────────────────────
    private static final float WALK_SPEED_P2        = 260f;
    private static final float CHARGE_SPEED_P2      = 400f;
    private static final float DECISION_INTERVAL_P2 = 0.85f;
    private static final float ANIM_RATE_P2         = 1.5f;

    // ── Tuning: distances ─────────────────────────────────────────────────────
    private static final float CLOSE_RANGE  = 150f;
    private static final float MEDIUM_RANGE = 300f;

    // ── Tuning: move durations ────────────────────────────────────────────────
    private static final float MACE_WINDUP_DUR  = 0.35f;
    private static final float MACE_SLAM_DUR    = 0.4f;
    private static final float MACE_RECOVER_DUR = 0.4f;
    private static final float CHARGE_DUR       = 0.7f;
    private static final float DEF_LEAP_DUR     = 0.5f;
    private static final float POWER_RISE_DUR   = 0.45f;
    private static final float STUN_DURATION    = 3.0f;

    // ── Tuning: physics ───────────────────────────────────────────────────────
    private static final float GRAVITY      = 700f;
    private static final float LEAP_VY      = 550f;
    private static final float POWER_LEAP_VY = 700f;
    private static final float DEF_LEAP_VY  = 400f;
    private static final float DEF_LEAP_VX  = 300f;

    // ── Tuning: hitboxes ──────────────────────────────────────────────────────
    private static final float MACE_HB_W         = 350f;
    private static final float MACE_HB_H         = 200f;
    private static final float MACE_HB_LIFETIME  = 0.15f;
    private static final int   MACE_DAMAGE       = 1;

    private static final float LEAP_HB_W         = 300f;
    private static final float LEAP_HB_H         = 80f;
    private static final float LEAP_HB_LIFETIME  = 0.15f;
    private static final int   LEAP_DAMAGE       = 1;

    private static final float SHOCK_W           = 200f;
    private static final float SHOCK_H           = 80f;
    private static final float SHOCK_SPEED       = 400f;
    private static final float SHOCK_LIFETIME    = 0.6f;
    private static final int   SHOCK_DAMAGE      = 2;

    // ── Tuning: rapid-hit defensive leap ─────────────────────────────────────
    private static final int   RAPID_HIT_THRESHOLD = 2;
    private static final float RAPID_HIT_WINDOW    = 0.6f;
    private static final int CONTACT_DAMAGE = 2;

    // ─────────────────────────────────────────────────────────────────────────

    private EnemyEntity self;

    private enum Move { MACE_SLAM, CHARGE_RUN, OFFENSIVE_LEAP, DEFENSIVE_LEAP, POWER_SLAM }
    private enum State {
        IDLE,
        MACE_WINDUP, MACE_SLAM, MACE_RECOVER,
        CHARGE,
        LEAP_RISE, LEAP_FALL,
        DEF_LEAP,
        POWER_RISE, POWER_FALL, POWER_IMPACT, POWER_RECOVER,
        STUN
    }

    private State   state      = State.IDLE;
    private float   stateTimer = 0f;
    private boolean phaseTwo   = false;
    private Move    lastMove   = null;

    private int   rapidHitCount = 0;
    private float rapidHitTimer = 0f;
    private int   lastKnownHp;

    private float decisionTimer = 0f;
    private boolean poundLanded = false;
    private float   animRate    = ANIM_RATE_P1;

    private final Array<AttackHitbox> pendingHitboxes = new Array<>();
    private final Array<AttackHitbox> tempDrain = new Array<>();

    // ─────────────────────────────────────────────────────────────────────────

    @Override public void setEntity(EnemyEntity entity) {
        this.self        = entity;
        this.lastKnownHp = entity.getHp();
    }
    @Override public int           getContactDamage() { return CONTACT_DAMAGE; }
    @Override public AnimationType idleAnimation()    { return AnimationType.BOSS_IDLE; }

    public boolean isStunned()        { return state == State.STUN; }
    public boolean isPoundLanded()    { return poundLanded; }
    public void    clearPoundLanded() { poundLanded = false; }
    public float   getAnimRate()      { return animRate; }

    public Array<AttackHitbox> drainPendingHitboxes() {
        tempDrain.clear();
        tempDrain.addAll(pendingHitboxes);
        pendingHitboxes.clear();
        return tempDrain;
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void update(float delta, Entity player) {
        stateTimer    += delta;
        decisionTimer += delta;

        detectRapidHits(delta);
        checkPhaseTransition();

        // [FIXED] Calculate distance from the true center of both entities
        float cxBoss   = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        float cxPlayer = (player.getHitboxLeft() + player.getHitboxRight()) / 2f;
        float dx       = cxPlayer - cxBoss;
        float absDx    = Math.abs(dx);

        switch (state) {

            case IDLE:
                applyFriction();
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_IDLE);
                float interval = phaseTwo ? DECISION_INTERVAL_P2 : DECISION_INTERVAL_P1;
                if (decisionTimer >= interval) {
                    decisionTimer = 0f;
                    pickMove(player, absDx);
                }
                break;

            case MACE_WINDUP:
                // [FIXED] facePlayer(dx) removed from here to prevent direction-flipping jitter
                setAnimation(AnimationType.BOSS_MACE_WINDUP);
                if (stateTimer >= MACE_WINDUP_DUR) enterState(State.MACE_SLAM);
                break;

            case MACE_SLAM:
                setAnimation(AnimationType.BOSS_MACE_SLAM);
                if (stateTimer >= MACE_SLAM_DUR) {
                    spawnMaceHitbox();
                    poundLanded = true;
                    enterState(State.MACE_RECOVER);
                }
                break;

            case MACE_RECOVER:
                setAnimation(AnimationType.BOSS_MACE_RECOVER);
                if (stateTimer >= MACE_RECOVER_DUR) enterState(State.IDLE);
                break;

            case CHARGE:
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_CHARGE);
                if (stateTimer >= CHARGE_DUR) {
                    self.getVelocity().x = 0;
                    enterState(State.IDLE);
                }
                break;

            case LEAP_RISE:
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_LEAP_RISE);
                if (self.getVelocity().y <= 0) enterState(State.LEAP_FALL);
                break;

            case LEAP_FALL:
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_LEAP_FALL);
                if (self.isOnGround()) {
                    spawnLeapLandingHitbox();
                    poundLanded = true;
                    self.getVelocity().x = 0;
                    enterState(State.IDLE);
                }
                break;

            case DEF_LEAP:
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_DEF_LEAP);
                if (self.isOnGround() && stateTimer >= DEF_LEAP_DUR) {
                    self.getVelocity().x = 0;
                    enterState(State.IDLE);
                }
                break;

            case POWER_RISE:
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_POWER_RISE);
                if (stateTimer >= POWER_RISE_DUR) {
                    self.getVelocity().x = 0;
                    self.getVelocity().y = -1200f;
                    enterState(State.POWER_FALL);
                }
                break;

            case POWER_FALL:
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_POWER_FALL);
                if (self.isOnGround()) {
                    spawnPowerSlamHitboxes();
                    poundLanded = true;
                    self.getVelocity().set(0, 0);
                    enterState(State.POWER_IMPACT);
                }
                break;

            case POWER_IMPACT:
                setAnimation(AnimationType.BOSS_POWER_IMPACT);
                if (stateTimer >= 0.4f) enterState(State.POWER_RECOVER);
                break;

            case POWER_RECOVER:
                setAnimation(AnimationType.BOSS_IDLE);
                if (stateTimer >= 0.5f) enterState(State.IDLE);
                break;

            case STUN:
                self.getVelocity().x = 0;
                applyGravity(delta);
                setAnimation(AnimationType.BOSS_STUN);
                if (stateTimer >= STUN_DURATION) {
                    activatePhaseTwo();
                    enterState(State.IDLE);
                }
                break;
        }

        self.getPosition().x += self.getVelocity().x * delta;
        self.getPosition().y += self.getVelocity().y * delta;
    }

    // ── Hitbox spawners ───────────────────────────────────────────────────────

    private void spawnMaceHitbox() {
        float bossFloor = self.getHitboxBottom();
        float x;
        if (self.isLookingRight()) {
            x = self.getHitboxRight();
        } else {
            x = self.getHitboxLeft() - MACE_HB_W;
        }
        pendingHitboxes.add(new AttackHitbox(
            x, bossFloor,
            MACE_HB_W, MACE_HB_H,
            MACE_DAMAGE, MACE_HB_LIFETIME,
            AnimationType.BOSS_MACE_SLAM,
            self.isLookingRight(),
            new Vector2(0, 0)
        ));
    }

    private void spawnLeapLandingHitbox() {
        float cx = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        pendingHitboxes.add(new AttackHitbox(
            cx - LEAP_HB_W / 2f, self.getHitboxBottom(),
            LEAP_HB_W, LEAP_HB_H,
            LEAP_DAMAGE, LEAP_HB_LIFETIME,
            AnimationType.BOSS_LEAP_FALL,
            self.isLookingRight(),
            new Vector2(0, 0)
        ));
    }

    private void spawnPowerSlamHitboxes() {
        float cx    = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        float floor = self.getHitboxBottom();

        pendingHitboxes.add(new AttackHitbox(
            cx - LEAP_HB_W / 2f, floor,
            LEAP_HB_W, LEAP_HB_H,
            SHOCK_DAMAGE, MACE_HB_LIFETIME,
            AnimationType.BOSS_POWER_IMPACT,
            self.isLookingRight(),
            new Vector2(0, 0)
        ));

        pendingHitboxes.add(new AttackHitbox(
            cx - SHOCK_W, floor,
            SHOCK_W, SHOCK_H,
            SHOCK_DAMAGE, SHOCK_LIFETIME,
            AnimationType.BOSS_POWER_IMPACT,
            false,
            new Vector2(-SHOCK_SPEED, 0)
        ));

        pendingHitboxes.add(new AttackHitbox(
            cx, floor,
            SHOCK_W, SHOCK_H,
            SHOCK_DAMAGE, SHOCK_LIFETIME,
            AnimationType.BOSS_POWER_IMPACT,
            true,
            new Vector2(SHOCK_SPEED, 0)
        ));
    }

    // ── Move selection ────────────────────────────────────────────────────────

    private void pickMove(Entity player, float absDx) {
        if (rapidHitCount >= RAPID_HIT_THRESHOLD) {
            rapidHitCount = 0;
            executeMove(Move.DEFENSIVE_LEAP, player);
            return;
        }

        float wMaceSlam = 0, wChargeRun = 0, wOffensiveLeap = 0, wDefLeap = 0, wPowerSlam = 0;

        if (absDx < CLOSE_RANGE) {
            wMaceSlam = 60; wChargeRun = 5; wOffensiveLeap = 15; wDefLeap = 20;
            wPowerSlam = phaseTwo ? 10 : 0;
        } else if (absDx < MEDIUM_RANGE) {
            wMaceSlam = 15; wChargeRun = 35; wOffensiveLeap = 35; wDefLeap = 15;
            wPowerSlam = phaseTwo ? 20 : 0;
        } else {
            wMaceSlam = 5; wChargeRun = 40; wOffensiveLeap = 40; wDefLeap = 15;
            wPowerSlam = phaseTwo ? 25 : 0;
        }

        if (lastMove == Move.MACE_SLAM)      wMaceSlam      = 0;
        if (lastMove == Move.CHARGE_RUN)     wChargeRun     = 0;
        if (lastMove == Move.OFFENSIVE_LEAP) wOffensiveLeap = 0;
        if (lastMove == Move.DEFENSIVE_LEAP) wDefLeap       = 0;
        if (lastMove == Move.POWER_SLAM)     wPowerSlam     = 0;

        float total = wMaceSlam + wChargeRun + wOffensiveLeap + wDefLeap + wPowerSlam;
        if (total <= 0f) { enterState(State.IDLE); return; }

        float roll = (float)(Math.random() * total);
        Move chosen;
        if      (roll < wMaceSlam)                                           chosen = Move.MACE_SLAM;
        else if (roll < wMaceSlam + wChargeRun)                              chosen = Move.CHARGE_RUN;
        else if (roll < wMaceSlam + wChargeRun + wOffensiveLeap)             chosen = Move.OFFENSIVE_LEAP;
        else if (roll < wMaceSlam + wChargeRun + wOffensiveLeap + wDefLeap)  chosen = Move.DEFENSIVE_LEAP;
        else                                                                  chosen = Move.POWER_SLAM;

        executeMove(chosen, player);
    }

    private void executeMove(Move move, Entity player) {
        lastMove = move;

        // Face the player once dynamically at the START of the move calculation
        float cxBoss   = (self.getHitboxLeft() + self.getHitboxRight()) / 2f;
        float cxPlayer = (player.getHitboxLeft() + player.getHitboxRight()) / 2f;
        float dx       = cxPlayer - cxBoss;
        float dir      = dx > 0 ? 1f : -1f;
        float speed    = phaseTwo ? CHARGE_SPEED_P2 : CHARGE_SPEED_P1;

        switch (move) {
            case MACE_SLAM:
                facePlayer(dx);
                enterState(State.MACE_WINDUP);
                break;
            case CHARGE_RUN:
                facePlayer(dx);
                self.getVelocity().x = dir * speed;
                enterState(State.CHARGE);
                break;
            case OFFENSIVE_LEAP:
                facePlayer(dx);
                self.getVelocity().x = dir * (phaseTwo ? WALK_SPEED_P2 : WALK_SPEED_P1);
                self.getVelocity().y = LEAP_VY;
                enterState(State.LEAP_RISE);
                break;
            case DEFENSIVE_LEAP:
                self.getVelocity().x = -dir * DEF_LEAP_VX;
                self.getVelocity().y = DEF_LEAP_VY;
                self.setLookingRight(!self.isLookingRight());
                enterState(State.DEF_LEAP);
                break;
            case POWER_SLAM:
                self.getVelocity().x = dir * 60f;
                self.getVelocity().y = POWER_LEAP_VY;
                enterState(State.POWER_RISE);
                break;
        }
    }

    // ── Phase transition ──────────────────────────────────────────────────────

    private void checkPhaseTransition() {
        if (!phaseTwo && state != State.STUN && self.getHp() <= self.getMaxHp() / 2) {
            self.getVelocity().set(0, 0);
            enterState(State.STUN);
        }
    }

    private void activatePhaseTwo() {
        phaseTwo      = true;
        animRate      = ANIM_RATE_P2;
        lastMove      = null;
        decisionTimer = 0f;
    }

    // ── Rapid-hit detection ───────────────────────────────────────────────────

    private void detectRapidHits(float delta) {
        int currentHp = self.getHp();
        if (currentHp < lastKnownHp) {
            rapidHitCount++;
            rapidHitTimer = 0f;
        }
        lastKnownHp   = currentHp;
        rapidHitTimer += delta;
        if (rapidHitTimer >= RAPID_HIT_WINDOW) {
            rapidHitCount = 0;
            rapidHitTimer = 0f;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    // [FIXED] Force decisionTimer to reset when entering IDLE so it doesn't instantly skip to the next attack
    private void enterState(State next) {
        state = next;
        stateTimer = 0f;

        if (next == State.IDLE) {
            decisionTimer = 0f;
        }
    }

    private void setAnimation(AnimationType anim) {
        if (self.getCurrentAnimation() != anim) {
            self.setCurrentAnimation(anim);
            self.resetStateTime();
        }
    }

    private void facePlayer(float dx) { self.setLookingRight(dx > 0); }

    private void applyGravity(float delta) {
        if (self.isOnGround()) {
            if (self.getVelocity().y < 0) self.getVelocity().y = 0;
        } else {
            self.getVelocity().y -= GRAVITY * delta;
        }
    }

    private void applyFriction() { self.getVelocity().x *= 0.75f; }
}
