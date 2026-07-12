package io.github.some_example_name.model.enums;

public enum AnimationType {
    KNIGHT_IDLE(9, "animation/Idle.png" ),
    KNIGHT_RUN(13,"animation/Run.png"),
    KNIGHT_Dash(12,"animation/Dash.png"),
    KNIGHT_Slide(4,"animation/Wall Slide.png"),

    KNIGHT_Fall(12,"animation/Airborne.png"),
    KNIGHT_Jump(12,"animation/Airborne.png"),
    KNIGHT_DJump(8,"animation/Double_Jump.png"),
    KNIGHT_WallJump(9,"animation/Walljump.png"),
    KNIGHT_Focus(6,"animation/Focus Get.png"),
    KNIGHT_HowlingWraiths(7,"animation/Scream.png"),
    KNIGHT_VengefulSpirit(9,"animation/Fireball_Cast.png"),
    KNIGHT_Attack(5,"animation/SlashAlt.png"),
    KNIGHT_Damaged(12,"animation/Idle Hurt.png"),
    KNIGHT_Flashing(12,"animation/Idle Hurt_flash.png"),

    // ── VFX hitbox animations ────────────────────────────────────────────────
    NAIL_SLASH(6, "animation/Effects/SlashEffectAlt.png"),
    HOWLING_WRAITHS_BLAST(13, "animation/Effects/SoulScream.png"),
    VENGEFUL_SPIRIT_PROJECTILE(8, "animation/Effects/BlastSoul.png"),

    CRAWLER_IDLE(1,"Untitled 4.png"),
    CRAWLER_WALK(1,"Untitled 4.png"),
    CRAWLER_LUNGE(1,"Untitled 4.png"),
    CRAWLER_DEAD(3,"animation/Crystal_Crawler/Death Air.png"),
    FLYER_IDLE(4,"animation/Crystal_Hunter/Fly.png"),
    FLYER_HOVER(4,"animation/Crystal_Hunter/Fly.png"),
    FLYER_SWOOP(4,"animation/Crystal_Hunter/Fly.png"),
    FLYER_DEAD(3,"animation/Crystal_Hunter/Death Air.png"),

    BOSS_IDLE(5,"animation/False_knight/Idle.png"),
    BOSS_MACE_WINDUP(3,"animation/False_knight/Attack.png"),
    BOSS_MACE_SLAM(3,"animation/False_knight/Attack.png"),
    BOSS_MACE_RECOVER(5,"animation/False_knight/Attack Recover.png"),
    BOSS_CHARGE(5,"animation/False_knight/Run.png"),
    BOSS_LEAP_RISE(4,"animation/False_knight/Jump.png"),
    BOSS_LEAP_FALL(5,"animation/False_knight/Land.png"),
    BOSS_DEF_LEAP(4,"animation/False_knight/Jump.png"),
    BOSS_POWER_RISE(8,"animation/False_knight/Jump Attack.png"),
    BOSS_POWER_FALL(8,"animation/False_knight/Jump Attack.png"),
    BOSS_POWER_IMPACT(8,"animation/False_knight/Jump Attack.png"),
    BOSS_STUN(5,"animation/False_knight/Body.png"),
    BOSS_DEAD(3,"animation/False_knight/DeathFall.png"),
    SENTRY_PATROL(7, "animation/Husk_Hornhead/Walk.png"),
    SENTRY_REST(6,   "animation/Husk_Hornhead/Idle.png"),
    SENTRY_MARCH(12,  "animation/Husk_Hornhead/Attack Lunge.png"),
    SENTRY_DEAD(1,   "animation/Husk_Hornhead/Death Air.png"),
    CRYSTAL_PROJECTILE(4, "animation/Effects/LaserCircle.png"),
    LASER_BEAM(10,         "animation/Effects/CrystalLaser.png"),

    // ── Laser flyer (special flying enemy) — placeholders ─────────────────────
    LASER_FLYER_IDLE(5,   "animation/Crystallized/Idle.png"),
    LASER_FLYER_CHARGE(6, "animation/Crystallized/Run.png"),
    LASER_FLYER_FIRE(7,   "animation/Crystallized/Shoot.png"),
    LASER_FLYER_ENRAGE(6, "animation/Crystallized/Run.png"),
    LASER_FLYER_DEAD(3,   "animation/Crystallized/Death Air.png"),


    // ── HUD ──────────────────────────────────────────────────────────────────
    HUD_MASK_FULL(1,   "animation/HUD/FilledHealth.png"),
    HUD_MASK_EMPTY(1,  "animation/HUD/EmptyHealth.png"),
    HUD_MASK_BREAK(6,  "animation/HUD/BreakHealth.png"),
    HUD_SOUL_EMPTY(1,  "animation/HUD/SoulOrb_Empty.png"),
    HUD_SOUL_HALF(1,   "animation/HUD/SoulOrb_Half.png"),
    HUD_SOUL_FULL(1,   "animation/HUD/SoulOrb_Full.png"),
    HUD_SOUL_SHINE(5,  "animation/HUD/FilledHealthShine.png"),
    // ── Boss Attack VFX ───────────────────────────────────────────────────────
    BOSS_SLAM_EFFECT(6, "animation/Effects/attackBoss.png"),      // Effect for Mace/Leap landings
    BOSS_SHOCKWAVE(6, "animation/Effects/attackBoss.png"),      // The traveling wave from Power Slam


    ZOTE_IDLE(5,          "animation/Zote/Idle.png"),
    ZOTE_ENRAGE_ATTACK(4, "animation/Zote/Attack.png"),

    ;
    public final int frameCount;
    public final String path;

    AnimationType(int frameCount, String path) {
        this.frameCount = frameCount;
        this.path = path;
    }
}
