package net.reindiegames.re2d.core.level;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

public class Tile {
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
            this.body = level.getChunkBase().createBody(BodyType.STATIC, tx, ty);
            level.getChunkBase().createBoundingBox(body, 1.0f, 1.0f);
        } else {
            this.body = null;
        }
    }

    public void dispose() {
        if (body != null) {
            level.getChunkBase().world.destroyBody(body);
        }
    }
}
