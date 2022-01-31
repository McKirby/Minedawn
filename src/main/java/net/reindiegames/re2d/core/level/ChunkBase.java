package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.level.entity.Entity;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class ChunkBase {
    public final Level level;

    private final Map<Integer, Map<Integer, Chunk>> chunkMap;
    private final Set<Chunk> loadedChunks;
    private final Set<Entity> entities;

    private int nextEntityId;

    protected ChunkBase(Level level) {
        this.level = level;
        this.chunkMap = new HashMap<>();
        this.loadedChunks = new HashSet<>();
        this.entities = new HashSet<>();

        this.nextEntityId = 0;
    }

    public Chunk getChunk(int cx, int cy, boolean generate, boolean load) {
        synchronized (chunkMap) {
            Map<Integer, Chunk> xMap = chunkMap.computeIfAbsent(cx, key -> new HashMap<>());
            Chunk chunk = xMap.getOrDefault(cy, null);
            if (chunk != null) {
                if (load) this.loadChunk(chunk);
                return chunk;
            }

            if (generate) {
                chunk = new Chunk(level, cx, cy);

                final int[][] tiles = new int[CHUNK_SIZE][CHUNK_SIZE];
                final short[][] variants = new short[CHUNK_SIZE][CHUNK_SIZE];
                level.populate(chunk, tiles, variants);

                Vector2f levelPos;
                TileType type;
                Tile tile;
                for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
                    for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                        levelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                        if (tiles[rx][ry] <= 0) continue;

                        type = TileType.getById(tiles[rx][ry]);
                        tile = new Tile(chunk.level, (int) levelPos.x, (int) levelPos.y, type);
                        tile.variant = variants[rx][ry];

                        chunk.tiles[rx][ry] = tile;
                    }
                }

                xMap.put(cy, chunk);
                if (load) this.loadChunk(chunk);
                return chunk;
            } else {
                return null;
            }
        }
    }

    public void forEachLoadedChunk(Consumer<Chunk> chunkConsumer) {
        synchronized (loadedChunks) {
            loadedChunks.forEach(chunkConsumer);
        }
    }

    public void loadChunk(Chunk chunk) {
        synchronized (loadedChunks) {
            loadedChunks.add(chunk);
        }
    }

    public void unloadChunk(Chunk chunk) {
        synchronized (loadedChunks) {
            loadedChunks.remove(chunk);
        }
    }

    public int nextEntityId() {
        return nextEntityId++;
    }

    public void forEachEntity(Consumer<Entity> entityConsumer) {
        synchronized (entities) {
            entities.forEach(entityConsumer);
        }
    }

    protected void addEntity(Entity entity) {
        synchronized (entities) {
            entities.add(entity);
        }
    }

    private void removeEntity(Entity entity) {
        synchronized (entities) {
            entities.remove(entity);
        }
    }
}
