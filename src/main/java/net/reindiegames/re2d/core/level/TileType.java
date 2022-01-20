package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class TileType extends GameResource {
    public static final String RESOURCE_PATH = "tiles/";

    public static final int NO_TILING = 0;
    public static final int COMPLETE_TILING = 1;
    public static final short[] TILING_VARIANTS = new short[2];

    public static final int GRASS_ID = 1;
    public static TileType GRASS;

    private static final Map<Integer, TileType> TILE_TYPES = new HashMap<>();

    static {
        TILING_VARIANTS[NO_TILING] = 1;
        TILING_VARIANTS[COMPLETE_TILING] = 30;
    }

    protected int tiling;
    protected short defaultVariant;

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

    public static TileType[] getTypes() {
        return TILE_TYPES.values().toArray(new TileType[TILE_TYPES.size()]);
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
        return RESOURCE_PATH + resourceName;
    }
}
