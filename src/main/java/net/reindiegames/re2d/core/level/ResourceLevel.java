package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class ResourceLevel extends GameResource implements Level {
    public static final String RESOURCE_PATH = "level/";

    public static final int TEST_LEVEL_ID = 1;
    public static ResourceLevel TEST_LEVEL;

    private static final Map<Integer, ResourceLevel> RESOURCE_LEVELS = new HashMap<>();

    public final ChunkBase chunkBase;

    private ResourceLevel(String resourceName) {
        super(resourceName);
        this.chunkBase = new ChunkBase(this);
        RESOURCE_LEVELS.put(id, this);

        this.getChunkBase().getChunk(0, 0, true, true);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStaticField(ResourceLevel.class, "TEST_LEVEL", ResourceLevel.getById(TEST_LEVEL_ID));
    }

    public static ResourceLevel getById(int id) {
        return RESOURCE_LEVELS.getOrDefault(id, null);
    }

    public static ResourceLevel[] getLevels() {
        return RESOURCE_LEVELS.values().toArray(new ResourceLevel[RESOURCE_LEVELS.size()]);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resourceName;
    }

    @Override
    public ChunkBase getChunkBase() {
        return chunkBase;
    }

    @Override
    public Chunk loadChunk(int cx, int cy) {
        return new Chunk(this, cx, cy);
    }
}
