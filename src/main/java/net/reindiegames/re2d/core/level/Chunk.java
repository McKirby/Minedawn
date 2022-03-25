package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

public class Chunk extends Transformable {
    public static final int CHUNK_SIZE = 16;

    public static final byte LIQUID_LAYER = 0;
    public static final byte TERRAIN_LAYER_1 = 1;
    public static final byte TERRAIN_LAYER_2 = 2;
    public static final byte TERRAIN_LAYER_3 = 3;
    public static final byte ENTITY_LAYER = 4;
    public static final byte TOP_LAYER_1 = 5;
    public static final byte TOP_LAYER_2 = 6;
    public static final byte TOP_LAYER_3 = 7;
    public static final byte CHUNK_LAYERS = 8;

    public final Level level;
    public final int cx;
    public final int cy;

    public final Tile[][][] tiles;

    protected Chunk(Level level, int cx, int cy) {
        super(new Vector2f(CHUNK_SIZE, CHUNK_SIZE));
        this.level = level;
        this.cx = cx;
        this.cy = cy;
        this.tiles = new Tile[CHUNK_SIZE][CHUNK_SIZE][CHUNK_LAYERS];
    }

    @Override
    public Vector2f getPosition() {
        return CoordinateSystems.chunkToLevel(cx, cy);
    }
}
