package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class TileType extends GameResource {
    public static final String RESOURCE_PATH = "tiles/";

    public static TileType GRASS;

    public static final int NO_TILING = 0;
    public static final int COMPLETE_TILING = 1;
    public static final short[] TILING_VARIANTS = new short[2];

    private static final Map<Integer, TileType> ID_TILE_MAP = new HashMap<>();
    private static final Map<String, TileType> RESOURCE_TILE_MAP = new HashMap<>();

    static {
        TILING_VARIANTS[NO_TILING] = 1;
        TILING_VARIANTS[COMPLETE_TILING] = 30;
    }

    protected int tiling;
    protected short defaultVariant;

    private TileType(String resource) {
        super(resource);
        ID_TILE_MAP.put(id, this);
        RESOURCE_TILE_MAP.put(resource, this);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStatic(TileType.class, "GRASS", TileType.getByResource("grass.json"));
    }

    public static TileType getById(int id) {
        return ID_TILE_MAP.getOrDefault(id, null);
    }

    public static TileType getByResource(String resource) {
        return RESOURCE_TILE_MAP.getOrDefault(resource, null);
    }

    public static TileType[] getTypes() {
        return ID_TILE_MAP.values().toArray(new TileType[ID_TILE_MAP.size()]);
    }

    public int getTiling() {
        return tiling;
    }

    public short getDefaultVariant() {
        return defaultVariant;
    }

    @Override
    public void load(JsonObject source) {
        final JsonObject core = source.get("core").getAsJsonObject();
        this.tiling = core.get("tiling").getAsInt();
        this.defaultVariant = core.get("default_variant").getAsShort();
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }
}
