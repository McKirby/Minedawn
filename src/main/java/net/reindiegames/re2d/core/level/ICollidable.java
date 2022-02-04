package net.reindiegames.re2d.core.level;

import org.jbox2d.dynamics.Body;

public interface ICollidable {
    public abstract Level getLevel();

    public abstract Body getBody();

    public abstract void touch(ICollidable object);

    public abstract void release(ICollidable object);

    public abstract boolean collidesWith(ICollidable object);

    public default void removePhysics() {
        this.getLevel().getChunkBase().world.destroyBody(this.getBody());
    }
}
