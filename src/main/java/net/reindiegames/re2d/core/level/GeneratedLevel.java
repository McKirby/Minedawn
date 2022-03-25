package net.reindiegames.re2d.core.level;

import org.joml.Vector2i;

import java.util.Random;

public class GeneratedLevel implements Level {
    public final long seed;

    protected final ChunkBase chunkBase;
    protected final Random random;

    protected final Vector2i spawn;
    protected final ChunkGenerator chunkGenerator;

    public GeneratedLevel(long seed, ChunkGenerator generator) {
        this.seed = seed;
        this.chunkBase = new ChunkBase(this);
        this.random = new Random();

        this.chunkGenerator = generator;
        chunkGenerator.initialize(this);
        this.spawn = chunkGenerator.getSpawn();
    }

    @Override
    public void populate(Chunk chunk, int[][][] tiles, short[][][] variants) {
        chunkGenerator.populate(chunk, tiles, variants);
    }

    @Override
    public ChunkBase getChunkBase() {
        return chunkBase;
    }

    @Override
    public Vector2i getSpawn() {
        return spawn;
    }
}
