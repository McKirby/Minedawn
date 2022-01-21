package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

public abstract class Transformable {
    public final Vector2f pos;
    public final Vector2f size;
    public float rotation;

    public final float[] transformation;
    public boolean changed;

    public Transformable(Vector2f pos, Vector2f size, float rotation) {
        this.pos = pos;
        this.size = size;
        this.rotation = rotation;
        this.transformation = new float[4 * 4];
        this.changed = true;
    }
}
