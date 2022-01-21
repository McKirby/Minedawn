package net.reindiegames.re2d.core.level;

public class Tile {
    public final Level level;
    public final int tx;
    public final int ty;
    public final TileType type;

    public short variant;

    protected Tile(Level level, int tx, int ty, TileType type) {
        this.level = level;
        this.tx = tx;
        this.ty = ty;
        this.type = type;
        this.variant = type.defaultVariant;
    }
}
