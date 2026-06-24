package io.github.some_example_name.model;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public class SolidBlock {
    public Rectangle bounds;

    public SolidBlock(float x,float y,float width,float height) {
        this.bounds = new Rectangle(x,y,width,height);
    }
}
