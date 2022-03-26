package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.level.entity.Entity;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2i;

public class Tile implements ICollidable {

    public final TileStack stack;
    public final byte layer;

    public final TileType type;
    public short variant;

    private final Body body;

    protected Tile(TileStack stack, byte layer, TileType type) {
        this.stack = stack;
        this.layer = layer;

        this.type = type;
        this.variant = type.defaultVariant;

        this.body = stack.level.getChunkBase().createBody(this, BodyType.STATIC, stack.tx, stack.ty);
        stack.level.getChunkBase().createBoundingBox(this, body, 1.0f, 1.0f, 0.0f);
    }

    public void destroy() {
        this.removePhysics();
    }

    @Override
    public Level getLevel() {
        return stack.level;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void touch(ICollidable object, boolean collision) {
    }

    @Override
    public void release(ICollidable object, boolean collision) {
    }

    @Override
    public boolean collidesWith(ICollidable object) {
        return object instanceof Entity && type.solid;
    }

    @Override
    public void collision(ICollidable object) {
    }
}
