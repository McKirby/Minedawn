package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;

import java.util.HashMap;
import java.util.Map;

public class ResourceLevel extends GameResource implements Level {
    public static final String RESOURCE_PATH = "level/";
    private static final Map<Integer, ResourceLevel> LEVEL = new HashMap<>();

    public final ChunkBase chunkBase;

    private ResourceLevel(String resourceName) {
        super(resourceName);
        this.chunkBase = new ChunkBase(this);
        LEVEL.put(id, this);
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
}
