package net.reindiegames.re2d.core.level;

import java.util.Random;

public class GeneratedLevel implements Level {
    public final long seed;

    protected final ChunkBase chunkBase;
    protected final Random random;
    protected final ChunkGenerator chunkGenerator;

    public GeneratedLevel(long seed, ChunkGenerator generator) {
        this.seed = seed;
        this.chunkBase = new ChunkBase(this);
        this.random = new Random();

        this.chunkGenerator = generator;
        chunkGenerator.initialize(this);
    }

    @Override
    public void populate(Chunk chunk, int[][] tiles, short[][] variants) {
        chunkGenerator.populate(chunk, tiles, variants);
    }

    @Override
    public ChunkBase getChunkBase() {
        return chunkBase;
    }
}
