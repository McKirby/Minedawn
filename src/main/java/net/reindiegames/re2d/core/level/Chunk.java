package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

public class Chunk extends Transformable {
    public static final int CHUNK_SIZE = 16;

    public final Level level;
    public final int cx;
    public final int cy;

    public final Tile[][] tiles;

    protected Chunk(Level level, int cx, int cy) {
        super(new Vector2f(CHUNK_SIZE, CHUNK_SIZE));
        this.level = level;
        this.cx = cx;
        this.cy = cy;
        this.tiles = new Tile[CHUNK_SIZE][CHUNK_SIZE];
    }

    @Override
    public Vector2f getPosition() {
        return CoordinateSystems.chunkToLevel(cx, cy);
    }
}
