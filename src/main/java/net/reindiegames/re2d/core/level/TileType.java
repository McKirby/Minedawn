package net.reindiegames.re2d.core.level;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.io.IO;
import net.reindiegames.re2d.core.io.JsonResourceIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileType implements JsonResourceIO {
    public static final String TILE_TYPES_RESOURCE_PATH = "tiles/";
    private static final Map<Integer, TileType> TILE_TYPES = new HashMap<>();

    public final String resourceName;
    public final int id;
    public final String name;

    private TileType(String resourceName) {
        this.resourceName = resourceName;

        final JsonObject object = JsonResourceIO.loadObjectFromResource(this.getResourcePath());
        final JsonObject metaObject = object.get("meta").getAsJsonObject();
        this.id = metaObject.get("id").getAsInt();
        this.name = metaObject.get("name").getAsString();

        TILE_TYPES.put(id, this);
    }

    public static void loadAll() {
        final List<String> resources = IO.listResources(TILE_TYPES_RESOURCE_PATH);

        for (String resource : resources) {
            final TileType type = new TileType(resource);
            type.loadFromResource();
        }
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return TILE_TYPES_RESOURCE_PATH + resourceName;
    }

    @Override
    public String toString() {
        return "TileType{Resource = '" + resourceName + "', ID = " + id + ", Name = '" + name + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TileType) return ((TileType) o).id == this.id;
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
