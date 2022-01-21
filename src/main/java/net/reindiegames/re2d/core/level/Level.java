package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;
import org.joml.Vector2i;

public interface Level {
    public abstract Chunk loadChunk(int cx, int cy);

    public abstract ChunkBase getChunkBase();

    public default Chunk getChunk(Vector2f levelPos) {
        return this.getChunk(levelPos, false, false);
    }

    public default Chunk getChunk(Vector2f levelPos, boolean generate, boolean load) {
        final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);
        return this.getChunkBase().getChunk(chunkPos.x, chunkPos.y, generate, load);
    }

    public default Tile getTile(Vector2f levelPos) {
        final Chunk chunk = this.getChunk(levelPos, false, false);
        if (chunk == null) return null;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
        return chunk.tiles[relative.x][relative.y];
    }

    public default void setTileType(Vector2f levelPos, TileType type) {
        this.setTileType(levelPos, type, type.defaultVariant);
    }

    public default void setTileType(Vector2f levelPos, TileType type, short variant) {
        final Chunk chunk = this.getChunk(levelPos, false, false);
        if (chunk == null) return;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
        final Tile tile = new Tile(this, (int) levelPos.x, (int) levelPos.y, type);
        tile.variant = variant;
        chunk.tiles[relative.x][relative.y] = tile;
        chunk.changed = true;
    }
}
