package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;
import org.joml.Vector2i;

public interface Pinpointable extends Positionable, Scaleable, Rotateable {
    public default Vector2f getCenter() {
        final Vector2f size = this.getSize();
        return this.getPosition().add(size.x / 2.0f, size.y / 2.0f);
    }

    public default Vector2f getTopLeft() {
        final Vector2f size = this.getSize();
        return this.getPosition().add(0.0f, size.y);
    }

    public default Vector2f getTopRight() {
        final Vector2f size = this.getSize();
        return this.getPosition().add(size.x, size.y);
    }

    public default Vector2f getBottomRight() {
        final Vector2f size = this.getSize();
        return this.getPosition().add(size.x, 0.0f);
    }

    public default Vector2f getBottomLeft() {
        return this.getPosition();
    }

    public default Vector2i getCenterTilePosition() {
        return CoordinateSystems.levelToLevelTile(this.getCenter());
    }
}
