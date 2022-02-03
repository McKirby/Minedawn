package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;
import org.joml.Vector2i;

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

    public Vector2f getCenter() {
        return this.getPosition().add(size.x / 2.0f, size.y / 2.0f);
    }

    public Vector2i getCenterTilePosition() {
        final Vector2f center = this.getCenter();
        return new Vector2i((int) center.x, (int) center.y);
    }

    @Override
    public float getRotation() {
        return 0.0f;
    }
}
