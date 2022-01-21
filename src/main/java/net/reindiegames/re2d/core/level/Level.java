package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;
import org.joml.Vector2i;

public interface Level extends ChunkSource {
    ChunkBase getChunkBase();

    default Chunk getChunk(Vector2f levelPos) {
        return this.getChunk(levelPos, false, false);
    }

    default Chunk getChunk(Vector2f levelPos, boolean generate, boolean load) {
        final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);
        return this.getChunkBase().getChunk(chunkPos.x, chunkPos.y, generate, load);
    }

    default void setTileType(Vector2f levelPos, TileType type) {
        this.setTileType(levelPos, type, type.defaultVariant);
    }

    default void setTileType(Vector2f levelPos, TileType type, short variant) {
        final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);
        final Chunk chunk = this.getChunkBase().getChunk(chunkPos.x, chunkPos.y, false, false);
        if (chunk == null) return;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
        chunk.tiles[relative.x][relative.y] = type.id;
        chunk.variants[relative.x][relative.y] = variant;
        chunk.changed = true;
    }
}
