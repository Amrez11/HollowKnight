package io.github.some_example_name.model.enums;

public enum AnimationType {
    KNIGHT_IDLE(9, "animation/Idle.png" ),
    KNIGHT_RUN(13,"animation/Run.png")
    ;
    public final int frameCount;
    public final String path;


    AnimationType(int frameCount, String path) {
        this.frameCount = frameCount;
        this.path = path;

    }
}
