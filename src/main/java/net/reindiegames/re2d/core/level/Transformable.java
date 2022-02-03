package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

public abstract class Transformable implements Positionable, Scaleable, Rotateable {
    public final Vector2f size;

    public final float[] transformation;
    public boolean changed;

    public Transformable(Vector2f size) {
        this.size = size;
        this.transformation = new float[4 * 4];
        this.changed = true;
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(size.x, size.y);
    }

    @Override
    public float getRotation() {
        return 0.0f;
    }
}
