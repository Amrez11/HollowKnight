package io.github.some_example_name.SaveInfo;

/**
 * Persisted snapshot of a single enemy: which factory type it was spawned
 * from (so it can be recreated with the right IEnemyBehavior on load),
 * where it was standing, how much hp it had left, and whether it was dead.
 */
public class EnemySaveData {
    public String  type;   // "CRAWLER" | "SENTRY" | "FLYER" | "LASER_FLYER" | "BOSS"
    public float   x;
    public float   y;
    public int     hp;
    public boolean dead;

    /** No-arg constructor required by LibGDX's reflection-based Json. */
    public EnemySaveData() {}

    public EnemySaveData(String type, float x, float y, int hp, boolean dead) {
        this.type = type;
        this.x    = x;
        this.y    = y;
        this.hp   = hp;
        this.dead = dead;
    }
}
