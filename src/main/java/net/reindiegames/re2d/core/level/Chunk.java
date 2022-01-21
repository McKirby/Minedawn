package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

public class Chunk extends Transformable {
    public static final int CHUNK_SIZE = 16;

    public final Level level;
    public final int cx;
    public final int cy;

    public final Tile[][] tiles;

    protected Chunk(Level level, int cx, int cy) {
        super(CoordinateSystems.chunkToLevel(cx, cy), new Vector2f(CHUNK_SIZE, CHUNK_SIZE), 0.0f);
        this.level = level;
        this.cx = cx;
        this.cy = cy;

        this.tiles = new Tile[CHUNK_SIZE][CHUNK_SIZE];
        Vector2f pos;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                pos = CoordinateSystems.chunkRelativeToLevel(cx, cy, rx, ry);
                tiles[rx][ry] = new Tile(level, (int) pos.x, (int) pos.y, TileType.GRASS);
            }
        }
    }
}
