package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class TileType extends GameResource {
    public static final String RESOURCE_PATH = "tiles/";

    public static final int GRASS_ID = 1;
    public static TileType GRASS;

    private static final Map<Integer, TileType> TILE_TYPES = new HashMap<>();

    private TileType(String resourceName) {
        super(resourceName);
        TILE_TYPES.put(id, this);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStaticField(TileType.class, "GRASS", TileType.getById(GRASS_ID));
    }

    public static TileType getById(int id) {
        return TILE_TYPES.getOrDefault(id, null);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resourceName;
    }
}
