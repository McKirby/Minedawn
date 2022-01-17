package net.reindiegames.re2d.core.level.entity;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;

import java.util.HashMap;
import java.util.Map;

public class EntityType extends GameResource {
    public static final String RESOURCE_PATH = "entities/";
    private static final Map<Integer, EntityType> ENTITY_TYPES = new HashMap<>();

    private EntityType(String resourceName) {
        super(resourceName);
        ENTITY_TYPES.put(id, this);
    }

    private static void link() {
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resourceName;
    }
}
