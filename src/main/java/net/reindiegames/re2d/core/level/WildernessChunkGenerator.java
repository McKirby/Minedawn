package net.reindiegames.re2d.core.level;

import org.joml.Vector2i;

public class WildernessChunkGenerator implements ChunkGenerator {
    public static final int SPAWN_SIZE = 2;

    @Override
    public Vector2i getSpawn() {
        return new Vector2i((SPAWN_SIZE / 2) * Chunk.CHUNK_SIZE, (SPAWN_SIZE / 2) * Chunk.CHUNK_SIZE);
    }

    @Override
    public void initialize(GeneratedLevel generatedLevel) {
        for (int cx = 0; cx < SPAWN_SIZE; cx++) {
            for (int cy = 0; cy < SPAWN_SIZE; cy++) {
                generatedLevel.getChunkBase().getChunk(cx, cy, true, true);
            }
        }
    }

    @Override
    public void populate(Chunk chunk, int[][][] tiles, short[][][] variants) {
        for (byte rx = 0; rx < Chunk.CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < Chunk.CHUNK_SIZE; ry++) {
                tiles[rx][ry][TileStack.TERRAIN_LAYER_1] = TileType.GRASS.id;
                variants[rx][ry][TileStack.TERRAIN_LAYER_1] = 14;
            }
        }
    }
}
