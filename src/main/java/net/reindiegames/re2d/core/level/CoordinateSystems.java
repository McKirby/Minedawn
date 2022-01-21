package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;
import org.joml.Vector2i;

public final class CoordinateSystems {
    private CoordinateSystems() {
    }

    public static Vector2i levelToChunk(Vector2f levelPos) {
        final Vector2i vec = new Vector2i();
        if (levelPos.x >= 0) {
            vec.x = ((int) Math.floor(levelPos.x)) / Chunk.CHUNK_SIZE;
        } else {
            vec.x = (((int) Math.floor(levelPos.x + 1)) / Chunk.CHUNK_SIZE) - 1;
        }

        if (levelPos.y >= 0) {
            vec.y = ((int) Math.floor(levelPos.y)) / Chunk.CHUNK_SIZE;
        } else {
            vec.y = (((int) Math.floor(levelPos.y + 1)) / Chunk.CHUNK_SIZE) - 1;
        }
        return vec;
    }

    public static Vector2f chunkToLevel(int cx, int cy) {
        return new Vector2f(cx * Chunk.CHUNK_SIZE, cy * Chunk.CHUNK_SIZE);
    }

    public static Vector2i levelToChunkRelative(Vector2f levelPos) {
        final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);

        final Vector2i vec = new Vector2i();
        vec.x = (int) Math.abs(levelPos.x - (chunkPos.x * Chunk.CHUNK_SIZE));
        vec.y = (int) Math.abs(levelPos.y - (chunkPos.y * Chunk.CHUNK_SIZE));
        return vec;
    }

    public static Vector2f chunkRelativeToLevel(Vector2i chunk, Vector2i relative) {
        return CoordinateSystems.chunkRelativeToLevel(chunk.x, chunk.y, (byte) relative.x, (byte) relative.y);
    }

    public static Vector2f chunkRelativeToLevel(int cx, int cy, byte rx, byte ry) {
        final Vector2f vec = new Vector2f();
        vec.x = cx * Chunk.CHUNK_SIZE + rx;
        vec.y = cy * Chunk.CHUNK_SIZE + ry;
        return vec;
    }
}
