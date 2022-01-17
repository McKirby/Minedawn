package net.reindiegames.re2d.core.level;

import java.util.HashMap;
import java.util.Map;

public class ChunkBase {
    public final Level level;

    private final Map<Integer, Map<Integer, Chunk>> chunkMap;

    protected ChunkBase(Level level) {
        this.level = level;
        this.chunkMap = new HashMap<>();
    }

    public Chunk getChunk(int cx, int cy, boolean generate) {
        Map<Integer, Chunk> xMap = chunkMap.computeIfAbsent(cx, key -> new HashMap<>());
        Chunk chunk = xMap.getOrDefault(cy, null);
        if (chunk != null) return chunk;

        if (generate) {
            chunk = level.loadChunk(cx, cy);
            xMap.put(cy, chunk);
            return chunk;
        } else {
            return null;
        }
    }
}
