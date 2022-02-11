package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TileType extends GameResource {
    private static final String RESOURCE_PATH = "core/level/tiles/";
    private static final String TILE_IMPL_PREFIX = "net.reindiegames.re2d.core.level.tiles.";

    public static TileType GRASS;
    public static TileType WATER;
    public static TileType STONE_WALL;
    public static TileType COBBLESTONE;
    public static TileType MARKER;
    public static TileType MUSHROOM;

    public static final int NO_TILING = 0;
    public static final int COMPLETE_TILING = 1;
    public static final short[] TILING_VARIANTS = new short[2];
    public static final short[] DEFAULT_VARIANT = new short[2];

    private static final Map<Integer, TileType> ID_TILE_MAP = new HashMap<>();
    private static final Map<String, TileType> RESOURCE_TILE_MAP = new HashMap<>();

    static {
        TILING_VARIANTS[NO_TILING] = 1;
        TILING_VARIANTS[COMPLETE_TILING] = 30;

        DEFAULT_VARIANT[NO_TILING] = 0;
        DEFAULT_VARIANT[COMPLETE_TILING] = 11;
    }

    protected int tiling;
    protected short variants;
    protected short defaultVariant;
    protected boolean solid;
    protected Constructor constructor;

    private TileType(String resource) {
        super(resource);
        ID_TILE_MAP.put(id, this);
        RESOURCE_TILE_MAP.put(resource, this);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStatic(TileType.class, "GRASS", TileType.getByResource("grass.json"));
        ReflectionUtil.setStatic(TileType.class, "WATER", TileType.getByResource("water.json"));
        ReflectionUtil.setStatic(TileType.class, "STONE_WALL", TileType.getByResource("stone_wall.json"));
        ReflectionUtil.setStatic(TileType.class, "COBBLESTONE", TileType.getByResource("cobblestone.json"));
        ReflectionUtil.setStatic(TileType.class, "MARKER", TileType.getByResource("marker.json"));
        ReflectionUtil.setStatic(TileType.class, "MUSHROOM", TileType.getByResource("mushroom.json"));
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

    public short getVariants() {
        return variants;
    }

    public short getDefaultVariant() {
        return defaultVariant;
    }

    public boolean isSolid() {
        return solid;
    }

    public Tile newInstance(Level level, Chunk chunk, int tx, int ty) {
        if (constructor == null) {
            return new Tile(level, chunk, tx, ty, this);
        } else {
            try {
                return (Tile) constructor.newInstance(level, chunk, tx, ty);
            } catch (ReflectiveOperationException e) {
                Log.debug("Can not create Tile with Type '" + this.name + "'! Is the Implementation corrupt?!");
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void load(JsonObject source) {
        final JsonObject core = source.get("core").getAsJsonObject();
        this.tiling = core.get("tiling").getAsInt();

        this.defaultVariant = DEFAULT_VARIANT[tiling];
        this.solid = core.get("solid").getAsBoolean();

        if (core.has("implementation")) {
            try {
                Class clazz = Class.forName(TILE_IMPL_PREFIX + core.get("implementation").getAsString());
                this.constructor = clazz.getDeclaredConstructor(Level.class, Chunk.class, Integer.class, Integer.class);
                constructor.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } else {
            this.constructor = null;
        }

        final JsonObject client = source.get("client").getAsJsonObject();
        this.variants = (short) client.get("sprites").getAsJsonArray().size();
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }
}
