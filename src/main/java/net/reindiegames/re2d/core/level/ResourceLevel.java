package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class ResourceLevel extends GameResource implements Level {
    public static final String RESOURCE_PATH = "core/level/";

    public static final int TEST_LEVEL_ID = 100000;
    public static ResourceLevel TEST_LEVEL;

    private static final Map<Integer, ResourceLevel> ID_LEVEL_MAP = new HashMap<>();
    private static final Map<String, ResourceLevel> RESOURCE_LEVEL_MAP = new HashMap<>();

    public final ChunkBase chunkBase;

    private ResourceLevel(String resource) {
        super(resource);
        this.chunkBase = new ChunkBase(this);

        ID_LEVEL_MAP.put(id, this);
        RESOURCE_LEVEL_MAP.put(resource, this);

        int spawnSize = 2;
        for (int cx = -spawnSize; cx <= spawnSize; cx++) {
            for (int cy = -spawnSize; cy <= spawnSize; cy++) {
                this.getChunkBase().getChunk(cx, cy, true, true);
            }
        }
    }

    private static void link() throws Exception {
        ReflectionUtil.setStatic(ResourceLevel.class, "TEST_LEVEL", ResourceLevel.getByResource("test_level.json"));
    }

    public static ResourceLevel getById(int id) {
        return ID_LEVEL_MAP.getOrDefault(id, null);
    }

    public static ResourceLevel getByResource(String resource) {
        return RESOURCE_LEVEL_MAP.getOrDefault(resource, null);
    }

    public static ResourceLevel[] getTypes() {
        return ID_LEVEL_MAP.values().toArray(new ResourceLevel[ID_LEVEL_MAP.size()]);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }

    @Override
    public ChunkBase getChunkBase() {
        return chunkBase;
    }

    @Override
    public void populate(Chunk chunk, int[][] tiles) {
        for (int rx = 0; rx < Chunk.CHUNK_SIZE; rx++) {
            for (int ry = 0; ry < Chunk.CHUNK_SIZE; ry++) {
                tiles[rx][ry] = TileType.GRASS.id;
            }
        }
    }
}
