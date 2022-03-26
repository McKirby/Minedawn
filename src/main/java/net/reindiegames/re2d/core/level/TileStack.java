package net.reindiegames.re2d.core.level;

public class TileStack {
    public static final byte TERRAIN_LAYER_1 = 1;
    public static final byte LIQUID_LAYER = 0;
    public static final byte TERRAIN_LAYER_2 = 2;
    public static final byte TERRAIN_LAYER_3 = 3;
    public static final byte ENTITY_LAYER = 4;
    public static final byte TOP_LAYER_1 = 5;
    public static final byte TOP_LAYER_2 = 6;
    public static final byte TOP_LAYER_3 = 7;
    public static final byte LAYERS = 8;

    public final Level level;
    public final Chunk chunk;
    public final int tx;
    public final int ty;
    public final Tile[] tiles;

    public TileStack(Level level, Chunk chunk, int tx, int ty) {
        this.level = level;
        this.chunk = chunk;
        this.tx = tx;
        this.ty = ty;
        this.tiles = new Tile[LAYERS];
    }

    public void clear(byte layer) {
        final Tile tile = tiles[layer];
        if (tile != null) {
            tile.destroy();
        }
    }

    public void clear() {
        for (byte layer = 0; layer < LAYERS; layer++) {
            this.clear(layer);
        }
    }

    public void setTileType(byte layer, TileType type, short variant) {
        this.clear(layer);
        tiles[layer] = type.newInstance(this, layer);
        tiles[layer].variant = variant;
    }

    public boolean isSolid() {
        for (byte layer = 0; layer < TileStack.LAYERS; layer++) {
            if (tiles[layer] != null && tiles[layer].type.isSolid()) {
                return true;
            }
        }
        return false;
    }
}
