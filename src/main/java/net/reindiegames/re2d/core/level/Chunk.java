package net.reindiegames.re2d.core.level;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int TILE_LAYERS = 1;

    public final Level level;
    public final int cx;
    public final int cy;

    public final int[][][] tiles;
    public final short[][][] variants;

    protected Chunk(Level level, int cx, int cy) {
        this.level = level;
        this.cx = cx;
        this.cy = cy;

        this.tiles = new int[TILE_LAYERS][CHUNK_SIZE][CHUNK_SIZE];
        this.variants = new short[TILE_LAYERS][CHUNK_SIZE][CHUNK_SIZE];

        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                tiles[0][rx][ry] = TileType.GRASS.id;
                variants[0][rx][ry] = TileType.GRASS.defaultVariant;
            }
        }
    }
}
