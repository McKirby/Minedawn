package net.reindiegames.re2d.core.level;

public class ProceduralLevel implements Level {
    public final ChunkBase chunkBase;

    public ProceduralLevel() {
        this.chunkBase = new ChunkBase(this);
    }

    @Override
    public ChunkBase getChunkBase() {
        return chunkBase;
    }
}
