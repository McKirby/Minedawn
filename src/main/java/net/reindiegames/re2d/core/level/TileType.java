package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;

import java.util.HashMap;
import java.util.Map;

public class TileType extends GameResource {
    public static final String RESOURCE_PATH = "tiles/";
    private static final Map<Integer, TileType> TILE_TYPES = new HashMap<>();

    private TileType(String resourceName) {
        super(resourceName);
        TILE_TYPES.put(id, this);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resourceName;
    }
}
