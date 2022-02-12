package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;
import org.joml.Vector2i;

public interface Pinpointable extends Positionable, Scaleable, Rotateable {
    public default Vector2f getCenter() {
        final Vector2f size = this.getSize();
        return this.getPosition().add(size.x / 2.0f, size.y / 2.0f);
    }

    public default Vector2i getCenterTilePosition() {
        final Vector2f center = this.getCenter();
        return new Vector2i((int) center.x, (int) center.y);
    }
}
