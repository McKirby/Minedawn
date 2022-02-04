package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.level.entity.EntitySentient;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

public class Tile implements ICollidable {
    public final Level level;
    public final int tx;
    public final int ty;
    public final TileType type;

    public short variant;

    private final Body body;

    protected Tile(Level level, int tx, int ty, TileType type) {
        this.level = level;
        this.tx = tx;
        this.ty = ty;
        this.type = type;
        this.variant = type.defaultVariant;

        if (type.solid) {
            this.body = level.getChunkBase().createBody(this, BodyType.STATIC, tx, ty);
            level.getChunkBase().createBoundingBox(this, body, 1.0f, 1.0f, 0.0f);
        } else {
            this.body = null;
        }
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void touch(ICollidable object) {
    }

    @Override
    public void release(ICollidable object) {
    }

    @Override
    public boolean collidesWith(ICollidable object) {
        return object instanceof EntitySentient && type.solid;
    }
}
